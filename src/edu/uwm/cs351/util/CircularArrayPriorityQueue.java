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
		return null;
	}

	@Override // required
	public int size() {
		return 0;
	}

	@Override // required
	public boolean offer(E e) {
		return false;
	}

	@Override // required
	public E poll() {
		return null;
	}

	@Override // required
	public E peek() {
		return null;
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
			return false;
		}

		@Override // required
		public E next() {
			return null;
		}

		@Override // implementation
		public void remove() {
			Iterator.super.remove();
		}
	}
	
}
