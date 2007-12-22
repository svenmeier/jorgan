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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import jorgan.midi.DevicePool;
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

	private DefaultMutableTreeNode inRoot;

	private DefaultMutableTreeNode outRoot;

	/**
	 * Create this panel.
	 */
	public DeviceSelectionPanel() {
		setLayout(new BorderLayout());

		inRoot = new DefaultMutableTreeNode(config.get("inRoot").read(
				new MessageBuilder()).build());
		outRoot = new DefaultMutableTreeNode(config.get("outRoot").read(
				new MessageBuilder()).build());

		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.setEditable(false);
		tree.setModel(createModel());
		tree.expandPath(new TreePath(inRoot.getPath()));
		tree.expandPath(new TreePath(outRoot.getPath()));
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	protected TreeModel createModel() {
		root.add(inRoot);
		root.add(outRoot);

		String[] inDevices = DevicePool.getMidiDeviceNames(DevicePool.IN);
		for (int i = 0; i < inDevices.length; i++) {
			inRoot.add(new DefaultMutableTreeNode(inDevices[i]));
		}

		String[] outDevices = DevicePool.getMidiDeviceNames(DevicePool.OUT);
		for (int o = 0; o < outDevices.length; o++) {
			outRoot.add(new DefaultMutableTreeNode(outDevices[o]));
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
	public void setDevice(String name, int direction) {
		TreeNode[] path = getPath(name, direction);
		if (path == null) {
			tree.clearSelection();
		} else {
			tree.setSelectionPath(new TreePath(path));
		}
	}

	protected TreeNode[] getPath(String name, int direction) {
		TreeNode parent = (direction == DevicePool.OUT) ? this.outRoot
				: this.inRoot;
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
	public int getDeviceDirection() {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths.length == 1) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
					.getLastPathComponent();
			if (node.getParent() == inRoot) {
				return DevicePool.IN;
			} else {
				return DevicePool.OUT;
			}
		}

		return -1;
	}

	/**
	 * Get the name of the selected device.
	 * 
	 * @return name
	 */
	public String getDeviceName() {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths.length == 1) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
					.getLastPathComponent();
			if (node != inRoot && node != outRoot) {
				return (String) node.getUserObject();
			}
		}

		return null;
	}
}