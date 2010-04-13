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

import java.util.EventListener;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Abstract base class for custom tree models.
 */
public abstract class AbstractTreeModel implements TreeModel {

	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Get the parent of a node.
	 * 
	 * @param node
	 *            node to get parent for
	 * @return parent of node
	 */
	protected abstract Object getParent(Object node);

	/**
	 * Default convenience implementation does nothing.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	/**
	 * Invoke this method after you've changed how node is to be represented in
	 * the tree.
	 */
	public void nodeChanged(Object node) {
		if (listenerList != null && node != null) {
			Object parent = getParent(node);

			if (parent != null) {
				int anIndex = this.getIndexOfChild(parent, node);
				if (anIndex != -1) {
					int[] cIndexs = new int[1];

					cIndexs[0] = anIndex;
					nodesChanged(parent, cIndexs);
				}
			} else if (node == getRoot()) {
				nodesChanged(node, null);
			}
		}
	}

	/**
	 * Invoke this method after you've inserted some TreeNodes into node.
	 * childIndices should be the index of the new elements and must be sorted
	 * in ascending order.
	 */
	public void nodesWereInserted(Object node, int[] childIndices) {
		if (listenerList != null && node != null && childIndices != null
				&& childIndices.length > 0) {
			int cCount = childIndices.length;
			Object[] newChildren = new Object[cCount];

			for (int counter = 0; counter < cCount; counter++) {
				newChildren[counter] = getChild(node, childIndices[counter]);
			}
			fireTreeNodesInserted(this, getPathToRoot(node), childIndices,
					newChildren);
		}
	}

	/**
	 * Invoke this method after you've removed some TreeNodes from node.
	 * childIndices should be the index of the removed elements and must be
	 * sorted in ascending order. And removedChildren should be the array of the
	 * children objects that were removed.
	 */
	public void nodesWereRemoved(Object node, int[] childIndices,
			Object[] removedChildren) {
		if (node != null && childIndices != null) {
			fireTreeNodesRemoved(this, getPathToRoot(node), childIndices,
					removedChildren);
		}
	}

	/**
	 * Invoke this method after you've changed how the children identified by
	 * childIndicies are to be represented in the tree.
	 */
	public void nodesChanged(Object node, int[] childIndices) {
		if (node != null) {
			if (childIndices != null) {
				int cCount = childIndices.length;

				if (cCount > 0) {
					Object[] cChildren = new Object[cCount];

					for (int counter = 0; counter < cCount; counter++) {
						cChildren[counter] = getChild(node,
								childIndices[counter]);
					}
					fireTreeNodesChanged(this, getPathToRoot(node),
							childIndices, cChildren);
				}
			} else if (node == getRoot()) {
				fireTreeNodesChanged(this, getPathToRoot(node), null, null);
			}
		}
	}

	/**
	 * Invoke this method if you've exchanged the root.
	 */
	public void rootExchanged() {
		fireTreeStructureChanged(this, getPathToRoot(getRoot()), null, null);
	}

	/**
	 * Invoke this method if you've totally changed the children of node and its
	 * childrens children... This will post a treeStructureChanged event.
	 */
	public void nodeStructureChanged(Object node) {
		fireTreeStructureChanged(this, getPathToRoot(node), null, null);
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param aNode
	 *            the TreeNode to get the path for
	 */
	public Object[] getPathToRoot(Object aNode) {
		return getPathToRoot(aNode, 0);
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param aNode
	 *            the TreeNode to get the path for
	 * @param depth
	 *            an int giving the number of steps already taken towards the
	 *            root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node
	 */
	protected Object[] getPathToRoot(Object aNode, int depth) {
		Object[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/*
		 * Check for null, in case someone passed in a null node, or they passed
		 * in an element that isn't rooted at root.
		 */
		if (aNode == null) {
			return null;
		} else {
			depth++;
			if (aNode == getRoot()) {
				retNodes = new Object[depth];
			} else {
				retNodes = getPathToRoot(getParent(aNode), depth);
			}
			if (retNodes == null) {
				return null;
			}
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 * 
	 * @see #removeTreeModelListener
	 * @param l
	 *            the listener to add
	 */
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	/**
	 * Removes a listener previously added with <B>addTreeModelListener()</B>.
	 * 
	 * @see #addTreeModelListener
	 * @param l
	 *            the listener to remove
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	/*
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path,
			int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new TreeModelEvent(source, path == null ? null
							: new TreePath(path), childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

	/**
	 * Returns an array of all the objects currently registered as
	 * <code><em>Foo</em>Listener</code>s upon this model.
	 * <code><em>Foo</em>Listener</code>s are registered using the
	 * <code>add<em>Foo</em>Listener</code> method.
	 * 
	 * <p>
	 * 
	 * You can specify the <code>listenerType</code> argument with a class
	 * literal, such as <code><em>Foo</em>Listener.class</code>. For
	 * example, you can query a <code>DefaultTreeModel</code> <code>m</code>
	 * for its tree model listeners with the following code:
	 * 
	 * <pre>
	 * TreeModelListener[] tmls = (TreeModelListener[]) (m
	 * 		.getListeners(TreeModelListener.class));
	 * </pre>
	 * 
	 * If no such listeners exist, this method returns an empty array.
	 * 
	 * @param listenerType
	 *            the type of listeners requested; this parameter should specify
	 *            an interface that descends from
	 *            <code>java.util.EventListener</code>
	 * @return an array of all objects registered as
	 *         <code><em>Foo</em>Listener</code>s on this component, or an
	 *         empty array if no such listeners have been added
	 * @exception ClassCastException
	 *                if <code>listenerType</code> doesn't specify a class or
	 *                interface that implements
	 *                <code>java.util.EventListener</code>
	 * 
	 * @see #getTreeModelListeners()
	 * 
	 * @since 1.3
	 */
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return listenerList.getListeners(listenerType);
	}

	/**
	 * Returns an array of all the tree model listeners registered on this
	 * model.
	 * 
	 * @return all of this model's <code>TreeModelListener</code>s or an
	 *         empty array if no tree model listeners are currently registered
	 * 
	 * @see #addTreeModelListener
	 * @see #removeTreeModelListener
	 * 
	 * @since 1.4
	 */
	public TreeModelListener[] getTreeModelListeners() {
		return listenerList.getListeners(TreeModelListener.class);
	}
}