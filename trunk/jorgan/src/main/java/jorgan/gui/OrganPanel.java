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
import jorgan.gui.undo.UndoListener;
import jorgan.gui.undo.UndoManager;
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
import swingx.docking.Docked;
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
	private DockingPane docking = new BordererDockingPane();

	private Set<DockableAction> dockableActions = new HashSet<DockableAction>();

	/*
	 * The inner dockingPane that holds all consoles.
	 */
	private DockingPane consoleDocking = new BordererDockingPane();

	private BackAction backAction = new BackAction();

	private ForwardAction forwardAction = new ForwardAction();

	private UndoAction undoAction = new UndoAction();

	private RedoAction redoAction = new RedoAction();

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
	 * Get widgets (i.e. actions or components) for the toolbar.
	 * 
	 * @return widgets
	 */
	public List<Object> getToolBarWidgets() {
		List<Object> widgets = new ArrayList<Object>();

		widgets.add(undoAction);
		widgets.add(redoAction);

		widgets.add(backAction);
		widgets.add(forwardAction);

		return widgets;
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
			this.session.lookup(UndoManager.class).removeListener(
					(UndoListener) Spin.over(eventsListener));
			this.session.lookup(ElementSelection.class).removeListener(
					eventsListener);
			this.session.removeListener(eventsListener);

			for (DockableAction action : dockableActions) {
				action.getDockable().setSession(null);
			}

			for (Element element : this.session.getOrgan().getElements()) {
				if (element instanceof Console) {
					removeConsoleDockable((Console) element);
				}
			}
		}

		this.session = session;

		if (this.session != null) {
			setConstructing(!this.session.lookup(OrganPlay.class).isOpen());

			this.session.addListener(eventsListener);
			this.session.lookup(ElementSelection.class)
					.addListener(eventsListener);
			this.session.lookup(UndoManager.class).addListener(
					(UndoListener) Spin.over(eventsListener));
			this.session.lookup(ElementProblems.class).addListener(
					(ProblemListener) Spin.over(eventsListener));
			this.session.lookup(OrganPlay.class).addPlayerListener(
					(PlayListener) Spin.over(eventsListener));
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(eventsListener));

			for (DockableAction action : dockableActions) {
				action.getDockable().setSession(this.session);
			}

			for (Console console : this.session.getOrgan().getElements(
					Console.class)) {
				addConsoleDockable(console);
			}
		}

		undoAction.setEnabled(false);
		redoAction.setEnabled(false);

		updateHistory();
	}

	protected void updateHistory() {
		if (session == null || !constructing) {
			backAction.setEnabled(false);
			forwardAction.setEnabled(false);
		} else {
			backAction
					.setEnabled(session.lookup(ElementSelection.class).canBack());
			forwardAction.setEnabled(session.lookup(ElementSelection.class)
					.canForward());
		}
	}

	protected void updateActions() {
		for (DockableAction action : dockableActions) {
			action.update();
		}
	}

	protected ConsoleDockable addConsoleDockable(Console console) {

		ConsoleDockable dockable = new ConsoleDockable(console) {
			@Override
			public void docked(Docked docked) {
				super.docked(docked);

				setSession(session);
			}

			@Override
			public void undocked() {
				setSession(null);

				super.undocked();
			}
		};

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

		updateHistory();

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
				OrganPanelPersister persister = new OrganPanelPersister(reader);
				persister.load();
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
			OrganPanelPersister persister = new OrganPanelPersister(reader);
			persister.load();
		} catch (Exception error) {
			throw new Error("unable to load default docking");
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	protected void saveDocking() {
		Writer writer = new StringWriter();
		try {
			OrganPanelPersister persister = new OrganPanelPersister(writer);
			persister.save();
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
		}
		consoleDocking.putDockable(console, dockable);
	}

	/**
	 * The listener to events.
	 */
	private class EventsListener extends OrganAdapter implements PlayListener,
			ProblemListener, SelectionListener, UndoListener, SessionListener {

		public void received(int channel, int command, int data1, int data2) {
			messagesMonitor.input();
		}

		public void sent(int channel, int command, int data1, int data2) {
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

		public void destroyed() {
		}

		public void selectionChanged() {
			if (session.lookup(ElementSelection.class).getSelectionCount() == 1) {
				Element element = session.lookup(ElementSelection.class).getSelectedElement();
				if (element instanceof Console) {
					Console console = (Console) element;

					if (consoleDocking.getDockable(console) == null) {
						addConsoleDockable(console);
					}
				}
			}

			updateHistory();
		}

		public void changed() {
			undoAction.setEnabled(session.lookup(UndoManager.class).canUndo());
			redoAction.setEnabled(session.lookup(UndoManager.class).canRedo());
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
			if (constructing) {
				setEnabled(dockable.forConstruct());
			} else {
				setEnabled(dockable.forPlay());
			}
		}

		public void actionPerformed(ActionEvent ev) {
			docking.putDockable(dockable.getKey(), dockable);
		}
	}

	/**
	 * The action that steps back to the previous element.
	 */
	private class BackAction extends BaseAction {
		private BackAction() {
			config.get("back").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(ElementSelection.class).back();
		}
	}

	/**
	 * The action that steps forward to the next element.
	 */
	private class ForwardAction extends BaseAction {
		private ForwardAction() {
			config.get("forward").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(ElementSelection.class).forward();
		}
	}

	private class UndoAction extends BaseAction {
		private UndoAction() {
			config.get("undo").read(this);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(UndoManager.class).undo();
		}
	}

	private class RedoAction extends BaseAction {
		private RedoAction() {
			config.get("redo").read(this);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(UndoManager.class).redo();
		}
	}

	private class OrganPanelPersister extends XMLPersister {

		private OrganPanelPersister(Reader reader) {
			super(docking, reader, DOCKING_VERSION);
		}

		private OrganPanelPersister(Writer writer) {
			super(docking, writer, DOCKING_VERSION);
		}

		@Override
		protected JComponent resolveComponent(Object key) {
			if ("consoles".equals(key)) {
				return consoleDocking;
			} else {
				return null;
			}
		}

		@Override
		protected Dockable resolveDockable(Object key) {
			for (DockableAction action : dockableActions) {
				if (action.getDockable().getKey().equals(key)) {
					return action.getDockable();
				}
			}
			return null;
		}
	}
}