package edu.uwm.cs351.util;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * An implementation of priority queue in which elements
 * are kept in sorted order and we can insert at either end.
 */
public class CircularArrayPriorityQueue<E> extends AbstractQueue<E> {
	private static final int INITIAL_CAPACITY = 1;
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	
	/**
	 * Used to report an error found when checking the invariant.
	 * By providing a string, this will help debugging the class if the invariant should fail.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	@SuppressWarnings("unchecked")
	private E[] makeArray(int s) {
		return (E[]) new Object[s];
	}

	private E[] data;
	private Comparator<E> comparator;
	private int head; // index of first element in PQ (a legal index)
	private int rear; // index of space after last element in PQ (a legal index)
	private int version;

	private boolean wellFormed() {
		if (data == null) return report("data array is null");
		if (comparator == null) return report("comparator is null");
		if (head < 0 || head >= data.length) return report("head is bad: " + head);
		if (rear < 0 || rear >= data.length) return report("rear is bad: " + rear);
		E prev = null;
		for (int i=head; i != rear;) {
			if (i != head) {
				if (comparator.compare(prev,data[i]) > 0) return report("out of order: " + prev + " and " + data[i]);
			}
			prev = data[i];
			++i;
			if (i == data.length) i = 0;
		}
		return true;
	}
	
	/**
	 * Return whether the mid point is closer to the head than to the rear,
	 * with ties resolved in favor of the rear.
	 * @param mid mid index
	 * @return whether the mid index is closer to head than to rear
	 */
	private boolean inFirstHalf(int mid) {
		// TODO
		int distanceToHead = (mid - head + data.length) % data.length;
		int distanceToRear = (rear - mid + data.length) % data.length;
		return distanceToHead < distanceToRear;
	}

	// TODO: Body of class
	// "offer" is the most work
	//
	// The constructor taking a comparator may be annotated
	@SuppressWarnings("unchecked")
	public CircularArrayPriorityQueue(Comparator<E> comparator) {
		if (comparator == null) {
			comparator = (Comparator<E>)Comparator.naturalOrder();
		}
		this.data = makeArray(INITIAL_CAPACITY);
		this.version = 1;
		this.head = this.rear = 0;
		this.comparator = comparator;
	}

	/**
	 * Creates a new CircularArrayPriorityQueue object with a default Comparator<E>.
	 * The default Comparator<E> assumes that the values implement Comparable<E>.
	 */
	public CircularArrayPriorityQueue() {
		this(null);
	}

	@Override // required
	public Iterator<E> iterator() {
		assert wellFormed(): "invariant failed in iterator()";
		return new MyIterator();
	}

	@Override // required
	public int size() {
		assert wellFormed(): "invariant broke in size";
		return (rear - head + data.length) % data.length;
	}

	@Override // required
	public boolean offer(E e) {
		assert wellFormed(): "invariant failed at start of offer";
		// if(e == null) throw new NullPointerException();

		// Ensure capacity
		if ((rear+1)%data.length == head) resize();
		int insertIndex = binarySearch(e);

		if(inFirstHalf(insertIndex)) {
			shiftElementsLeft(head, insertIndex);
			data[(insertIndex - 1 + data.length) % data.length] = e;
			head = (head - 1 + data.length) % data.length;
		}
		else {
			shiftElementsRight(insertIndex, rear);
			data[insertIndex] = e;
			rear = (rear + 1) % data.length;
		}

		version++;
		assert wellFormed(): "invariant broke by offer";
		return true;
	}

	@Override // required
	public E poll() {
		assert wellFormed(): "invariant broke in poll";
		E result = null;

		if (head != rear) {
			result = data[head];
			data[head] = null;
			head = (head + 1) % data.length;
			version++;
		}
		assert wellFormed(): "invariant broke by poll";
		return result;
	}

	@Override // required
	public E peek() {
		assert wellFormed(): "invariant broke in peek";
		return data[head];
	}

	/**
	 * Resize the array to double its current capacity.
	 */
	private void resize() {
		int newSize = data.length * 2;
		E[] newData = makeArray(newSize);

		// Copy elements to the new array
		int i = head;
		int j = 0;
		while (i != rear) {
			newData[j++] = data[i];
			i = (i + 1) % data.length;
		}

		// Update head, rear and data
		head = 0;
		rear = j;
		data = newData;
	}

	/**
	 * Perform binary search to find the index where the new element should be inserted.
	 *
	 * @param e The element to be inserted.
	 * @return The index where the new element should be inserted.
	 */
	private int binarySearch(E e) {
		int low = head;
		int high = rear >= head ? rear : rear + data.length;

		while (low < high) {
			int mid = (low + high) >>> 1;
			if (comparator.compare(data[mid % data.length], e) > 0) {
				high = mid;
			} else {
				low = mid + 1;
			}
		}

		return high % data.length;
	}

	/**
	 * Shifts elements to the right within the specified range in the underlying data array.
	 *
	 * @param start The starting index of the range (inclusive).
	 * @param end   The ending index of the range (exclusive).
	 */
	private void shiftElementsRight(int start, int end) {
		for (int i = end; i != start; i = (i - 1 + data.length) % data.length) {
			data[i] = data[(i - 1 + data.length) % data.length];
		}
	}

	/**
	 * Shifts elements to the left within the specified range in the underlying data array.
	 *
	 * @param start The starting index of the range (inclusive).
	 * @param end   The ending index of the range (exclusive).
	 */
	private void shiftElementsLeft(int start, int end) {
		for (int i = start; i != end; i = (i + 1) % data.length) {
			data[(i - 1 + data.length) % data.length] = data[i];
		}
	}
	
	private class MyIterator implements Iterator<E> {
		private int current = head;
		private boolean canRemove = false;
		private int colVersion = version;
		
		/// Data structure Design:
		// current is index of current element (if canRemove is true)
		// otherwise there is no current and "current" is the index of the next element.
		// It must always be between head and rear (inclusive) sees as indices in a circular array.
		// If current == rear then there is no current element (why not?)
		// and no next element either (why not?)
		
		private boolean wellFormed() {
			if (!CircularArrayPriorityQueue.this.wellFormed()) return false;
			if (version != colVersion) return true;
			if (current < 0 || current >= data.length) return report("current is bad index: " + current);
			if (head <= rear) {
				if (current < head || current > rear) return report("current is out of range: " + current + " not in [" + head + "," + rear +")");
			} else {
				if (current < head && current > rear) return report("current is out of range: " + current + " not in [" + head + "," + rear + ")"); 
			}
			if (canRemove) {
				if (rear == current) return report("canRemove but current == rear");
			}
			return true;
		}

		// TODO: Body of iterator class. "remove" is the most work
		@Override // required
		public boolean hasNext() {
			assert wellFormed(): "invariant of iterator failed in hasNext";
			if(version != colVersion) throw new ConcurrentModificationException();
			if(canRemove && (current + 1) % data.length == rear) return false;
			return canRemove || current != rear;
		}

		@Override // required
		public E next() {
			if (!hasNext()) throw new NoSuchElementException();
			if(canRemove) current = (current + 1) % data.length;
			E nextElement = data[current];
			canRemove = current != rear;
			assert wellFormed(): "invariant of iterator failed at end of next";
			return nextElement;
		}

		@Override // implementation
		public void remove() {
			Iterator.super.remove();
		}
	}
	
}
