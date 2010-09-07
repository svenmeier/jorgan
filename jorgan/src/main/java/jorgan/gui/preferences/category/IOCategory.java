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
package jorgan.gui.preferences.category;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.session.History;
import jorgan.session.OrganSession;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 */
public class IOCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			IOCategory.class);

	private Model<Integer> backupCount = getModel(new Property(
			OrganSession.class, "backupCount"));

	private Model<Integer> historyMax = getModel(new Property(History.class,
			"max"));

	private JSpinner backupCountSpinner = new JSpinner(new SpinnerNumberModel(
			0, 0, 255, 1));

	private JSpinner historyMaxSpinner = new JSpinner(new SpinnerNumberModel(0,
			0, 100, 1));

	public IOCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		DefinitionBuilder builder = new DefinitionBuilder(panel);
		Column column = builder.column();

		column.term(config.get("historyMax").read(new JLabel()));
		column.definition(historyMaxSpinner);

		column.term(config.get("backupCount").read(new JLabel()));
		column.definition(backupCountSpinner);

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		historyMaxSpinner.setValue(historyMax.getValue());
		backupCountSpinner.setValue(backupCount.getValue());
	}

	@Override
	protected void write() {
		historyMax.setValue((Integer) historyMaxSpinner.getValue());
		backupCount.setValue((Integer) backupCountSpinner.getValue());
	}
}