package jorgan.memory.exports;

import java.util.Iterator;

import jorgan.util.AbstractIterator;

public class Range implements Iterable<Integer> {

	public final int from;

	public final int to;

	public Range(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public Iterator<Integer> iterator() {
		if (from <= to) {
			return new Ascending();
		} else {
			return new Descending();
		}
	}

	private class Ascending extends AbstractIterator<Integer> {

		private int current = from - 1;

		public boolean hasNext() {
			return current < to;
		}

		public Integer next() {
			current++;
			return current;
		}
	}

	private class Descending extends AbstractIterator<Integer> {

		private int current = from + 1;

		public boolean hasNext() {
			return current > to;
		}

		public Integer next() {
			current--;
			return current;
		}
	}
}