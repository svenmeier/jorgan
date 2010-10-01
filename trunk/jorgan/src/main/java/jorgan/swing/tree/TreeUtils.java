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
package jorgan.swing.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Utility method for trees.
 */
public class TreeUtils {

	/**
	 * Expand all nodes.
	 * 
	 * @param tree
	 */
	public static <T> void expand(JTree tree, T t) {
		TreePath path = getPath(tree, t);

		tree.expandPath(path);
	}

	@SuppressWarnings("unchecked")
	private static <T> TreePath getPath(JTree tree, T node) {
		return new TreePath(((SimpleTreeModel<T>) tree.getModel())
				.getPath(node));
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getSelection(JTree tree) {
		List<T> selection = new ArrayList<T>();

		TreePath[] paths = tree.getSelectionPaths();
		if (paths != null) {
			for (TreePath path : paths) {
				selection.add((T) path.getLastPathComponent());
			}
		}

		return selection;
	}

	public static <T> void addSelection(JTree tree, T t) {
		TreePath path = getPath(tree, t);

		tree.addSelectionPath(path);
	}

	public static <T> void scrollPathToVisible(JTree tree, T t) {
		TreePath path = getPath(tree, t);

		tree.scrollPathToVisible(path);
	}
}