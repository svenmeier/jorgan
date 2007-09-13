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

import jorgan.io.DispositionStream;
import jorgan.swing.GridBuilder;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link DispositionStream} category.
 */
public class DispositionStreamCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			DispositionStreamCategory.class);

	private Model recentMax = getModel(new Property(DispositionStream.class,
			"recentMax"));

	private Model historySize = getModel(new Property(DispositionStream.class,
			"historySize"));

	private JSpinner recentMaxSpinner = new JSpinner(new SpinnerNumberModel(0,
			0, 100, 1));

	private JSpinner historySizeSpinner = new JSpinner(new SpinnerNumberModel(
			0, 0, 255, 1));

	public DispositionStreamCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		panel.add(config.get("recentsSize").read(new JLabel()), builder
				.nextColumn());
		panel.add(recentMaxSpinner, builder.nextColumn());

		builder.nextRow();

		panel.add(config.get("historySize").read(new JLabel()), builder
				.nextColumn());
		panel.add(historySizeSpinner, builder.nextColumn());

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		recentMaxSpinner.setValue(recentMax.getValue());
		historySizeSpinner.setValue(historySize.getValue());
	}

	@Override
	protected void write() {
		recentMax.setValue(recentMaxSpinner.getValue());
		historySize.setValue(historySizeSpinner.getValue());
	}
}