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
package jorgan.lan.gui.preferences;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.lan.LanDeviceProvider;
import jorgan.lan.SendDevice;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link SendDevice} category.
 */
public class LanCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			LanCategory.class);

	private Model<Integer> senderCount = getModel(new Property(
			LanDeviceProvider.class, "senderCount"));

	private Model<Integer> receiverCount = getModel(new Property(
			LanDeviceProvider.class, "receiverCount"));

	private JSpinner senderCountSpinner;

	private JSpinner receiverCountSpinner;

	public LanCategory() {
		config.read(this);
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.term(config.get("senderCount").read(new JLabel()));

		senderCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
		column.definition(senderCountSpinner);

		column.term(config.get("receiverCount").read(new JLabel()));

		receiverCountSpinner = new JSpinner(
				new SpinnerNumberModel(0, 0, 256, 1));
		column.definition(receiverCountSpinner);

		return panel;
	}

	@Override
	protected void read() {
		senderCountSpinner.setValue(senderCount.getValue());
		receiverCountSpinner.setValue(receiverCount.getValue());
	}

	@Override
	protected void write() {
		senderCount.setValue((Integer) senderCountSpinner.getValue());
		receiverCount.setValue((Integer) receiverCountSpinner.getValue());
	}
}