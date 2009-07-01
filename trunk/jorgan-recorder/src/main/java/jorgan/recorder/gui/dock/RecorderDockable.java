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
import javax.sound.midi.Sequence;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import jorgan.gui.dock.OrganDockable;
import jorgan.recorder.Performance;
import jorgan.recorder.PerformanceListener;
import jorgan.recorder.gui.TracksPanel;
import jorgan.recorder.gui.file.MidiFileFilter;
import jorgan.recorder.io.MidiStream;
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

	private Performance performance;

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

	private EventListener eventListener = new EventListener();

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
			performance.removeListener((PerformanceListener) Spin
					.over(eventListener));
			performance = null;

			setContent(null);
		}

		this.session = session;

		if (this.session != null) {
			performance = session.lookup(Performance.class);
			performance.addListener((PerformanceListener) Spin
					.over(eventListener));

			tracksPanel = new TracksPanel(performance);
			JScrollPane scrollPane = new JScrollPane(tracksPanel);
			scrollPane.setRowHeaderView(tracksPanel.getHeader());
			scrollPane.getViewport().setBackground(tracksPanel.getBackground());
			setContent(scrollPane);
		}
	}

	private void updateTime() {
		if (performance != null && tracksPanel != null) {
			long time = performance.getTime();
			long totalTime = performance.getTotalTime();

			setStatus(format.format(new Date(time)) + " / "
					+ format.format(new Date(totalTime)));

			tracksPanel.updateTime();
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
			performance.reset();
		}
	}

	private class ImportAction extends BaseAction {
		public ImportAction() {
			config.get("import").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			MidiStream midiStream = new MidiStream();

			JFileChooser chooser = new JFileChooser(midiStream
					.getRecentDirectory());
			chooser.setDialogTitle(getShortDescription());
			chooser
					.setFileFilter(new jorgan.recorder.gui.file.MidiFileFilter());
			if (chooser.showOpenDialog(getContent()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();

				try {
					Sequence sequence = midiStream.read(file);

					performance.setSequence(sequence);
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
			MidiStream midiStream = new MidiStream();

			JFileChooser chooser = new JFileChooser(midiStream
					.getRecentDirectory());
			chooser.setDialogTitle(getShortDescription());
			chooser.setFileFilter(new MidiFileFilter());
			if (chooser.showSaveDialog(getContent()) == JFileChooser.APPROVE_OPTION) {
				File file = jorgan.recorder.io.MidiFileFilter.addSuffix(chooser
						.getSelectedFile());

				try {
					midiStream.write(performance.getSequence(), file);
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
			performance.first();
		}
	}

	private class LastAction extends BaseAction {
		public LastAction() {
			config.get("last").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			performance.last();
		}
	}

	private class PlayAction extends BaseAction {
		public PlayAction() {
			config.get("play").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (performance.getState() == Performance.STATE_PLAY) {
				performance.stop();
			} else {
				performance.play();
			}
		}

		protected void update() {
			if (performance.getState() == Performance.STATE_PLAY) {
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
			if (performance.getState() == Performance.STATE_RECORD) {
				performance.stop();
			} else {
				performance.record();
			}
		}

		protected void update() {
			if (performance.getState() == Performance.STATE_RECORD) {
				config.get("stop").read(this);
			} else {
				config.get("record").read(this);
			}
		}
	}

	private class EventListener implements PerformanceListener {
		public void timeChanged(long millis) {
			updateTime();
		}

		public void trackersChanged() {
		}

		public void stateChanged(int state) {
			playAction.update();
			recordAction.update();
		}
	}
}
