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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JScrollPane;
import javax.swing.Timer;

import jorgan.gui.dock.OrganDockable;
import jorgan.recorder.SessionRecorder;
import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderAdapter;
import jorgan.recorder.midi.RecorderListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;

public class RecorderDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			RecorderDockable.class);

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	private OrganSession session;

	private Recorder recorder;

	private SessionRecorder sessionRecorder;

	private NewAction newAction = new NewAction();

	private OpenAction openAction = new OpenAction();

	private SaveAction saveAction = new SaveAction();

	private PlayAction playAction = new PlayAction();

	private FirstAction firstAction = new FirstAction();

	private LastAction lastAction = new LastAction();

	private RecordAction recordAction = new RecordAction();

	private Timer timer = new Timer(250, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			updateTime();
		}
	});

	private TracksPanel tracksPanel;

	public RecorderDockable() {
		config.read(this);

		format.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public boolean forConstruct() {
		return false;
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		docked.addTool(newAction);
		docked.addTool(openAction);
		docked.addTool(saveAction);
		docked.addToolSeparator();
		docked.addTool(firstAction);
		docked.addTool(playAction);
		docked.addTool(lastAction);
		docked.addTool(recordAction);

		timer.start();
	}

	@Override
	public void undocked() {
		timer.stop();

		super.undocked();
	}

	public void setSession(OrganSession session) {
		if (this.session != null) {
			sessionRecorder.dispose();
			sessionRecorder = null;

			setContent(null);
		}

		this.session = session;

		if (this.session != null) {
			recorder = new Recorder();
			recorder.addListener((RecorderListener) Spin
					.over(new RecorderAdapter() {
						public void timeChanged(long millis) {
							updateTime();
						}

						public void playing() {
							updateActions();
						}

						public void recording() {
							updateActions();
						}

						public void stopped() {
							updateActions();
						}

						private void updateActions() {
							playAction.update();
							recordAction.update();
						}
					}));

			tracksPanel = new TracksPanel(recorder) {
				@Override
				protected TrackPanel createTrackPanel(Recorder recorder,
						final int track) {
					return new TrackPanel(recorder, track) {
						@Override
						protected String getTitle() {
							return RecorderDockable.this.getTitle(track);
						}
					};
				}
			};
			setContent(new JScrollPane(tracksPanel));

			sessionRecorder = new SessionRecorder(session, recorder);
		}
	}

	private void updateTime() {
		setStatus(format.format(new Date(recorder.getTime())));
		tracksPanel.revalidate();
		tracksPanel.repaint();
	}

	private String getTitle(int track) {
		return sessionRecorder.getTitle(track);
	}

	private class NewAction extends BaseAction {
		public NewAction() {
			config.get("new").read(this);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class OpenAction extends BaseAction {
		public OpenAction() {
			config.get("open").read(this);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class SaveAction extends BaseAction {
		public SaveAction() {
			config.get("save").read(this);
		}

		public void actionPerformed(ActionEvent e) {
		}
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
}
