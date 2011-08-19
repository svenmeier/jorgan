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
import jorgan.gui.console.ConsoleStack;
import jorgan.gui.dock.AbstractEditor;
import jorgan.gui.dock.AbstractView;
import jorgan.gui.dock.BordererDockingPane;
import jorgan.gui.dock.spi.EditorRegistry;
import jorgan.gui.dock.spi.ViewRegistry;
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
import jorgan.util.ReverseIterable;
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

	private ResetAction resetAction = new ResetAction();

	private boolean constructing = false;

	/**
	 * The organ.
	 */
	private OrganSession session;

	/**
	 * The listener to events.
	 */
	private EventsListener eventsListener = new EventsListener();

	private DockingPane views = new BordererDockingPane() {
		@Override
		protected JComponent createComponent(Object key) {
			if ("consoles".equals(key)) {
				return editors;
			} else {
				return null;
			}
		}

		@Override
		protected Dockable createDockable(Object key) {
			for (ViewAction action : viewActions) {
				AbstractView dockable = action.getView();
				if (dockable.getKey().equals(key)) {
					dockable.setSession(session);

					return dockable;
				}
			}
			return null;
		}

		@Override
		protected void dismissDockable(Dockable dockable) {
			((AbstractView) dockable).setSession(null);
		};
	};

	private Set<ViewAction> viewActions = new HashSet<ViewAction>();

	/*
	 * The inner dockingPane holding all editors.
	 */
	private DockingPane editors = new BordererDockingPane() {
		@Override
		protected void dismissDockable(Dockable dockable) {
			((AbstractEditor) dockable).setSession(null);
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

		views.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(views, BorderLayout.CENTER);

		for (AbstractView view : ViewRegistry.getViews()) {
			viewActions.add(new ViewAction(view));
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
		List<Object> menu = new ArrayList<Object>(viewActions);
		menu.add(null);
		menu.add(resetAction);
		return menu;
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
			this.session.removeListener((SessionListener) Spin
					.over(eventsListener));

			for (Object key : views.getDockableKeys()) {
				AbstractView view = (AbstractView) views.getDockable(key);
				if (view != null) {
					view.setSession(null);
				}
			}

			for (Object key : editors.getDockableKeys()) {
				AbstractEditor editor = (AbstractEditor) editors
						.removeDockable(key);
				if (editor != null) {
					editor.setSession(null);
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

			for (Object key : views.getDockableKeys()) {
				AbstractView view = (AbstractView) views.getDockable(key);
				if (view != null) {
					view.setSession(this.session);
				}
			}

			// alphabetically front to back
			for (Element element : new ReverseIterable<Element>(session
					.getOrgan().getElements(Element.class))) {
				addEditor(element);
			}
		}
	}

	protected void updateActions() {
		for (ViewAction action : viewActions) {
			action.update();
		}
	}

	protected void addEditor(Element element) {
		AbstractEditor editor = EditorRegistry.getEditor(element);
		if (editor != null) {
			editors.putDockable(element, editor);
			editor.setSession(this.session);
		}
	}

	protected void removeEditor(Element element) {
		AbstractEditor editor = (AbstractEditor) editors.getDockable(element);
		if (editor != null) {
			editor.setSession(null);
			editors.removeDockable(element);
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
				new XMLPersister(this.views, reader, DOCKING_VERSION).load();
				return;
			} catch (Exception ex) {
				logger.log(Level.WARNING, "unable to load docking", ex);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}

		loadDefaultDocking();
	}

	private void loadDefaultDocking() {
		String dockingXml;
		if (constructing) {
			dockingXml = "construct.xml";
		} else {
			dockingXml = "play.xml";
		}
		Reader reader = new InputStreamReader(getClass().getResourceAsStream(
				dockingXml));
		try {
			new XMLPersister(this.views, reader, DOCKING_VERSION).load();
		} catch (Exception error) {
			throw new Error("unable to load default docking");
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	protected void saveDocking() {
		Writer writer = new StringWriter();
		try {
			new XMLPersister(views, writer, DOCKING_VERSION).save();
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
		AbstractEditor editor = (AbstractEditor) editors.getDockable(console);
		if (editor == null) {
			addEditor(console);
		} else {
			editors.putDockable(console, editor);
		}
	}

	/**
	 * The listener to events.
	 */
	private class EventsListener extends OrganAdapter implements PlayListener,
			ProblemListener, SelectionListener, SessionListener {

		@Override
		public void received(Element element, MidiMessage message) {
			messagesMonitor.input();
		}

		@Override
		public void sent(Element element, MidiMessage message) {
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

				if (editors.getDockable(element) == null) {
					addEditor(element);
				}
			}
		}

		public void modified() {
		}

		public void elementAdded(Element element) {
			if (element instanceof Console) {
				addEditor((Console) element);
			}
		}

		public void elementRemoved(Element element) {
			AbstractEditor editor = (AbstractEditor) editors
					.getDockable(element);
			if (editor != null) {
				removeEditor(element);
			}
		}
	}

	private class ViewAction extends BaseAction {

		private AbstractView view;

		public ViewAction(AbstractView view) {
			this.view = view;

			setName(view.getTitle());
			setSmallIcon(view.getIcon());

			update();
		}

		public AbstractView getView() {
			return view;
		}

		public void update() {
			if (session != null && constructing) {
				setEnabled(view.forConstruct());
			} else {
				setEnabled(view.forPlay());
			}
		}

		public void actionPerformed(ActionEvent ev) {
			view.setSession(session);

			views.putDockable(view.getKey(), view);
		}
	}

	private class ResetAction extends BaseAction {

		private ResetAction() {
			config.get("reset").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			loadDefaultDocking();
		}
	}
}