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
import javax.swing.tree.TreePath;

import jorgan.midi.DevicePool;
import jorgan.swing.tree.CheckedTreeCell;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * A panel to select a MIDI device.
 */
public class DeviceSelectionPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			DeviceSelectionPanel.class);

	private JTree deviceTree = new JTree();

	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	private DefaultMutableTreeNode inRoot;

	private DefaultMutableTreeNode outRoot;

	private String deviceName = null;

	private int direction;

	/**
	 * Create this panel.
	 */
	public DeviceSelectionPanel() {
		setLayout(new BorderLayout());

		inRoot = new DefaultMutableTreeNode(config.get("inRoot").read(
				new MessageBuilder()).build());
		outRoot = new DefaultMutableTreeNode(config.get("outRoot").read(
				new MessageBuilder()).build());

		deviceTree.setShowsRootHandles(true);
		deviceTree.setRootVisible(false);
		deviceTree.setEditable(true);
		deviceTree.setCellRenderer(new MidiDeviceCell());
		deviceTree.setCellEditor(new MidiDeviceCell());
		deviceTree.setModel(createModel());
		deviceTree.expandPath(new TreePath(inRoot.getPath()));
		deviceTree.expandPath(new TreePath(outRoot.getPath()));
		add(new JScrollPane(deviceTree), BorderLayout.CENTER);
	}

	protected TreeModel createModel() {
		root.add(inRoot);
		root.add(outRoot);

		String[] inDevices = DevicePool.getMidiDeviceNames(DevicePool.IN);
		for (int i = 0; i < inDevices.length; i++) {
			inRoot.add(new DefaultMutableTreeNode(inDevices[i], false));
		}

		String[] outDevices = DevicePool.getMidiDeviceNames(DevicePool.OUT);
		for (int o = 0; o < outDevices.length; o++) {
			outRoot.add(new DefaultMutableTreeNode(outDevices[o], false));
		}

		return new DefaultTreeModel(root, true) {
			@Override
			public void valueForPathChanged(TreePath path, Object newValue) {
				if (Boolean.TRUE.equals(newValue)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();

					setDevice((String) node.getUserObject(),
							node.getParent() == outRoot ? DevicePool.OUT
									: DevicePool.IN);
				} else {
					setDevice(null, DevicePool.IN);
				}
			}
		};
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
		DefaultTreeModel model = (DefaultTreeModel) deviceTree.getModel();

		if (this.deviceName != null) {
			DefaultMutableTreeNode node = getNode(this.deviceName,
					this.direction);
			if (node != null) {
				model.nodeChanged(node);
			}
		}

		this.deviceName = name;
		this.direction = direction;

		if (this.deviceName != null) {
			DefaultMutableTreeNode node = getNode(this.deviceName,
					this.direction);
			if (node != null) {
				model.nodeChanged(node);
			}
		}
	}

	protected DefaultMutableTreeNode getNode(String name, int direction) {
		DefaultMutableTreeNode parent = (direction == DevicePool.OUT) ? this.outRoot
				: this.inRoot;
		for (int n = 0; n < parent.getChildCount(); n++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent
					.getChildAt(n);
			if (child.getUserObject().equals(name)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Is the device selected for <code>out</code> or <code>in</code>.
	 * 
	 * @return <code>true</code> if selected for out
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Get the name of the selected device.
	 * 
	 * @return name
	 */
	public String getDeviceName() {
		return deviceName;
	}

	private class MidiDeviceCell extends CheckedTreeCell {

		@Override
		protected boolean isChecked(Object value) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			return node.getUserObject() == deviceName
					&& (direction == DevicePool.OUT
							&& (node.getParent() == outRoot) || direction == DevicePool.IN
							&& (node.getParent() == inRoot));
		}
	}
}