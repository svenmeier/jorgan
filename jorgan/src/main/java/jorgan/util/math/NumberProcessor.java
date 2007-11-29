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
package jorgan.util.math;

/**
 * A processor of numbers.
 */
public class NumberProcessor {

	private transient Node root;

	public NumberProcessor(String pattern) throws ProcessingException {
		try {
			root = createNodes(pattern);
		} catch (Exception ex) {
			throw new ProcessingException(pattern, ex);
		}
	}

	private Node createNodes(String pattern) throws Exception {

		Node next = null;
		int gt = pattern.indexOf('|');
		if (gt != -1) {
			next = createNodes(pattern.substring(gt + 1).trim());
			pattern = pattern.substring(0, gt);
		}

		Node node = createNode(pattern);
		node.setNext(next);
		return node;
	}

	private Node createNode(String pattern) throws Exception {
		if (pattern.length() == 0) {
			return new IdentityNode();
		}

		int space = pattern.indexOf(' ');
		if (space == -1) {
			return new ConstantNode(pattern);
		}

		String type = pattern.substring(0, space).trim();
		String parameter = pattern.substring(space + 1).trim();
		if ("set".equals(type)) {
			return new SetNode(parameter);
		} else if ("get".equals(type)) {
			return new GetNode(parameter);
		} else if ("filter".equals(type)) {
			return new FilterNode(parameter);
		} else if ("add".equals(type)) {
			return new AddNode(parameter);
		} else if ("sub".equals(type)) {
			return new SubNode(parameter);
		} else if ("div".equals(type)) {
			return new DivNode(parameter);
		} else if ("mult".equals(type)) {
			return new MultNode(parameter);
		} else {
			throw new IllegalArgumentException("unkown type");
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

	private class IdentityNode extends Node {

		public IdentityNode() {
		}

		@Override
		public float processImpl(float value, Context context) {
			return value;
		}
	}

	private class SetNode extends Node {

		private String name;

		public SetNode(String name) throws Exception {
			this.name = name;
		}

		@Override
		public float processImpl(float value, Context context) {
			context.set(name, value);

			return value;
		}
	}

	private class GetNode extends Node {

		private String name;
		private float defaultValue;

		public GetNode(String name) throws Exception {
			int space = name.indexOf(' ');
			if (space != -1) {
				defaultValue = Float.parseFloat(name.substring(space + 1));
				name = name.substring(0, space);
			}
			this.name = name;
		}

		@Override
		public float processImpl(float value, Context context) {
			value = context.get(name);
			if (Float.isNaN(value)) {
				value = this.defaultValue;
			}
			return value;
		}
	}

	private class ConstantNode extends Node {

		private float value;

		public ConstantNode(String value) throws Exception {
			this.value = Float.parseFloat(value);
		}

		@Override
		public float processImpl(float value, Context context) {
			return this.value;
		}
	}

	private class FilterNode extends Node {

		private float from;

		private float to;

		public FilterNode(String range) throws Exception {

			int hyphen = range.indexOf('-');
			if (hyphen == -1) {
				this.from = Float.parseFloat(range);
				this.to = this.from;
			} else {
				this.from = Float.parseFloat(range.substring(0, hyphen).trim());
				this.to = Float.parseFloat(range.substring(hyphen + 1).trim());
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

	private class AddNode extends Node {

		private float value;

		public AddNode(String value) throws Exception {

			this.value = Float.parseFloat(value);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value + this.value;
		}
	}

	private class SubNode extends Node {

		private float value;

		public SubNode(String value) throws Exception {

			this.value = Float.parseFloat(value);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value - this.value;
		}
	}

	private class DivNode extends Node {

		private float value;

		public DivNode(String value) throws Exception {

			this.value = Float.parseFloat(value);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value / this.value;
		}
	}

	private class MultNode extends Node {

		private float value;

		public MultNode(String value) throws Exception {

			this.value = Float.parseFloat(value);
		}

		@Override
		public float processImpl(float value, Context context) {
			return value * this.value;
		}
	}

	public static interface Context {
		public void set(String name, float value);
		
		public float get(String name);
	}
}