import java.util.Comparator;
import java.util.Iterator;
import java.util.Queue;

import edu.uwm.cs351.util.CircularArrayPriorityQueue;
import junit.framework.TestCase;

public class TestCircularArrayPriorityQueue extends TestCase {

	Queue<String> self;
	
	private static Comparator<String> myComparator = new Comparator<String>() {
		public int compare(String s1, String s2) {
			if (s1 == null) s1 = "null";
			if (s2 == null) s2 = "null";
			return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		}
	};
	
	@Override // implementation
	public void setUp() {
		self = new CircularArrayPriorityQueue<>(myComparator);
	}
	
	public void test0() {
		assertEquals(0, self.size());
	}
	
	public void test1() {
		assertNull(self.peek());
		assertNull(self.poll());
	}
	
	public void test2() {
		self.add("Hello");
		assertEquals(1, self.size());
	}
	
	public void test3() {
		self.add("bread");
		self.add("apples");
		assertEquals("apples", self.peek());
		assertEquals("apples", self.poll());
	}
	
	public void test4() {
		Queue<Secret> q = new CircularArrayPriorityQueue<Secret>();
		q.add(new Secret("carrots"));
		q.add(new Secret("celery"));
		assertEquals("carrots", q.poll().value);
		assertEquals("celery", q.peek().value);
	}
	
	public void test5() {
		self = new CircularArrayPriorityQueue<>(null);
		self.add("A");
		self.add("b");
		self.add("C");
		
		assertEquals("A", self.remove());
		assertEquals("C", self.remove());
		assertEquals("b", self.remove());
		
		assertTrue(self.isEmpty());
	}
	
	public void test6() {
		self.add("Hello");
		self.add("and");
		self.add(null);
		self.add("world");
		
		assertEquals("and", self.poll());
		assertEquals("Hello", self.poll());
		assertEquals(null, self.poll());
		assertEquals("world", self.poll());
		
		assertNull(self.peek());
	}
	
	public void test7() {
		self.add("A");
		self.add("bb");
		self.add("CCC");
		self.add("ccc");
		self.add("BB");
		self.add("a");
		
		assertEquals("A", self.poll());
		assertEquals("a", self.poll());
		assertEquals("bb", self.poll());
		assertEquals("BB", self.poll());
		assertEquals("CCC", self.poll());
		assertEquals("ccc", self.poll());
	}
	
	public void test8() {
		self = new CircularArrayPriorityQueue<>((s1,s2) -> 0);
		self.add("To");
		self.add("be");
		self.add("or");
		self.add("not");
		self.add("to");
		self.add("be");
		self.add("that");
		self.add("is");
		self.add("the");
		self.add("question");
		
		Iterator<String> it = self.iterator();
		
		assertEquals("To", it.next());
		assertEquals("be", it.next());
		assertEquals("or", it.next());
		assertEquals("not", it.next());
		assertEquals("to", it.next());
		assertEquals("be", it.next());
		assertEquals("that", it.next());
		assertEquals("is", it.next());
		assertEquals("the", it.next());
		assertEquals("question", it.next());
	}
	
	public void test9() {
		String a1 = "A";
		String a2 = new String("A");
		self.add(a1);
		self.add("rose");
		self.add("is");
		self.add("a");
		self.add("Rose");
		self.add("IS");
		self.add(a2);
		self.add("ROSE");
		
		assertSame(a1, self.poll());
		assertEquals("a", self.poll());
		assertSame(a2, self.poll());
		assertEquals("is", self.poll());
		assertEquals("IS", self.poll());
		assertEquals("rose", self.poll());
		assertEquals("Rose", self.poll());
		assertEquals("ROSE", self.poll());
	}
	
	private class Secret implements Comparable<Secret> {
		final String value;
		Secret(String v) { value = v; }
		@Override // required
		public int compareTo(TestCircularArrayPriorityQueue.Secret arg) {
			return value.compareTo(arg.value);
		}
	}
}
