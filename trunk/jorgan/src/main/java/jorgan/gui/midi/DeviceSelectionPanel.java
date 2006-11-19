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

import java.awt.*;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jorgan.sound.midi.DevicePool;
import jorgan.swing.tree.CheckedTreeCell;

/**
 * A panel to select a MIDI device.
 */
public class DeviceSelectionPanel extends JPanel {

	protected static final ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.resources");

	private JTree deviceTree = new JTree();

	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	private DefaultMutableTreeNode in = new DefaultMutableTreeNode(resources
			.getString("log.select.input"));

	private DefaultMutableTreeNode out = new DefaultMutableTreeNode(resources
			.getString("log.select.output"));

	private String deviceName = null;

	private boolean deviceOut = false;

	/**
	 * Create this panel.
	 */
	public DeviceSelectionPanel() {
		setLayout(new BorderLayout());

		deviceTree.setShowsRootHandles(true);
		deviceTree.setRootVisible(false);
		deviceTree.setEditable(true);
		deviceTree.setCellRenderer(new MidiDeviceCell());
		deviceTree.setCellEditor(new MidiDeviceCell());
		deviceTree.setModel(createModel());
		add(new JScrollPane(deviceTree), BorderLayout.CENTER);
	}

	protected TreeModel createModel() {
		root.add(in);
		root.add(out);

		String[] inDevices = DevicePool.getMidiDeviceNames(false);
		for (int i = 0; i < inDevices.length; i++) {
			in.add(new DefaultMutableTreeNode(inDevices[i], false));
		}

		String[] outDevices = DevicePool.getMidiDeviceNames(true);
		for (int o = 0; o < outDevices.length; o++) {
			out.add(new DefaultMutableTreeNode(outDevices[o], false));
		}

		return new DefaultTreeModel(root, true) {
			public void valueForPathChanged(TreePath path, Object newValue) {
				if (Boolean.TRUE.equals(newValue)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();

					setDevice((String) node.getUserObject(),
							node.getParent() == out);
				} else {
					setDevice(null, false);
				}
			}
		};
	}

	/**
	 * Set the selected device.
	 * 
	 * @param name	name of device
	 * @param out	is the device selected for <code>out</code> of <code>in</code> 
	 */
	public void setDevice(String name, boolean out) {
		DefaultTreeModel model = (DefaultTreeModel) deviceTree.getModel();

		if (this.deviceName != null) {
			DefaultMutableTreeNode node = getNode(this.deviceName,
					this.deviceOut);
			if (node != null) {
				model.nodeChanged(node);
			}
		}

		this.deviceName = name;
		this.deviceOut = out;

		if (this.deviceName != null) {
			DefaultMutableTreeNode node = getNode(this.deviceName,
					this.deviceOut);
			if (node != null) {
				model.nodeChanged(node);
			}
		}
	}

	protected DefaultMutableTreeNode getNode(String name, boolean out) {
		DefaultMutableTreeNode parent = out ? this.out : this.in;
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
	 * @return	<code>true</code> if selected for out
	 */
	public boolean getDeviceOut() {
		return deviceOut;
	}

	/**
	 * Get the name of the selected device.
	 * 
	 * @return	name
	 */
	public String getDeviceName() {
		return deviceName;
	}

	private class MidiDeviceCell extends CheckedTreeCell {

		protected boolean isCheckable(Object value) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			return !node.getAllowsChildren();
		}

		protected boolean isChecked(Object value) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			return node.getUserObject() == deviceName
					&& ((node.getParent() == out) == deviceOut);
		}
	}
}