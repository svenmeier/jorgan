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
package jorgan.recorder.gui.preferences;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.recorder.ElementEncoder;
import jorgan.recorder.tracker.ConsoleTracker;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link MidiMerger} category.
 */
public class RecorderCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			RecorderCategory.class);

	private Model<Boolean> recordCombinationRecalls = getModel(new Property(
			ConsoleTracker.class, "recordCombinationRecalls"));

	private Model<Boolean> encodeNames = getModel(new Property(
			ElementEncoder.class, "name"));

	private JCheckBox recordCombinationRecallsCheckBox = new JCheckBox();

	private JCheckBox encodeNamesCheckBox = new JCheckBox();

	public RecorderCategory() {
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

		column.definition(config.get("encodeNames").read(encodeNamesCheckBox));

		column.definition(config.get("recordCombinationRecalls").read(
				recordCombinationRecallsCheckBox));

		return panel;
	}

	@Override
	protected void read() {
		encodeNamesCheckBox.setSelected(encodeNames.getValue());

		recordCombinationRecallsCheckBox.setSelected(recordCombinationRecalls
				.getValue());
	}

	@Override
	protected void write() {
		encodeNames.setValue(encodeNamesCheckBox.isSelected());

		recordCombinationRecalls.setValue(recordCombinationRecallsCheckBox
				.isSelected());
	}
}