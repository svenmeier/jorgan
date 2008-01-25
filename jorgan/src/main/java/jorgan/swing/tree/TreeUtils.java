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
	public static void expand(JTree tree) {
		expand(tree, Integer.MAX_VALUE);
	}

	/**
	 * Expand all nodes of given level.
	 * 
	 * @param tree
	 * @param levels
	 */
	public static void expand(JTree tree, int levels) {
		if (levels > 0) {
			expand(tree, new TreePath(tree.getModel().getRoot()), levels);
		}
	}

	/**
	 * Expand all children of the given path of given level.
	 * 
	 * @param tree
	 * @param path
	 * @param levels
	 */
	public static void expand(JTree tree, TreePath path, int levels) {
		if (levels > 0) {
			tree.expandPath(path);

			Object parent = path.getLastPathComponent();
			for (int c = 0; c < tree.getModel().getChildCount(parent); c++) {
				Object child = tree.getModel().getChild(parent, c);
				if (!tree.getModel().isLeaf(child)) {
					expand(tree, path.pathByAddingChild(child), levels - 1);
				}
			}
		}
	}
}