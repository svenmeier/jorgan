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
package jorgan.fluidsynth.gui.dock;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.gui.dock.OrganDockable;
import jorgan.session.OrganSession;
import jorgan.swing.Separator;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

public class FluidsynthDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthDockable.class);

	private JPanel panel;

	private OrganSession session;

	public FluidsynthDockable() {
		config.read(this);

		panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);
		Column column = builder.column();

		column.header(config.get("chorus").read(new Separator.CheckBox()));

		column.term(config.get("chorus/nr").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		column.term(config.get("chorus/level").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		column.term(config.get("chorus/speed").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		column.term(config.get("chorus/depth").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		column.term(config.get("chorus/type").read(new JLabel()));
		column.definition(new JComboBox(new Object[] { "SINE", "TRIANGLE" }));

		column.header(config.get("reverb").read(new Separator.CheckBox()));

		column.term(config.get("reverb/room").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		column.term(config.get("reverb/damping").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		column.term(config.get("reverb/width").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		column.term(config.get("reverb/level").read(new JLabel()));
		column.definition(new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)));

		setContent(new JScrollPane(panel));
	}

	@Override
	public void setSession(OrganSession session) {
		this.session = session;
	}
}
