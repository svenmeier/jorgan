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
package jorgan.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.recorder.midi.Recorder;

public class TrackHeaders extends JPanel {

	private Recorder recorder;

	private JPanel panel = new JPanel(new GridLayout(-1, 1));

	public TrackHeaders(Recorder recorder) {
		super(new BorderLayout());

		setBackground(Color.white);

		this.recorder = recorder;

		panel.setBackground(Color.white);
		add(panel, BorderLayout.NORTH);

		updateTracks();
	}

	public void updateTracks() {
		panel.removeAll();

		for (int track = 0; track < recorder.getTrackCount(); track++) {
			JLabel label = new JLabel("Track " + track) {
				@Override
				public Dimension getPreferredSize() {
					Dimension size = super.getPreferredSize();
					size.height = TrackPanel.HEIGHT;
					return size;
				}
			};
			panel.add(label);
		}

		panel.revalidate();
		panel.repaint();
	}
}