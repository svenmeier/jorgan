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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderAdapter;
import jorgan.recorder.midi.RecorderListener;
import spin.Spin;

public class TracksPanel extends JPanel implements Scrollable {

	private Recorder recorder;

	public TracksPanel(Recorder recorder) {
		super(new GridLayout(-1, 1));

		setBackground(Color.white);

		this.recorder = recorder;
		recorder.addListener((RecorderListener) Spin
				.over(new RecorderAdapter() {
					public void tracksChanged(int tracks) {
						updateTracks();
					}
				}));

		updateTracks();
	}

	protected void updateTracks() {
		removeAll();

		for (int track = 0; track < recorder.getTrackCount(); track++) {
			TrackPanel trackPanel = createTrackPanel(recorder, track);
			add(trackPanel);
		}

		revalidate();
		repaint();
	}

	protected TrackPanel createTrackPanel(Recorder recorder, int track) {
		return new TrackPanel(recorder, track);
	}

	public Dimension getPreferredScrollableViewportSize() {
		int width = 60 * TrackPanel.SECOND_WIDTH;
		int height = getPreferredSize().height;

		return new Dimension(width, height);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		int increment;

		if (orientation == SwingConstants.HORIZONTAL) {
			increment = visibleRect.width;
		} else {
			increment = visibleRect.height;
		}

		return increment;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		int increment;

		if (orientation == SwingConstants.HORIZONTAL) {
			increment = 10 * TrackPanel.SECOND_WIDTH;
		} else {
			increment = getPreferredSize().height / recorder.getTrackCount();
		}

		return increment;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
}
