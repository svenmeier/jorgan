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

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jorgan.recorder.SessionRecorder;

public class TrackHeader extends JPanel {

	private SessionRecorder recorder;

	public TrackHeader(SessionRecorder recorder, int track) {
		super(new GridLayout());

		this.recorder = recorder;

		JLabel label = new JLabel();
		label.setText(this.recorder.getTitle(track));
		label.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(label);
	}
}
