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
			root = createNodes(term.trim());
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
		String type = Character.toUpperCase(term.charAt(0)) + term.substring(1, space).trim();
		term = term.substring(space + 1).trim();
		
		Class clazz = Class.forName(getClass().getPackage().getName() + ".node." + type);

		return (Node)clazz.getConstructor(new Class[]{String.class}).newInstance(term);
	}

	public float process(float value, Context context) {
		return root.process(value, context);
	}

	public static abstract class Node {
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
}