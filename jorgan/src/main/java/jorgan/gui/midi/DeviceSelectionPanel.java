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
package jorgan.gui.midi;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.swing.tree.TreeUtils;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * A panel to select a MIDI device.
 */
public class DeviceSelectionPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			DeviceSelectionPanel.class);

	private JTree tree = new JTree();

	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	private Map<Direction, DefaultMutableTreeNode> roots = new HashMap<Direction, DefaultMutableTreeNode>();

	/**
	 * Create this panel.
	 */
	public DeviceSelectionPanel() {
		setLayout(new BorderLayout());

		createRoot(Direction.IN);
		createRoot(Direction.OUT);

		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.setEditable(false);
		tree.setModel(createModel());
		TreeUtils.expand(tree);
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	private void createRoot(Direction direction) {
		roots.put(direction, new DefaultMutableTreeNode(config.get(direction.name())
				.read(new MessageBuilder()).build()));
	}
	
	protected TreeModel createModel() {
		root.add(roots.get(Direction.IN));
		root.add(roots.get(Direction.OUT));

		String[] inDevices = DevicePool.instance().getMidiDeviceNames(
				Direction.IN);
		for (int i = 0; i < inDevices.length; i++) {
			roots.get(Direction.IN).add(
					new DefaultMutableTreeNode(inDevices[i]));
		}

		String[] outDevices = DevicePool.instance().getMidiDeviceNames(
				Direction.OUT);
		for (int o = 0; o < outDevices.length; o++) {
			roots.get(Direction.OUT).add(
					new DefaultMutableTreeNode(outDevices[o]));
		}

		return new DefaultTreeModel(root);
	}

	/**
	 * Set the selected device.
	 * 
	 * @param name
	 *            name of device
	 * @param out
	 *            is the device selected for <code>out</code> of
	 *            <code>in</code>
	 */
	public void setDevice(String name, Direction direction) {
		if (name != null && direction != null) {
			TreeNode[] path = getPath(name, direction);
			if (path != null) {
				tree.setSelectionPath(new TreePath(path));
				return;
			}
		}
		tree.clearSelection();
	}

	protected TreeNode[] getPath(String name, Direction direction) {
		TreeNode parent = roots.get(direction);
		for (int n = 0; n < parent.getChildCount(); n++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent
					.getChildAt(n);
			if (child.getUserObject().equals(name)) {
				return child.getPath();
			}
		}
		return null;
	}

	/**
	 * Is the device selected for {@link DevicePool#OUT} or
	 * {@link DevicePool#IN}.
	 */
	public Direction getDeviceDirection() {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths != null && paths.length == 1) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
					.getLastPathComponent();
			if (node.getParent() == roots.get(Direction.IN)) {
				return Direction.IN;
			} else {
				return Direction.OUT;
			}
		}

		return null;
	}

	/**
	 * Get the name of the selected device.
	 * 
	 * @return name
	 */
	public String getDeviceName() {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths != null && paths.length == 1) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
					.getLastPathComponent();
			if (!roots.containsValue(node)) {
				return (String) node.getUserObject();
			}
		}

		return null;
	}
}