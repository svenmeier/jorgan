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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.construct.ElementPropertiesPanel;
import jorgan.gui.construct.ElementsPanel;
import jorgan.gui.construct.MessagesPanel;
import jorgan.gui.construct.ReferencesPanel;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.gui.midi.MidiMonitor;
import jorgan.gui.play.DescriptionPanel;
import jorgan.gui.play.MemoryPanel;
import jorgan.gui.play.MessagesMonitor;
import jorgan.gui.play.ProblemsPanel;
import jorgan.gui.play.VirtualKeyboard;
import jorgan.play.event.PlayEvent;
import jorgan.play.event.PlayListener;
import jorgan.swing.BaseAction;
import swingx.docking.DefaultDockable;
import swingx.docking.Dock;
import swingx.docking.Dockable;
import swingx.docking.DockingPane;
import swingx.docking.border.Eclipse3Border;
import swingx.docking.persistence.XMLPersister;
import bias.Configuration;

/**
 * Panel for display and editing of an organ.
 */
public class OrganPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			OrganPanel.class);

	private static Logger logger = Logger.getLogger(OrganPanel.class.getName());

	private boolean constructing = false;

	/**
	 * The organ.
	 */
	private OrganSession session;

	/**
	 * The listener to organ changes.
	 */
	private OrganListener organListener = new InternalOrganListener();

	/**
	 * The listener to selection changes.
	 */
	private ElementSelectionListener selectionListener = new InternalSelectionListener();

	/**
	 * The listener to events sent by the player.
	 */
	private PlayListener playListener = new InternalPlayListener();

	/*
	 * The outer dockingPane that holds all views.
	 */
	private DockingPane viewDocking = new BordererDockingPane();

	private Map<String, View> views = new HashMap<String, View>();

	/*
	 * The inner dockingPane that holds all consoles.
	 */
	private DockingPane consoleDocking = new BordererDockingPane();

	private Map<Console, DefaultDockable> consolesMap = new HashMap<Console, DefaultDockable>();

	private BackAction backAction = new BackAction();

	private ForwardAction forwardAction = new ForwardAction();

	private MessagesMonitor playMonitor = new MessagesMonitor();

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

		new View("properties", new ElementPropertiesPanel(), true);
		new View("elements", new ElementsPanel(), true);
		new View("references", new ReferencesPanel(), true);
		new View("description", new DescriptionPanel(), true);
		new View("messages", new MessagesPanel(), true);
		new View("problems", new ProblemsPanel(), false);
		new View("keyboard", new VirtualKeyboard(), false);
		new View("midiMonitor", new MidiMonitor(), false);
		new View("memory", new MemoryPanel(), false);

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

		widgets.add(playMonitor);

		return widgets;
	}

	/**
	 * Get widgets (i.e. actions or components) for the menu bar.
	 * 
	 * @return widgets
	 */
	public List<Object> getMenuWidgets() {
		List<Object> actions = new ArrayList<Object>();

		for (String key : views.keySet()) {
			actions.add(views.get(key));
		}

		actions.add(null);

		actions.add(forwardAction);
		actions.add(backAction);

		return actions;
	}

	/**
	 * Set the organ to be displayed.
	 * 
	 * @param session
	 *            the organ to be displayed
	 */
	public void setOrgan(OrganSession session) {
		if (this.session != null) {
			this.session.removeOrganListener(organListener);
			this.session.removePlayerListener(playListener);
			this.session.removeSelectionListener(selectionListener);

			for (View dockable : views.values()) {
				if (dockable.getComponent() instanceof OrganAware) {
					((OrganAware) dockable.getComponent()).setOrgan(null);
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

			this.session.addOrganListener(organListener);
			this.session.addPlayerListener(playListener);
			this.session.addSelectionListener(selectionListener);

			for (View dockable : views.values()) {
				if (dockable.getComponent() instanceof OrganAware) {
					((OrganAware) dockable.getComponent())
							.setOrgan(this.session);
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
			backAction.setEnabled(session.getSelectionModel().canBack());
			forwardAction.setEnabled(session.getSelectionModel().canForward());
		}
	}

	protected void addConsoleDockable(Console console) {

		ConsolePanel consolePanel = new ConsolePanel();
		consolePanel.setOrgan(session);
		consolePanel.setConsole(console);

		JScrollPane scrollPane = new JScrollPane(consolePanel);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		DefaultDockable dockable = new DefaultDockable(scrollPane, Elements
				.getDisplayName(console));

		consoleDocking.putDockable(console, dockable);

		consolesMap.put(console, dockable);
	}

	protected void updateConsoleDockable(Console console) {
		DefaultDockable dockable = consolesMap.get(console);

		dockable.setName(Elements.getDisplayName(console));

		consoleDocking.putDockable(console, dockable);
	}

	protected void removeConsoleDockable(Console console) {
		Dockable dockable = consolesMap.remove(console);

		JScrollPane scrollPane = (JScrollPane) dockable.getComponent();
		ConsolePanel consolePanel = (ConsolePanel) scrollPane.getViewport()
				.getView();
		consolePanel.setConsole(null);
		consolePanel.setOrgan(null);

		consoleDocking.removeDockable(console);
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

			for (View dockable : views.values()) {
				dockable.update();
			}

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

	private class BordererDockingPane extends DockingPane {
		@Override
		protected Dock createDockImpl() {
			Dock dock = super.createDockImpl();
			dock.setBorder(new Eclipse3Border());
			return dock;
		}
	}

	/**
	 * The listener to events of the player.
	 */
	private class InternalPlayListener implements PlayListener {

		public void inputAccepted() {
			playMonitor.input();
		}

		public void outputProduced() {
			playMonitor.output();
		}

		public void playerAdded(PlayEvent ev) {
		}

		public void playerRemoved(PlayEvent ev) {
		}

		public void problemAdded(PlayEvent ev) {
			if (ev.getProblem() instanceof jorgan.play.Error) {
				View dockable = views.get("problems");
				viewDocking.putDockable(dockable.getKey(), dockable);
			}
		}

		public void problemRemoved(PlayEvent ev) {
		}

		public void opened() {
			setConstructing(false);
		}

		public void closed() {
			setConstructing(true);
		}
	}

	/**
	 * The listener to organ events.
	 */
	private class InternalOrganListener implements OrganListener {

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
	}

	/**
	 * The listener to selection events.
	 */
	private class InternalSelectionListener implements ElementSelectionListener {
		public void selectionChanged(ElementSelectionEvent ev) {
			if (session.getSelectionModel().getSelectionCount() == 1) {
				Element element = session.getSelectionModel()
						.getSelectedElement();
				if (element instanceof Console) {
					Console console = (Console) element;

					Dockable dockable = consolesMap.get(console);
					if (dockable == null) {
						addConsoleDockable(console);
					} else {
						consoleDocking.putDockable(console, dockable);
					}
				}
			}

			updateHistory();
		}
	}

	private class View extends BaseAction implements Dockable {

		private String key;

		private JComponent component;

		private boolean constructionOnly;

		private View(String key, JComponent component, boolean constructionOnly) {
			this.key = key;
			this.component = component;
			this.constructionOnly = constructionOnly;

			config.get(key).read(this);

			views.put(key, this);
			update();
		}

		public String getKey() {
			return key;
		}

		public void update() {
			setEnabled(!constructionOnly || constructing);
		}

		public void actionPerformed(ActionEvent ev) {
			viewDocking.putDockable(key, this);
		}

		public void closed() {
		}

		public boolean closing() {
			return true;
		}

		public JComponent getComponent() {
			return component;
		}

		public Icon getIcon() {
			return getSmallIcon();
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
			session.getSelectionModel().back();
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
			session.getSelectionModel().forward();
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
			return views.get(key);
		}
	}
}