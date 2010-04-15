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
package jorgan.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiMessage;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ConsolePanel.ConsoleStack;
import jorgan.gui.dock.BordererDockingPane;
import jorgan.gui.dock.ConsoleDockable;
import jorgan.gui.dock.OrganDockable;
import jorgan.gui.dock.spi.DockableRegistry;
import jorgan.gui.play.MessagesMonitor;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.play.OrganPlay;
import jorgan.play.event.PlayListener;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.ProblemListener;
import jorgan.problem.Severity;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import jorgan.util.IOUtils;
import spin.Spin;
import swingx.docking.Dockable;
import swingx.docking.DockingPane;
import swingx.docking.persistence.XMLPersister;
import bias.Configuration;

/**
 * Panel for display and editing of an organ.
 */
public class OrganPanel extends JPanel implements SessionAware, ConsoleStack {

	private static final String DOCKING_VERSION = "1";

	private static Configuration config = Configuration.getRoot().get(
			OrganPanel.class);

	private static Logger logger = Logger.getLogger(OrganPanel.class.getName());

	private boolean constructing = false;

	/**
	 * The organ.
	 */
	private OrganSession session;

	/**
	 * The listener to events.
	 */
	private EventsListener eventsListener = new EventsListener();

	/*
	 * The outer dockingPane.
	 */
	private DockingPane docking = new BordererDockingPane() {
		@Override
		protected JComponent createComponent(Object key) {
			if ("consoles".equals(key)) {
				return consoleDocking;
			} else {
				return null;
			}
		}

		@Override
		protected Dockable createDockable(Object key) {
			for (DockableAction action : dockableActions) {
				OrganDockable dockable = action.getDockable();
				if (dockable.getKey().equals(key)) {
					dockable.setSession(session);

					return dockable;
				}
			}
			return null;
		}

		@Override
		protected void dismissDockable(Dockable dockable) {
			((OrganDockable) dockable).setSession(null);
		};
	};

	private Set<DockableAction> dockableActions = new HashSet<DockableAction>();

	/*
	 * The inner dockingPane that holds all consoles.
	 */
	private DockingPane consoleDocking = new BordererDockingPane() {
		@Override
		protected void dismissDockable(Dockable dockable) {
			((ConsoleDockable) dockable).setSession(null);
		};
	};

	private MessagesMonitor messagesMonitor = new MessagesMonitor();

	private String playDocking;

	private String constructDocking;

	/**
	 * Create a new organPanel.
	 */
	public OrganPanel() {
		config.read(this);

		setLayout(new BorderLayout());

		docking.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(docking, BorderLayout.CENTER);

		for (OrganDockable dockable : DockableRegistry.getDockables()) {
			dockableActions.add(new DockableAction(dockable));
		}

		loadDocking();
	}

	/**
	 * Get widgets (i.e. actions or components) for the status bar.
	 * 
	 * @return widgets
	 */
	public List<Object> getStatusBarWidgets() {
		List<Object> widgets = new ArrayList<Object>();

		widgets.add(messagesMonitor);

		return widgets;
	}

