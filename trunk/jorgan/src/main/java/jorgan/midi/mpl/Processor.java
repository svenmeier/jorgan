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
package jorgan.midi.mpl;

/**
 * A processor for expressions in the <em>Midi Processing Language</em> (MPL).
 */
public class Processor {

	private transient Node root;

	public Processor(String term) throws ProcessingException {
		try {
			root = createNodes(term);
		} catch (Exception ex) {
			throw new ProcessingException(term, ex);
		}
	}

	private Node createNodes(String term) throws Exception {

		Node next = null;
		int gt = term.indexOf('|');
		if (gt != -1) {
			next = createNodes(term.substring(gt + 1).trim());
			term = term.substring(0, gt);
		}

		Node node = createNode(term);
		node.setNext(next);
		return node;
	}

	private Node createNode(String term) throws Exception {
		if (term.length() == 0) {
			return new NoOp();
		}

		int space = term.indexOf(' ');
		String type = term.substring(0, space).trim();
		term = term.substring(space + 1).trim();
		if ("filter".equals(type)) {
			return new Filter(term);
		} else if ("set".equals(type)) {
			return new Set(term);
		} else if ("get".equals(type)) {
			return new Get(term);
		} else if ("add".equals(type)) {
			return new Add(term);
		} else if ("sub".equals(type)) {
			return new Sub(term);
		} else if ("div".equals(type)) {
			return new Div(term);
		} else if ("mult".equals(type)) {
			return new Mult(term);
		} else {
			throw new IllegalArgumentException("unkown type '" + type + "'");
		}
	}

	public float process(float value, Context context) {
		return root.process(value, context);
	}

	private abstract class Node {
		private Node next;

		public void setNext(Node node) {
			this.next = node;
		}

		public final float process(float value, Context context) {
			float f = processImpl(value, context);
			if (!Float.isNaN(f) && next != null) {
				f = next.process(f, context);
			}
			return f;
		}

		public abstract float processImpl(float value, Context context);
	}

	private class NoOp extends Node {

		public NoOp() {
		}

		@Override
		public float processImpl(float value, Context context) {
			return value;
		}
	}

	private class Set extends Node {

		private String name;

		public Set(String term) {
			this.name = term;
		}

		@Override
		public float processImpl(float value, Context context) {
			context.set(name, value);
			return value;
		}
	}

	private abstract class ValueNode extends Node {

		private String name;

		private float value = Float.NaN;

		protected ValueNode(String term) throws Exception {
			int space = term.indexOf(' ');
			if (space == -1) {
				if (Character.isDigit(term.charAt(0))) {
					value = Float.parseFloat(term);
				} else {
					name = term;
				}
			} else {
				name = term.substring(0, space);
				value = Float.parseFloat(term.substring(space + 1));
			}
		}

		protected float getValue(Context context) {
			float value = Float.NaN;
			if (name != null) {
				value = context.get(name);
			}
			if (Float.isNaN(value)) {
				value = this.value;
			}
			return value;
		}
	}

	private class Get extends ValueNode {

		public Get(String term) throws Exception {
			super(term);
		}

		@Override
		public float processImpl(float value, Context context) {
			return super.getValue(context);
		}
	}

	private class Add extends ValueNode {

		public Add(String term) throws Exception {
			super(term);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value + getValue(context);
		}
	}

	private class Sub extends ValueNode {

		public Sub(String term) throws Exception {
			super(term);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value - getValue(context);
		}
	}

	private class Div extends ValueNode {

		public Div(String term) throws Exception {
			super(term);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value / getValue(context);
		}
	}

	private class Mult extends ValueNode {

		public Mult(String term) throws Exception {
			super(term);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value * getValue(context);
		}
	}

	private class Filter extends Node {

		private float from;

		private float to;

		public Filter(String term) throws Exception {

			int hyphen = term.indexOf('-');
			if (hyphen == -1) {
				this.from = Float.parseFloat(term);
				this.to = this.from;
			} else {
				this.from = Float.parseFloat(term.substring(0, hyphen).trim());
				this.to = Float.parseFloat(term.substring(hyphen + 1).trim());
			}
		}

		@Override
		public float processImpl(float value, Context context) {
			if (value < Math.min(from, to)) {
				return Float.NaN;
			}
			if (value > Math.max(from, to)) {
				return Float.NaN;
			}

			return value;
		}
	}

	public static interface Context {
		public void set(String name, float value);

		public float get(String name);
	}
}