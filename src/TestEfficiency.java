import java.util.Iterator;
import java.util.Random;

import edu.uwm.cs351.util.CircularArrayPriorityQueue;
import junit.framework.TestCase;

public class TestEfficiency extends TestCase {

	private CircularArrayPriorityQueue<Integer> self = new CircularArrayPriorityQueue<>();
	
	private static final int POWER = 20;
	private static final int MAX = 1 << POWER;
	
	@Override // implementation
	public void setUp() {
		try {
			assert self.iterator().next() == 42;
			assertTrue(true);
		} catch (NullPointerException ex) {
			System.err.println("You must disable assertions to run this test.");
			System.err.println("Go to Run > Run Configurations. Select the 'Arguments' tab");
			System.err.println("Then remove '-ea' from the VM Arguments box.");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}

		self = new CircularArrayPriorityQueue<>();
	}
	
	public void testA() {
		Random r = new Random();
		for (int i=0; i < POWER; ++i) {
			assertTrue(self.offer(i));
		}
		for (int i=0; i < MAX; ++i) {
			assertTrue(self.offer(r.nextInt(POWER)));
			assertTrue(self.remove() < POWER);
		}
		assertEquals(POWER, self.size());
	}

	public void testB() {
		for (int i=1; i < MAX; ++i) {
			assertTrue(self.offer(i));
		}
		assertEquals(MAX-1, self.size());
		for (int i=1; i < MAX; ++i) {
			assertEquals(i, self.poll().intValue());
		}
	}
	
	public void testC() {
		for (int i=0; i < POWER; ++i) {
			assertTrue(self.offer(i+MAX));
		}
		for (int i=0; i < MAX/POWER; ++i) {
			assertTrue(self.offer(i));
		}
		assertEquals(MAX/POWER + POWER, self.size());
		for (int i=0; i < MAX/POWER; ++i) {
			assertEquals(i, self.remove().intValue());
		}
		for (int i=0; i < POWER; ++i) {
			assertEquals(i+MAX, self.poll().intValue());
		}
	}
	
	public void testD() {
		for (int i=0; i < POWER; ++i) {
			assertTrue(self.offer(i));
		}
		for (int i=MAX/POWER; i > 0; --i) {
			assertTrue(self.offer(i+POWER-1));
		}
		assertEquals(MAX/POWER + POWER, self.size());
		for (int i=0; i < POWER; ++i) {
			assertEquals(i, self.poll().intValue());
		}
		for (int i=0; i < MAX/POWER; ++i) {
			assertEquals(i+POWER, self.remove().intValue());
		}
	}

	public void testE() {
		for (int i=0; i <MAX; ++i) {
			assertTrue(self.offer(i));
		}
		Iterator<Integer> it = self.iterator();
		for (int i=0; i < MAX; ++i) {
			assertEquals(i, it.next().intValue());
		}
	}
	
	public void testF() {
		for (int i=0; i <MAX; ++i) {
			assertTrue(self.offer(i));
		}
		Iterator<Integer> it = self.iterator();
		for (int i=0; i < POWER; ++i) {
			assertEquals(i, it.next().intValue());
		}
		for (int i = 0; i < MAX/POWER; ++i) {
			assertEquals(i+POWER, it.next().intValue());
			it.remove();
		}
	}
	
	public void testG() {
		for (int i=0; i < MAX; ++i) {
			assertTrue(self.offer(i));
		}
		Iterator<Integer> it = self.iterator();
		for (int i=0; i < MAX-POWER*POWER; ++i) {
			it.next();
		}
		for (int i = MAX-POWER*POWER; i < MAX; ++i) {
			assertEquals(i, it.next().intValue());
			it.remove();
		}
	}
}
