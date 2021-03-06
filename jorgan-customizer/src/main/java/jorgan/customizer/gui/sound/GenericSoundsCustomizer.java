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
package jorgan.customizer.gui.sound;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import jorgan.customizer.gui.Customizer;
import jorgan.disposition.GenericSound;
import jorgan.session.OrganSession;
import bias.Configuration;

/**
 * customizer of {@link GenericSound}s.
 */
public class GenericSoundsCustomizer implements Customizer {

	private static Configuration config = Configuration.getRoot().get(
			GenericSoundsCustomizer.class);

	private String description;

	private JScrollPane scrollPane;

	private List<GenericSoundPanel> panels = new ArrayList<GenericSoundPanel>();

	public GenericSoundsCustomizer(OrganSession session) {
		config.read(this);

		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel grid = new JPanel(new GridLayout(0, 1));
		scrollPane.setViewportView(grid);

		for (GenericSound sound : session.getOrgan().getElements(
				GenericSound.class)) {
			GenericSoundPanel panel = new GenericSoundPanel(sound);
			panels.add(panel);
			grid.add(panel);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JComponent getComponent() {
		return scrollPane;
	}

	public void apply() {
		for (GenericSoundPanel panel : panels) {
			panel.apply();
		}
	}

	public static boolean customizes(OrganSession session) {
		return session.getOrgan().getElements(GenericSound.class).size() > 0;
	}
}