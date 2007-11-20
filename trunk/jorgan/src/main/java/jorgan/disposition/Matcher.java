/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.disposition;

import java.lang.reflect.Field;
import java.util.StringTokenizer;

/**
 * A matcher of nodes
 */
public class Matcher {

	private transient Node[] nodes;

	private String pattern;

	public Matcher pattern(String pattern) {
		setPattern(pattern);
		return this;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;

		nodes = null;
	}

	public void init(int length) throws MatcherException {
		if (nodes == null) {
			try {
				StringTokenizer tokens = new StringTokenizer(pattern, ",");

				nodes = new Node[tokens.countTokens()];
				for (int n = 0; n < nodes.length; n++) {
					nodes[n] = createNode(tokens.nextToken().trim());
				}
			} catch (Exception ex) {
				nodes = null;
			}

			if (nodes == null || nodes.length != length) {
				throw new MatcherException(pattern);
			}
		}
	}

	private Node createNode(String pattern) throws Exception {

		int colon = pattern.indexOf(':');
		if (colon != -1) {
			return createNode(pattern.substring(colon + 1).trim()).name(
					pattern.substring(0, colon).trim());
		}

		int hyphen = pattern.indexOf('-');
		if (hyphen != -1) {
			return new Range(Integer.parseInt(pattern.substring(0, hyphen)
					.trim()), Integer.parseInt(pattern.substring(hyphen + 1)
					.trim()));
		}

		return new Constant(Integer.parseInt(pattern));
	}

	/**
	 * Input this message.
	 * 
	 * @param data
	 *            data to read input from
	 * @param context
	 *            context to set values to
	 * @return <code>true</code> if message was matched
	 */
	public boolean input(int[] data) throws MatcherException {
		init(data.length);

		for (int n = 0; n < nodes.length; n++) {
			if (!nodes[n].input(data[n])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Output this message.
	 * 
	 * @param data
	 *            data to write output to
	 * @param context
	 *            context to get values from
	 */
	public void output(int[] data) throws MatcherException {
		init(data.length);

		for (int n = 0; n < nodes.length; n++) {
			data[n] = nodes[n].output();
		}
	}

	private Field getField(String name) throws Exception {
		Field field = getClass().getDeclaredField(name);

		if (field.getType() != Integer.TYPE && field.getType() != Float.TYPE) {
			throw new IllegalArgumentException(name);
		}

		return field;
	}

	/**
	 * A node of a message.
	 */
	private abstract class Node {

		protected Field field;

		/**
		 * Set an optional name.
		 * 
		 * @param name
		 *            name
		 */
		public Node name(String name) throws Exception {
			this.field = getField(name);

			return this;
		}

		/**
		 * Set the given value.
		 * 
		 * @param value
		 *            value to set
		 */
		protected void setValue(Number value) {
			try {
				field.set(Matcher.this, value);
			} catch (Exception ex) {
				throw new Error(ex);
			}
		}

		/**
		 * Get the value.
		 * 
		 * @param context
		 *            context to get from
		 * @return value
		 */
		protected Number getValue() {
			try {
				return (Number) field.get(Matcher.this);
			} catch (Exception ex) {
				throw new Error(ex);
			}
		}

		/**
		 * Read input.
		 * 
		 * @param data
		 *            data to read
		 * @return <code>true</code> if data matched
		 */
		public abstract boolean input(int data);

		/**
		 * Write output.
		 * 
		 * @return data
		 */
		public abstract int output();
	}

	/**
	 * A constant field.
	 */
	private class Constant extends Node {

		private int data;

		public Constant(int data) {
			this.data = data;
		}

		public boolean input(int data) {
			if (data != this.data) {
				return false;
			}

			if (field != null) {
				if (field.getType() == Integer.TYPE) {
					setValue(this.data);
				} else if (field.getType() == Float.TYPE) {
					setValue(1.0f);
				} else {
					throw new Error("illegal type '" + field.getType() + "'");
				}
			}

			return true;
		}

		public int output() {
			return data;
		}
	}

	private class Range extends Node {

		private int from;

		private int to;

		public Range(int from, int to) {

			this.from = from;
			this.to = to;
		}

		public boolean input(int data) {
			if (data < Math.min(from, to)) {
				return false;
			}
			if (data > Math.max(from, to)) {
				return false;
			}

			if (field != null) {
				if (field.getType() == Integer.TYPE) {
					setValue(data);
				} else if (field.getType() == Float.TYPE) {
					setValue((float) (data - from) / (to - from));
				} else {
					throw new Error("illegal type '" + field.getType() + "'");
				}
			}

			return true;
		}

		@Override
		public int output() {
			int data;

			Number value = getValue();

			if (field == null) {
				throw new IllegalStateException("no field");
			} else {
				if (field.getType() == Integer.TYPE) {
					data = value.intValue();
				} else if (field.getType() == Float.TYPE) {
					data = from + Math.round((to - from) * value.floatValue());
				} else {
					throw new Error("illegal type '" + field.getType() + "'");
				}
				
				if (data < Math.min(from, to)) {
					data = Math.min(from, to);
				}
				if (data > Math.max(from, to)) {
					data = Math.max(from, to);
				}
			}

			return data;
		}
	}
}