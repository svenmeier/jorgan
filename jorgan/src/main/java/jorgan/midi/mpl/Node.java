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

public abstract class Node {
	
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

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}
	
	private void toString(StringBuffer buffer) {
		buffer.append(typeToString(getClass()));
		buffer.append(" ");
		buffer.append(getArguments());

		if (next != null) {
			buffer.append(" | ");
			next.toString(buffer);
		}
	}
	
	protected abstract String getArguments();	

	public static Node create(String term) throws ProcessingException {
		try {
			return createNodes(term.trim());
		} catch (Exception ex) {
			throw new ProcessingException(term, ex);
		}
	}

	private static Node createNodes(String terms) throws Exception {

		Node node;
		
		int pipe = terms.indexOf('|');
		if (pipe == -1) {
			node = createNode(terms.trim());
		} else {
			node = createNode(terms.substring(0, pipe).trim());

			node.setNext(createNodes(terms.substring(pipe + 1).trim()));
		}
		return node;
	}

	private static Node createNode(String term) throws Exception {
		if (term.length() == 0) {
			return new NoOp();
		}

		int space = term.indexOf(' ');

		Class<?> type = Node.stringToType(term.substring(0, space).trim());
		String arguments = term.substring(space + 1).trim();

		return (Node)type.getDeclaredConstructor(new Class<?>[]{String.class}).newInstance(arguments);
	}
	
	private static Class<?> stringToType(String string)
			throws ClassNotFoundException {
		String simpleName = Character.toUpperCase(string.charAt(0))
				+ string.substring(1);

		return Class.forName(Node.class.getPackage().getName() + "." + simpleName);
	}

	private static String typeToString(Class<?> type) {
		String simpleName = type.getSimpleName();

		return Character.toLowerCase(simpleName.charAt(0))
				+ simpleName.substring(1);
	}
}