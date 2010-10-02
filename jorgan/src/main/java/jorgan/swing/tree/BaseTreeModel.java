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
import java.util.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jorgan.util.ArrayUtils;

/**
 * Abstract base class for custom tree models.
 */
public abstract class BaseTreeModel<T> implements TreeModel {

	public static final Object ROOT = new Object();

	private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@SuppressWarnings("unchecked")
	public final void valueForPathChanged(TreePath path, Object newValue) {
		setValue((T) path.getLastPathComponent(), newValue);
	}

	@Override
	public final Object getRoot() {
		return ROOT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final int getChildCount(Object parent) {
		if (parent == ROOT) {
			return getRoots().size();
		}
		return getChildren((T) parent).size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Object getChild(Object parent, int index) {
		if (parent == ROOT) {
			return getRoots().get(index);
		}
		return getChildren((T) parent).get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final int getIndexOfChild(Object parent, Object child) {
		if (parent == ROOT) {
			return getRoots().indexOf(child);
		}

		return getChildren((T) parent).indexOf(child);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean isLeaf(Object node) {
		if (node == ROOT) {
			return false;
		}

		return !hasChildren((T) node);
	}

	public abstract boolean hasChildren(T node);

	protected abstract List<T> getRoots();

	protected abstract List<T> getChildren(T parent);

	protected abstract Set<T> getParents(T node);

	/**
	 * Default convenience implementation does nothing.
	 */
	protected void setValue(T node, Object newValue) {
	}

	public final void fireRootsChanged() {
		TreePath path = new TreePath(ROOT);

		notifyStructureChanged(new TreeModelEvent(this, path));
	}

	public final void fireNodeChanged(T node) {
		for (T parent : getParents(node)) {
			if (parent == null) {
				int index = getRoots().indexOf(node);

				notifyNodesChanged(new TreeModelEvent(this, new TreePath(ROOT),
						new int[] { index }, new Object[] { node }));
			} else {
				int index = getChildren(parent).indexOf(node);

				for (TreePath path : getPaths(parent)) {
					notifyNodesChanged(new TreeModelEvent(this, path,
							new int[] { index }, new Object[] { node }));
				}
			}
		}
	}

	public final void fireChildrenChanged(T node) {
		for (TreePath path : getPaths(node)) {
			notifyStructureChanged(new TreeModelEvent(this, path));
		}
	}

	private void notifyNodesChanged(TreeModelEvent event) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesChanged(event);
		}
	}

	private void notifyStructureChanged(TreeModelEvent event) {
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(event);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TreePath> getPaths(T node) {
		List<TreePath> finished = new ArrayList<TreePath>();
		List<Object[]> todo = new ArrayList<Object[]>();

		todo.add(new Object[] { node });

		while (!todo.isEmpty()) {
			Object[] path = todo.remove(0);

			Set<T> parents = getParents((T) path[0]);
			if (parents.isEmpty()) {
				finished.add(new TreePath(ArrayUtils.prepend(ROOT, path)));
			} else {
				for (T parent : parents) {
					todo.add(ArrayUtils.prepend(parent, path));
				}
			}
		}

		return finished;
	}
}