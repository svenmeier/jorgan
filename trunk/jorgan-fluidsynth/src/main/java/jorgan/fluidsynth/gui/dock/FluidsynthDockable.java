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

import jorgan.gui.dock.OrganDockable;
import jorgan.swing.GridBuilder;
import jorgan.swing.Separator;
import jorgan.swing.GridBuilder.Row;
import bias.Configuration;

public class FluidsynthDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthDockable.class);

	private JPanel panel;

	public FluidsynthDockable() {
		config.read(this);

		panel = new JPanel();

		GridBuilder builder = new GridBuilder(panel);
		builder.column().right();
		builder.column().grow().fill();

		builder.row(config.get("chorus").read(new Separator.CheckBox()));

		Row row = builder.row();

		row.cell(config.get("chorus/nr").read(new JLabel()));
		row.cell(new JSpinner());

		row = builder.row();

		row.cell(config.get("chorus/level").read(new JLabel()));
		row.cell(new JSpinner());

		row = builder.row();

		row.cell(config.get("chorus/speed").read(new JLabel()));
		row.cell(new JSpinner());

		row = builder.row();
		
		row.cell(config.get("chorus/depth").read(new JLabel()));
		row.cell(new JSpinner());

		row = builder.row();
		
		row.cell(config.get("chorus/type").read(new JLabel()));
		row.cell(new JComboBox());

		builder.row(config.get("reverb").read(new Separator.CheckBox()));

		row = builder.row();

		row.cell(config.get("reverb/room").read(new JLabel()));
		row.cell(new JSpinner());

		row = builder.row();

		row.cell(config.get("reverb/damping").read(new JLabel()));
		row.cell(new JSpinner());

		row = builder.row();

		row.cell(config.get("reverb/width").read(new JLabel()));
		row.cell(new JSpinner());

		row = builder.row();

		row.cell(config.get("reverb/level").read(new JLabel()));
		row.cell(new JSpinner());

		setContent(new JScrollPane(panel));
	}
}
