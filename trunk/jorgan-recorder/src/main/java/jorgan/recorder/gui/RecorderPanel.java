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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import jorgan.swing.BaseAction;
import spin.Spin;
import bias.Configuration;

public class RecorderPanel extends JPanel {

	private static final Configuration config = Configuration.getRoot().get(
			RecorderPanel.class);

	private Recorder recorder;

	private PlayAction playAction = new PlayAction();

	private FirstAction firstAction = new FirstAction();

	private LastAction lastAction = new LastAction();

	private RecordAction recordAction = new RecordAction();

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
						updateActions();
					}

					public void recording() {
						updateActions();
					}

					public void stopping() {
					}

					public void stopped() {
						updateActions();
					}

					private void updateActions() {
						playAction.update();
						recordAction.update();
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

		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
		add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.add(new JButton(firstAction));
		buttonPanel.add(new JButton(playAction));
		buttonPanel.add(new JButton(lastAction));
		buttonPanel.add(new JButton(recordAction));

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
			trackPanel.setFont(new Font("Monospaced", Font.PLAIN, 16));
		}

		tracksPanel.revalidate();
		tracksPanel.repaint();
	}

	protected TrackPanel createTrackPanel(Recorder recorder, int track) {
		return new TrackPanel(recorder, track);
	}

	private void updateTime() {
		label.setText(format.format(new Date(getTime())));

		repaint();
	}

	private class FirstAction extends BaseAction {
		public FirstAction() {
			config.get("first").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			recorder.first();
		}
	}

	private class LastAction extends BaseAction {
		public LastAction() {
			config.get("last").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			recorder.last();
		}
	}

	private class PlayAction extends BaseAction {
		public PlayAction() {
			config.get("play").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (recorder.isStopped()) {
				recorder.play();
			} else {
				recorder.stop();
			}
		}

		protected void update() {
			if (recorder.isPlaying()) {
				config.get("stop").read(this);
			} else {
				config.get("play").read(this);
			}
		}
	}

	private class RecordAction extends BaseAction {
		public RecordAction() {
			config.get("record").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (recorder.isRecording()) {
				recorder.stop();
			} else {
				recorder.record();
			}
		}

		protected void update() {
			if (recorder.isRecording()) {
				config.get("stop").read(this);
			} else {
				config.get("record").read(this);
			}
		}
	}

	private long getTime() {
		return recorder.getTime();
	}
}
