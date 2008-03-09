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
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.BordererDockingPane;
import jorgan.gui.dock.ConsoleDockable;
import jorgan.gui.dock.spi.ProviderRegistry;
import jorgan.gui.play.MessagesMonitor;
import jorgan.play.event.PlayListener;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import jorgan.session.event.ElementSelectionEvent;
import jorgan.session.event.ElementSelectionListener;
import jorgan.session.event.Problem;
import jorgan.session.event.ProblemListener;
import jorgan.swing.BaseAction;
import swingx.docking.DefaultDockable;
import swingx.docking.Dockable;
import swingx.docking.Docked;
import swingx.docking.DockingPane;
import swingx.docking.persistence.XMLPersister;
import bias.Configuration;

/**
 * Panel for display and editing of an organ.
 */
public class OrganPanel extends JPanel implements SessionAware {

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
	 * The outer dockingPane that holds all views.
	 */
	private DockingPane viewDocking = new BordererDockingPane();

	private Set<Dockable> dockables = new HashSet<Dockable>();

	/*
	 * The inner dockingPane that holds all consoles.
	 */
	private DockingPane consoleDocking = new BordererDockingPane();

	private BackAction backAction = new BackAction();

	private ForwardAction forwardAction = new ForwardAction();

	private MessagesMonitor messagesMonitor = new MessagesMonitor();

	private String playDocking;

	private String constructDocking;

	/**
	 * Create a new organPanel.
	 */
	public OrganPanel() {
		config.read(this);

		setLayout(new BorderLayout());

		viewDocking.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(viewDocking, BorderLayout.CENTER);

		dockables.addAll(ProviderRegistry.getDockables());

		loadDocking();
	}

	/**
	 * Get widgets (i.e. actions or components) for the toolbar.
	 * 
	 * @return widgets
	 */
	public List<Object> getToolBarWidgets() {
		List<Object> widgets = new ArrayList<Object>();

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
		List<Object> actions = new ArrayList<Object>();

		for (Dockable dockable : dockables) {
			actions.add(new DockableAction(dockable));
		}

		return actions;
	}

	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.removeOrganListener(eventsListener);
			this.session.removePlayerListener(eventsListener);
			this.session.removeProblemListener(eventsListener);
			this.session.removeSelectionListener(eventsListener);

			for (Dockable dockable : dockables) {
				if (dockable instanceof SessionAware) {
					((SessionAware) dockable).setSession(null);
				}
			}

			for (Element element : this.session.getOrgan().getElements()) {
				if (element instanceof Console) {
					removeConsoleDockable((Console) element);
				}
			}
		}

		this.session = session;

		if (this.session != null) {
			setConstructing(!this.session.getPlay().isOpen());

			this.session.addSelectionListener(eventsListener);
			this.session.addProblemListener(eventsListener);
			this.session.addPlayerListener(eventsListener);
			this.session.addOrganListener(eventsListener);

			for (Dockable dockable : dockables) {
				if (dockable instanceof SessionAware) {
					((SessionAware) dockable).setSession(this.session);
				}
			}

			for (Element element : this.session.getOrgan().getElements()) {
				if (element instanceof Console) {
					addConsoleDockable((Console) element);
				}
			}
		}

		updateHistory();
	}

	protected void updateHistory() {
		if (session == null || !constructing) {
			backAction.setEnabled(false);
			forwardAction.setEnabled(false);
		} else {
			backAction.setEnabled(session.getElementSelection().canBack());
			forwardAction
					.setEnabled(session.getElementSelection().canForward());
		}
	}

	protected void addConsoleDockable(Console console) {

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
	}

	protected void updateConsoleDockable(Console console) {
		ConsoleDockable dockable = (ConsoleDockable) consoleDocking
				.getDockable(console);
		if (dockable == null) {
			addConsoleDockable(console);
		} else {
			consoleDocking.putDockable(console, dockable);
		}
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

	private void setConstructing(boolean constructing) {

		if (this.constructing != constructing) {
			saveDocking();

			this.constructing = constructing;

			loadDocking();

			updateHistory();

			config.write(this);
		}
	}

	protected void loadDocking() {
		String docking;
		if (constructing) {
			docking = constructDocking;
		} else {
			docking = playDocking;
		}
		if (docking != null) {
			try {
				Reader reader = new StringReader(docking);
				OrganPanelPersister persister = new OrganPanelPersister(reader);
				persister.load();
				return;
			} catch (Exception ex) {
				logger.log(Level.WARNING, "unable to load docking", ex);
			}
		}

		String dockingXml;
		if (constructing) {
			dockingXml = "construct.xml";
		} else {
			dockingXml = "play.xml";
		}
		try {
			Reader reader = new InputStreamReader(getClass()
					.getResourceAsStream(dockingXml));
			OrganPanelPersister persister = new OrganPanelPersister(reader);
			persister.load();
		} catch (Exception error) {
			throw new Error("unable to load default docking");
		}
	}

	protected void saveDocking() {
		try {
			Writer writer = new StringWriter();
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

	/**
	 * The listener to events.
	 */
	private class EventsListener implements PlayListener, ProblemListener,
			OrganListener, ElementSelectionListener {

		public void inputAccepted() {
			messagesMonitor.input();
		}

		public void outputProduced() {
			messagesMonitor.output();
		}

		public void problemAdded(Problem problem) {
		}

		public void problemRemoved(Problem ev) {
		}

		public void opened() {
			setConstructing(false);
		}

		public void closed() {
			setConstructing(true);
		}

		public void changed(OrganEvent event) {
			if (event.self()) {
				Element element = event.getElement();
				if (element instanceof Console) {
					updateConsoleDockable((Console) element);
				}
			}
		}

		public void added(OrganEvent event) {
			if (event.self()) {
				Element element = event.getElement();
				if (element instanceof Console) {
					addConsoleDockable((Console) element);
				}
			}
		}

		public void removed(OrganEvent event) {
			if (event.self()) {
				Element element = event.getElement();
				if (element instanceof Console) {
					removeConsoleDockable((Console) element);
				}
			}
		}

		public void selectionChanged(ElementSelectionEvent ev) {
			if (session.getElementSelection().getSelectionCount() == 1) {
				Element element = session.getElementSelection()
						.getSelectedElement();
				if (element instanceof Console) {
					Console console = (Console) element;

					updateConsoleDockable(console);
				}
			}

			updateHistory();
		}
	}

	private class DockableAction extends BaseAction {

		private Dockable dockable;

		public DockableAction(Dockable dockable) {
			this.dockable = dockable;

			setName(dockable.getTitle());
			setSmallIcon(dockable.getIcon());
		}

		public void actionPerformed(ActionEvent ev) {
			viewDocking.putDockable(DefaultDockable.getKey(dockable), dockable);
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
			session.getElementSelection().back();
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
			session.getElementSelection().forward();
		}
	}

	private class OrganPanelPersister extends XMLPersister {
		private OrganPanelPersister(Reader reader) {
			super(viewDocking, reader);
		}

		private OrganPanelPersister(Writer writer) {
			super(viewDocking, writer);
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
			for (Dockable dockable : dockables) {
				if (DefaultDockable.getKey(dockable).equals(key)) {
					return dockable;
				}
			}
			return null;
		}
	}
}