	/**
	 * Get widgets (i.e. actions or components) for the menu bar.
	 * 
	 * @return widgets
	 */
	public List<Object> getMenuWidgets() {
		return new ArrayList<Object>(dockableActions);
	}

	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(eventsListener));
			this.session.lookup(OrganPlay.class).removePlayerListener(
					(PlayListener) Spin.over(eventsListener));
			this.session.lookup(ElementProblems.class).removeListener(
					(ProblemListener) Spin.over(eventsListener));
			this.session.lookup(ElementSelection.class).removeListener(
					eventsListener);
			this.session.removeListener(eventsListener);

			for (Object key : docking.getDockableKeys()) {
				Dockable dockable = docking.getDockable(key);
				if (dockable != null) {
					((OrganDockable) dockable).setSession(null);
				}
			}

			for (Element element : this.session.getOrgan().getElements()) {
				if (element instanceof Console) {
					removeConsoleDockable((Console) element);
				}
			}
		}

		this.session = session;

		setConstructing(false);

		if (this.session != null) {
			setConstructing(this.session.isConstructing());

			this.session.addListener((SessionListener) Spin
					.over(eventsListener));
			this.session.lookup(ElementSelection.class).addListener(
					eventsListener);
			this.session.lookup(ElementProblems.class).addListener(
					(ProblemListener) Spin.over(eventsListener));
			this.session.lookup(OrganPlay.class).addPlayerListener(
					(PlayListener) Spin.over(eventsListener));
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(eventsListener));

			for (Object key : docking.getDockableKeys()) {
				Dockable dockable = docking.getDockable(key);
				if (dockable != null) {
					((OrganDockable) dockable).setSession(this.session);
				}
			}

			for (Console console : this.session.getOrgan().getElements(
					Console.class)) {
				addConsoleDockable(console);
			}
		}
	}

	protected void updateActions() {
		for (DockableAction action : dockableActions) {
			action.update();
		}
	}

	protected ConsoleDockable addConsoleDockable(Console console) {
		ConsoleDockable dockable = new ConsoleDockable(console);
		dockable.setSession(session);
		consoleDocking.putDockable(console, dockable);

		return dockable;
	}

	protected void removeConsoleDockable(Console console) {
		ConsoleDockable dockable = (ConsoleDockable) consoleDocking
				.getDockable(console);
		if (dockable != null) {
			consoleDocking.removeDockable(console);
		}
	}

	/**
	 * Get the organ.
	 * 
	 * @return the organ
	 */
	public OrganSession getOrgan() {
		return session;
	}

	public void setConstructing(boolean constructing) {

		if (this.constructing != constructing) {
			saveDocking();

			this.constructing = constructing;

			loadDocking();

			config.write(this);
		}

		updateActions();
	}

	protected void loadDocking() {
		String docking;
		if (constructing) {
			docking = constructDocking;
		} else {
			docking = playDocking;
		}
		if (docking != null) {
			Reader reader = new StringReader(docking);
			try {
				new XMLPersister(this.docking, reader, DOCKING_VERSION).load();
				return;
			} catch (Exception ex) {
				logger.log(Level.WARNING, "unable to load docking", ex);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}

		String dockingXml;
		if (constructing) {
			dockingXml = "construct.xml";
		} else {
			dockingXml = "play.xml";
		}
		Reader reader = new InputStreamReader(getClass().getResourceAsStream(
				dockingXml));
		try {
			new XMLPersister(this.docking, reader, DOCKING_VERSION).load();
		} catch (Exception error) {
			throw new Error("unable to load default docking");
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	protected void saveDocking() {
		Writer writer = new StringWriter();
		try {
			new XMLPersister(docking, writer, DOCKING_VERSION).save();
			String docking = writer.toString();
			if (constructing) {
				constructDocking = docking;
			} else {
				playDocking = docking;
			}
		} catch (Exception ex) {
			logger.log(Level.WARNING, "unable to save docking", ex);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public String getConstructDocking() {
		return constructDocking;
	}

	public void setConstructDocking(String constructDocking) {
		this.constructDocking = constructDocking;
	}

	public String getPlayDocking() {
		return playDocking;
	}

	public void setPlayDocking(String playDocking) {
		this.playDocking = playDocking;
	}

	public void closing() {
		saveDocking();
		config.write(this);
	}

	public void toFront(Console console) {
		Dockable dockable = consoleDocking.getDockable(console);
		if (dockable == null) {
			dockable = addConsoleDockable(console);
		} else {
			consoleDocking.putDockable(console, dockable);
		}
	}

	/**
	 * The listener to events.
	 */
	private class EventsListener extends OrganAdapter implements PlayListener,
			ProblemListener, SelectionListener, SessionListener {

		@Override
		public void received(MidiMessage message) {
			messagesMonitor.input();
		}

		@Override
		public void sent(MidiMessage message) {
			messagesMonitor.output();
		}

		public void problemAdded(Problem problem) {
			if (problem.getSeverity() == Severity.ERROR) {
				// TODO open problems dock
			}
		}

		public void problemRemoved(Problem ev) {
		}

		public void constructingChanged(boolean constructing) {
			setConstructing(constructing);
		}

		public void saved(File file) {
		}

		public void destroyed() {
		}

		public void selectionChanged() {
			if (session.lookup(ElementSelection.class).getSelectionCount() == 1) {
				Element element = session.lookup(ElementSelection.class)
						.getSelectedElement();
				if (element instanceof Console) {
					Console console = (Console) element;

					if (consoleDocking.getDockable(console) == null) {
						addConsoleDockable(console);
					}
				}
			}
		}

		public void modified() {
		}

		public void elementAdded(Element element) {
			if (element instanceof Console) {
				addConsoleDockable((Console) element);
			}
		}

		public void elementRemoved(Element element) {
			if (element instanceof Console) {
				removeConsoleDockable((Console) element);
			}
		}
	}

	private class DockableAction extends BaseAction {

		private OrganDockable dockable;

		public DockableAction(OrganDockable dockable) {
			this.dockable = dockable;

			setName(dockable.getTitle());
			setSmallIcon(dockable.getIcon());
		}

		public OrganDockable getDockable() {
			return dockable;
		}

		public void update() {
			if (session != null && constructing) {
				setEnabled(dockable.forConstruct());
			} else {
				setEnabled(dockable.forPlay());
			}
		}

		public void actionPerformed(ActionEvent ev) {
			dockable.setSession(session);

			docking.putDockable(dockable.getKey(), dockable);
		}
	}
}