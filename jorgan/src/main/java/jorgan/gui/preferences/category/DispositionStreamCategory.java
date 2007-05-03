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
import bias.swing.Category;
import bias.swing.PropertyModel;

import com.sun.imageio.plugins.common.I18N;

/**
 * {@link DispositionStream} category.
 */
public class DispositionStreamCategory extends JOrganCategory {

	private static I18N i18n = I18N.get(DispositionStreamCategory.class);

	private PropertyModel recentMax = getModel(DispositionStream.class,
			"recentMax");

	private PropertyModel historySize = getModel(DispositionStream.class,
			"historySize");

	private JSpinner recentMaxSpinner = new JSpinner(new SpinnerNumberModel(0,
			0, 100, 1));

	private JSpinner historySizeSpinner = new JSpinner(new SpinnerNumberModel(
			0, 0, 255, 1));

	protected String createName() {
		return i18n.getString("name");
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("recentsSizeSpinner/label")),
				builder.nextColumn());
		panel.add(recentMaxSpinner, builder.nextColumn());

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("historySizeSpinner/label")),
				builder.nextColumn());
		panel.add(historySizeSpinner, builder.nextColumn());

		return panel;
	}

	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	protected void read() {
		recentMaxSpinner.setValue(recentMax.getValue());
		historySizeSpinner.setValue(historySize.getValue());
	}

	protected void write() {
		recentMax.setValue(recentMaxSpinner.getValue());
		historySize.setValue(historySizeSpinner.getValue());
	}
}