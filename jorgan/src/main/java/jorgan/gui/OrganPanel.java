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

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.construct.ElementPropertiesPanel;
import jorgan.gui.construct.ElementsPanel;
import jorgan.gui.construct.ReferencesPanel;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.gui.midi.MidiMonitor;
import jorgan.gui.play.MemoryPanel;
import jorgan.gui.play.PlayMonitor;
import jorgan.gui.play.ProblemsPanel;
import jorgan.gui.play.VirtualKeyboard;
import jorgan.play.Problem;
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

	private static final String KEY_CONSOLES = "consoles";

	private static final String KEY_PROBLEMS = "problems";

	private static final String KEY_KEYBOARD = "keyboard";

	private static final String KEY_MIDI_MONITOR = "midiMonitor";

	private static final String KEY_ELEMENTS = "elements";

	private static final String KEY_REFERENCES = "references";

	private static final String KEY_PROPERTIES = "properties";

	private static final String KEY_MEMORY = "memory";

	private static final String KEY_SKINS = "skins";

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
	private PlayListener playerListener = new InternalPlayerListener();

	/*
	 * The outer dockingPane that holds all views.
	 */
	private DockingPane outer = new BordererDockingPane();

	/*
	 * The innter dockingPane that holds all consoles.
	 */
	private DockingPane inner = new BordererDockingPane();

	private ElementPropertiesPanel propertiesPanel = new ElementPropertiesPanel();

	private ElementsPanel elementsPanel = new ElementsPanel();

	private ReferencesPanel referencesPanel = new ReferencesPanel();

	private ProblemsPanel problemsPanel = new ProblemsPanel();

	private VirtualKeyboard virtualKeyboard = new VirtualKeyboard();

	private MidiMonitor midiMonitor = new MidiMonitor();

	private PlayMonitor playMonitor = new PlayMonitor();

	private MemoryPanel memoryPanel = new MemoryPanel();

	private ActionDockable problemsDockable = new ActionDockable(KEY_PROBLEMS,
			problemsPanel);

	private ActionDockable keyboardDockable = new ActionDockable(KEY_KEYBOARD,
			virtualKeyboard);

	private ActionDockable midiMonitorDockable = new ActionDockable(
			KEY_MIDI_MONITOR, midiMonitor);

	private ActionDockable elementsDockable = new ActionDockable(KEY_ELEMENTS,
			elementsPanel);

	private ActionDockable referencesDockable = new ActionDockable(
			KEY_REFERENCES, referencesPanel);

	private ActionDockable propertiesDockable = new ActionDockable(
			KEY_PROPERTIES, propertiesPanel);

	private ActionDockable memoryDockable = new ActionDockable(KEY_MEMORY,
			memoryPanel);

	private ActionDockable skinsDockable = new ActionDockable(KEY_SKINS, null);

	private Map<Console, DefaultDockable> consoleDockables = new HashMap<Console, DefaultDockable>();

	private BackAction backAction = new BackAction();

	private ForwardAction forwardAction = new ForwardAction();

	private String playDocking;

	private String constructDocking;

	/**
	 * Create a new organPanel.
	 */
	public OrganPanel() {
		config.read(this);

		setLayout(new BorderLayout());

		outer.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(outer, BorderLayout.CENTER);

		elementsDockable.setEnabled(false);
		referencesDockable.setEnabled(false);
		propertiesDockable.setEnabled(false);
		skinsDockable.setEnabled(false);

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
	public List getMenuWidgets() {
		List<Object> actions = new ArrayList<Object>();
		actions.add(problemsDockable);
		actions.add(keyboardDockable);
		actions.add(midiMonitorDockable);
		actions.add(memoryDockable);
		actions.add(elementsDockable);
		actions.add(propertiesDockable);
		actions.add(referencesDockable);
		actions.add(skinsDockable);

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
			this.session.removePlayerListener(playerListener);
			this.session.removeSelectionListener(selectionListener);

			propertiesPanel.setOrgan(null);
			referencesPanel.setOrgan(null);
			elementsPanel.setOrgan(null);
			problemsPanel.setOrgan(null);
			memoryPanel.setOrgan(null);

			for (int e = 0; e < this.session.getOrgan().getElementCount(); e++) {
				Element element = this.session.getOrgan().getElement(e);
				if (element instanceof Console) {
					removeConsoleDockable((Console) element);
				}
			}
		}

		this.session = session;

		if (this.session != null) {
			setConstructing(!this.session.getPlay().isOpen());

			this.session.addOrganListener(organListener);
			this.session.addPlayerListener(playerListener);
			this.session.addSelectionListener(selectionListener);

			propertiesPanel.setOrgan(this.session);
			referencesPanel.setOrgan(this.session);
			elementsPanel.setOrgan(this.session);
			problemsPanel.setOrgan(this.session);
			memoryPanel.setOrgan(this.session);

			for (int e = 0; e < this.session.getOrgan().getElementCount(); e++) {
				Element element = this.session.getOrgan().getElement(e);
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

		inner.putDockable(console, dockable);

		consoleDockables.put(console, dockable);
	}

	protected void updateConsoleDockable(Console console) {
		DefaultDockable dockable = consoleDockables.get(console);

		dockable.setName(Elements.getDisplayName(console));

		inner.putDockable(console, dockable);
	}

	protected void removeConsoleDockable(Console console) {
		Dockable dockable = consoleDockables.remove(console);

		JScrollPane scrollPane = (JScrollPane) dockable.getComponent();
		ConsolePanel consolePanel = (ConsolePanel) scrollPane.getViewport()
				.getView();
		consolePanel.setConsole(null);
		consolePanel.setOrgan(null);

		inner.removeDockable(console);
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

			elementsDockable.setEnabled(constructing);
			referencesDockable.setEnabled(constructing);
			propertiesDockable.setEnabled(constructing);

			updateHistory();

			config.write(this);
		}
	}

	protected void loadDocking() {
		try {
			String docking;
			if (constructing) {
				docking = constructDocking;
			} else {
				docking = playDocking;
			}
			Reader reader = new StringReader(docking);
			OrganPanelPersister persister = new OrganPanelPersister(reader);
			persister.load();
		} catch (Exception keepStandardDocking) {
			try {
				String dockingXml;
				if (constructing) {
					dockingXml = "construct.xml";
				} else {
					dockingXml = "play.xml";
				}
				Reader reader = new InputStreamReader(getClass()
						.getResourceAsStream(dockingXml));
				OrganPanelPersister persister = new OrganPanelPersister(reader);
				persister.load();
			} catch (Exception error) {
				throw new Error("unable to load default docking");
			}
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
			logger.log(Level.FINE, "unable to save docking", ex);
		}
	}

	/**
	 * The listener to events of the player.
	 */
	private class InternalPlayerListener implements PlayListener {

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
			if (Problem.ERROR.equals(ev.getProblem().getLevel())) {
				outer.putDockable(KEY_PROBLEMS, problemsDockable);
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
	private class InternalOrganListener extends OrganAdapter {

		@Override
		public void elementChanged(OrganEvent event) {
			jorgan.disposition.Element element = event.getElement();

			if (element instanceof Console) {
				updateConsoleDockable((Console) element);
			}
		}

		@Override
		public void elementAdded(OrganEvent event) {

			jorgan.disposition.Element element = event.getElement();

			if (element instanceof Console) {
				addConsoleDockable((Console) element);
			}
		}

		@Override
		public void elementRemoved(OrganEvent event) {

			jorgan.disposition.Element element = event.getElement();

			if (element instanceof Console) {
				removeConsoleDockable((Console) element);
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

					Dockable dockable = consoleDockables.get(console);
					if (dockable == null) {
						addConsoleDockable(console);

						dockable = consoleDockables.get(console);
					}
					inner.putDockable(console, dockable);
				}
			}

			updateHistory();
		}
	}

	private class ActionDockable extends BaseAction implements Dockable {

		private String key;

		private JComponent component;

		private Icon icon;

		private ActionDockable(String key, JComponent component) {
			this.key = key;
			this.component = component;
			this.icon = new ImageIcon(getClass().getResource(
					"img/" + key + ".gif"));
			putValue(Action.SMALL_ICON, icon);

			config.get(key).read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			outer.putDockable(key, this);
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
			return icon;
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
			super(outer, reader);
		}

		private OrganPanelPersister(Writer writer) {
			super(outer, writer);
		}

		@Override
		protected JComponent resolveComponent(Object key) {
			if (KEY_CONSOLES.equals(key)) {
				return inner;
			} else {
				return null;
			}
		}

		@Override
		protected Dockable resolveDockable(Object key) {
			if (KEY_KEYBOARD.equals(key)) {
				return keyboardDockable;
			} else if (KEY_PROBLEMS.equals(key)) {
				return problemsDockable;
			} else if (KEY_MIDI_MONITOR.equals(key)) {
				return midiMonitorDockable;
			} else if (KEY_MEMORY.equals(key)) {
				return memoryDockable;
			} else if (constructing) {
				if (KEY_ELEMENTS.equals(key)) {
					return elementsDockable;
				} else if (KEY_REFERENCES.equals(key)) {
					return referencesDockable;
				} else if (KEY_PROPERTIES.equals(key)) {
					return propertiesDockable;
				}
			}
			return null;
		}
	}

	private class BordererDockingPane extends DockingPane {
		@Override
		protected Dock createDockImpl() {
			Dock dock = super.createDockImpl();
			dock.setBorder(new Eclipse3Border());
			return dock;
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
}