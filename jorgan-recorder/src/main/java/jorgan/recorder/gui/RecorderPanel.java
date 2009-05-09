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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.sound.midi.MidiMessage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import spin.Spin;

public class RecorderPanel extends JPanel {

	private Recorder recorder;

	private JLabel label;

	private JPanel tracksPanel;

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	private Timer timer = new Timer(500, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			updateTime();
		}
	});

	public RecorderPanel(Recorder recorder) {
		super(new BorderLayout());

		this.recorder = recorder;

		format.setTimeZone(TimeZone.getTimeZone("UTC"));

		recorder.addListener((RecorderListener) Spin
				.over(new RecorderListener() {
					public void timeChanged(long millis) {
						updateTime();
					}

					public void tracksChanged(int tracks) {
						updateTracks();
					}

					public void played(int track, long millis,
							MidiMessage message) {
					}

					public void recorded(int track, long millis,
							MidiMessage message) {
					}

					public void playing() {
					}

					public void recording() {
					}

					public void stopping() {
					}

					public void stopped() {
					}
				}));

		label = new JLabel();
		label.setFont(new Font("Monospaced", Font.PLAIN, 32));
		label.setVerticalAlignment(JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setOpaque(true);
		label.setForeground(Color.white);
		label.setBackground(Color.black);
		label.setText(format.format(new Date(0)));
		add(label, BorderLayout.NORTH);

		tracksPanel = new JPanel(new GridLayout(-1, 1));
		tracksPanel.setBackground(Color.white);
		add(tracksPanel, BorderLayout.CENTER);

		addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				if (isDisplayable()) {
					timer.start();
				} else {
					timer.stop();
				}
			}
		});

		updateTracks();
	}

	protected void updateTracks() {
		tracksPanel.removeAll();

		for (int track = 0; track < recorder.getTrackCount(); track++) {
			TrackPanel trackPanel = createTrackPanel(recorder, track);
			tracksPanel.add(trackPanel);
		}

		tracksPanel.revalidate();
		tracksPanel.repaint();
	}

	protected TrackPanel createTrackPanel(Recorder recorder, int track) {
		return new TrackPanel(recorder, track);
	}

	private long getTime() {
		return recorder.getTime();
	}

	private void updateTime() {
		label.setText(format.format(new Date(getTime())));

		repaint();
	}
}
