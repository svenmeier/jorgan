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
package jorgan.recorder.gui.dock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import jorgan.gui.dock.OrganDockable;
import jorgan.recorder.SessionRecorder;
import jorgan.recorder.SessionRecorderListener;
import jorgan.recorder.gui.TracksPanel;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;
import bias.swing.MessageBox;

public class RecorderDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			RecorderDockable.class);

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	private OrganSession session;

	private SessionRecorder recorder;

	private ResetAction resetAction = new ResetAction();

	private ImportAction importAction = new ImportAction();

	private ExportAction exportAction = new ExportAction();

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

		docked.addTool(resetAction);
		docked.addTool(importAction);
		docked.addTool(exportAction);
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
			recorder.dispose();
			recorder = null;

			setContent(null);
		}

		this.session = session;

		if (this.session != null) {
			recorder = new SessionRecorder(session);

			recorder.addListener((SessionRecorderListener) Spin
					.over(new SessionRecorderListener() {
						public void timeChanged(long millis) {
							updateTime();
						}

						public void trackerChanged(int track) {
						}

						public void stateChanged(int state) {
							playAction.update();
							recordAction.update();
						}
					}));

			tracksPanel = new TracksPanel(recorder);
			JScrollPane scrollPane = new JScrollPane(tracksPanel);
			scrollPane.setRowHeaderView(tracksPanel.getHeader());
			setContent(scrollPane);
		}
	}

	private void updateTime() {
		if (recorder != null) {
			long time = recorder.getTime();
			long totalTime = recorder.getTotalTime();
			
			setStatus(format.format(new Date(time))
					+ " / "
					+ format.format(new Date(totalTime)));

			tracksPanel.revalidate();
			tracksPanel.repaint();
		}
	}

	protected void showBoxMessage(String key, Object... args) {
		config.get(key).read(new MessageBox(MessageBox.OPTIONS_OK)).show(
				getContent(), args);
	}

	private class ResetAction extends BaseAction {
		public ResetAction() {
			config.get("reset").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			recorder.reset();
		}
	}

	private class ImportAction extends BaseAction {
		public ImportAction() {
			config.get("import").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(getShortDescription());
			chooser
					.setFileFilter(new jorgan.recorder.gui.file.MidiFileFilter());
			if (chooser.showOpenDialog(getContent()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();

				try {
					Sequence sequence = MidiSystem.getSequence(file);

					recorder.setSequence(sequence);
				} catch (IOException ex) {
					showBoxMessage("importException", file.getName());
					return;
				} catch (InvalidMidiDataException ex) {
					showBoxMessage("importInvalid", file.getName());
					return;
				}
			}
		}
	}

	private class ExportAction extends BaseAction {
		public ExportAction() {
			config.get("export").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(getShortDescription());
			chooser
					.setFileFilter(new jorgan.recorder.gui.file.MidiFileFilter());
			if (chooser.showSaveDialog(getContent()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();

				try {
					MidiSystem.write(recorder.getSequence(), 1, file);
				} catch (IOException ex) {
					showBoxMessage("exportException", file.getName());
					return;
				}
			}
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
			if (recorder.getState() == SessionRecorder.STATE_PLAY) {
				recorder.stop();
			} else {
				recorder.play();
			}
		}

		protected void update() {
			if (recorder.getState() == SessionRecorder.STATE_PLAY) {
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
			if (recorder.getState() == SessionRecorder.STATE_RECORD) {
				recorder.stop();
			} else {
				recorder.record();
			}
		}

		protected void update() {
			if (recorder.getState() == SessionRecorder.STATE_RECORD) {
				config.get("stop").read(this);
			} else {
				config.get("record").read(this);
			}
		}
	}
}
