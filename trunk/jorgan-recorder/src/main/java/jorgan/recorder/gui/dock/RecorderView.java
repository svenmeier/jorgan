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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import jorgan.gui.dock.AbstractView;
import jorgan.recorder.Performance;
import jorgan.recorder.PerformanceListener;
import jorgan.recorder.gui.TracksPanel;
import jorgan.recorder.gui.file.MidiFileFilter;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;
import bias.swing.MessageBox;
import bias.util.MessageBuilder;

public class RecorderView extends AbstractView {

	private static Logger logger = Logger.getLogger(RecorderView.class
			.getName());

	private static Configuration config = Configuration.getRoot().get(
			RecorderView.class);

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	private OrganSession session;

	private Performance performance;

	private EjectAction ejectAction = new EjectAction();

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

	private MessageBuilder statusBuilder = new MessageBuilder();

	public RecorderView() {
		config.read(this);

		config.get("status").read(statusBuilder);

		format.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public boolean forConstruct() {
		return false;
	}

	@Override
	protected void addTools(Docked docked) {

		docked.addTool(firstAction);
		docked.addTool(playAction);
		docked.addTool(lastAction);
		docked.addTool(recordAction);
		docked.addToolSeparator();
		docked.addTool(ejectAction);
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

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
		}

		this.session = session;

		if (this.session != null) {
			performance = session.lookup(Performance.class);
			performance.addListener((PerformanceListener) Spin
					.over(eventListener));
		}

		update();
	}

	private void update() {
		if (tracksPanel != null) {
			tracksPanel = null;
			setContent(null);
		}

		if (performance != null && performance.isLoaded()) {
			tracksPanel = new TracksPanel(performance);
			JScrollPane scrollPane = new JScrollPane(tracksPanel);
			scrollPane.setRowHeaderView(tracksPanel.getHeader());
			scrollPane.getViewport().setBackground(tracksPanel.getBackground());
			setContent(scrollPane);
		}

		ejectAction.update();
		playAction.update();
		firstAction.update();
		lastAction.update();
		recordAction.update();

		updateTime();
	}

	private void updateTime() {
		if (performance != null && performance.isLoaded()) {
			String name = MidiFileFilter.removeSuffix(performance.getFile());
			long time = performance.getTime();
			long totalTime = performance.getTotalTime();

			setStatus(statusBuilder.build(name, format.format(new Date(time)),
					format.format(new Date(totalTime))));

			tracksPanel.updateTime();
		} else {
			setStatus(null);
		}
	}

	protected int showBoxMessage(String key, int options, Object... args) {
		return config.get(key).read(new MessageBox(options)).show(
				getContent().getTopLevelAncestor(), args);
	}

	private boolean canEject() {
		int option = showBoxMessage("eject/confirm",
				MessageBox.OPTIONS_YES_NO_CANCEL);
		if (option == MessageBox.OPTION_YES) {
			return save();
		} else if (option == MessageBox.OPTION_NO) {
			return true;
		}

		return false;
	}

	private boolean save() {
		try {
			performance.save();
		} catch (IOException ex) {
			logger.log(Level.INFO, "saving performance failed", ex);

			showBoxMessage("saveException", MessageBox.OPTIONS_OK, session
					.getFile().getName());

			return false;
		}

		return true;
	}

	private abstract class AbstractControlAction extends BaseAction {

		public AbstractControlAction() {
			setEnabled(false);
		}

		public void update() {
			setEnabled(performance != null && performance.isLoaded());
		}
	}

	private class EjectAction extends BaseAction {
		public EjectAction() {
			config.get("eject").read(this);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			performance.stop();

			if (performance.isLoaded() && performance.isModified()
					&& !canEject()) {
				return;
			}

			File file;
			JFileChooser chooser = new JFileChooser(session.getFile());
			config.get("eject/chooser").read(chooser);
			chooser.setFileFilter(new MidiFileFilter());
			if (chooser
					.showDialog(getContent(), chooser.getApproveButtonText()) == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				if (!file.exists()) {
					file = MidiFileFilter.addSuffix(file);
				}
			} else {
				file = null;
			}
			performance.setFile(file);
		}

		public void update() {
			setEnabled(performance != null && performance.isEnabled());
		}
	}

	private class FirstAction extends AbstractControlAction {
		public FirstAction() {
			config.get("first").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			performance.first();
		}
	}

	private class LastAction extends AbstractControlAction {
		public LastAction() {
			config.get("last").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			performance.last();
		}
	}

	private class PlayAction extends AbstractControlAction {
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

		public void update() {
			super.update();

			if (performance != null
					&& performance.getState() == Performance.STATE_PLAY) {
				config.get("stop").read(this);
			} else {
				config.get("play").read(this);
			}
		}
	}

	private class RecordAction extends AbstractControlAction {
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

		public void update() {
			super.update();

			if (performance != null
					&& performance.getState() == Performance.STATE_RECORD) {
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

		public void changed() {
			update();
		}

		public void stateChanged(int state) {
			playAction.update();
			recordAction.update();
		}
	}
}
