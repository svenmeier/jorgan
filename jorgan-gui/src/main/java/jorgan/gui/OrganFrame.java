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
import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import bias.Configuration;
import bias.swing.MessageBox;
import bias.util.MessageBuilder;
import jorgan.Version;
import jorgan.gui.action.spi.ActionRegistry;
import jorgan.gui.file.DispositionFileFilter;
import jorgan.gui.preferences.PreferencesDialog;
import jorgan.gui.undo.UndoManager;
import jorgan.io.disposition.ExtensionException;
import jorgan.io.disposition.FormatException;
import jorgan.session.History;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import jorgan.swing.DebugPanel;
import jorgan.swing.StatusBar;
import spin.Spin;

/**
 * The jOrgan frame.
 */
public class OrganFrame extends JFrame implements SessionAware {

	private static Logger logger = Logger.getLogger(OrganFrame.class.getName());

	private static Configuration config = Configuration.getRoot()
			.get(OrganFrame.class);

	/**
	 * The toolBar of this frame.
	 */
	private JToolBar toolBar = new JToolBar();

	/**
	 * The organPanel of this frame.
	 */
	private OrganPanel organPanel = new OrganPanel();

	/**
	 * The statusBar of this frame.
	 */
	private StatusBar statusBar = new StatusBar();

	/**
	 * The organ session.
	 */
	private OrganSession session;

	private DebugAction debugAction = new DebugAction();

	private NewAction newAction = new NewAction();

	private OpenAction openAction = new OpenAction();

	private SaveAction saveAction = new SaveAction();

	private CloseAction closeAction = new CloseAction();

	private ExitAction exitAction = new ExitAction();

	private PreferencesAction configurationAction = new PreferencesAction();

	private WebsiteAction websiteAction = new WebsiteAction();

	private AboutAction aboutAction = new AboutAction();

	private ConstructAction constructAction = new ConstructAction();

	private EventHandler handler = new EventHandler();

	private Changes changes = Changes.CONFIRM;

	/**
	 * Create a new organFrame.
	 */
	public OrganFrame() {
		config.read(this);

		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		buildToolBar();

		getContentPane().add(statusBar, BorderLayout.SOUTH);
		buildStatusBar();

		getContentPane().add(organPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				exit();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		buildMenu();
	}

	private void buildStatusBar() {
		for (Object widget : organPanel.getStatusBarWidgets()) {
			statusBar.addStatus((JComponent) widget);
		}
	}

	private void buildToolBar() {
		toolBar.removeAll();

		toolBar.add(openAction);
		toolBar.add(saveAction);

		if (session != null) {
			toolBar.addSeparator();
			JToggleButton toggle = new JToggleButton(constructAction);
			toggle.setText(null);
			toolBar.add(toggle);

			List<Action> actions = ActionRegistry.createToolbarActions(session,
					this);
			if (actions.size() > 0) {
				toolBar.addSeparator();

				for (Action action : actions) {
					toolBar.add(action);
				}
			}
		}

		toolBar.repaint();
		toolBar.revalidate();
	}

	private void buildMenu() {
		JMenuBar menuBar = getJMenuBar();
		menuBar.removeAll();

		// Prepare menus
		JMenu fileMenu = new JMenu();
		config.get("fileMenu").read(fileMenu);
		menuBar.add(fileMenu);

		JMenu editMenu = new JMenu();
		config.get("editMenu").read(editMenu);
		menuBar.add(editMenu);

		JMenu viewMenu = new JMenu();
		config.get("viewMenu").read(viewMenu);
		menuBar.add(viewMenu);

		JMenu helpMenu = new JMenu();
		config.get("helpMenu").read(helpMenu);
		menuBar.add(helpMenu);

		menuBar.revalidate();
		menuBar.repaint();

		// Add first elements in menus
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		JMenu recentsMenu = new JMenu();
		config.get("recentsMenu").read(recentsMenu);
		List<File> recents = new History().getRecentFiles();
		for (int r = 0; r < recents.size(); r++) {
			recentsMenu.add(new RecentAction(r + 1, recents.get(r)));
		}
		fileMenu.add(recentsMenu);
		fileMenu.addSeparator();
		fileMenu.add(saveAction);
		fileMenu.add(closeAction);

		viewMenu.add(debugAction);
		viewMenu.addSeparator();
		for (Action action : organPanel.getViewActions()) {
			viewMenu.add(action);
		}

		helpMenu.add(websiteAction);
		helpMenu.add(aboutAction);

		// Add elements if a session if open
		if (session != null) {
			List<Action> actions = ActionRegistry.createMenuActions(session, this);
			if (actions.size() > 0) {
				fileMenu.addSeparator();
				// Rules:
				// * in file menu :
				//   * first import,
				//   * then export,
				//   * then everything but customize or full screen,
				//   * finally customize
				// * in view menu : full screen
				for (Action action : actions) {
					if (action.getClass().getSimpleName().equals("FullScreenAction")) {
						viewMenu.addSeparator();
						viewMenu.add(action);
						actions.remove(action);
						break;
					}
				}
				for (Action action : actions) {
					if (action.getClass().getSimpleName().equals("ImportAction")) {
						fileMenu.add(action);
						actions.remove(action);
						break;
					}
				}
				for (Action action : actions) {
					if (action.getClass().getSimpleName().equals("ExportAction")) {
						fileMenu.add(action);
						actions.remove(action);
						break;
					}
				}
				for (Action action : actions) {
					if (!action.getClass().getSimpleName().equals("CustomizeAction")) {
						fileMenu.add(action);
					}
				}
				for (Action action : actions) {
					if (action.getClass().getSimpleName().equals("CustomizeAction")) {
						fileMenu.add(action);
					}
				}
			}

			editMenu.add(new JCheckBoxMenuItem(constructAction));
			editMenu.addSeparator();
			for (Action action : ActionRegistry.createToolbarActions(session,
					this)) {
				editMenu.add(action);
			}
			editMenu.addSeparator();
		}

		// Add last elements
		fileMenu.addSeparator();
		fileMenu.add(exitAction);

		editMenu.add(configurationAction);

		String title;
		if (session == null) {
			title = config.get("titleNoSession").read(new MessageBuilder())
					.build(new Version().get());
		} else {
			title = config.get("titleSession").read(new MessageBuilder()).build(
					new Version().get(),
					DispositionFileFilter.removeSuffix(session.getFile()));
		}
		setTitle(title);
	}

	public Changes getChanges() {
		return changes;
	}

	public void setChanges(Changes changes) {
		this.changes = changes;
	}

	/**
	 * Stop.
	 */
	private void exit() {
		if (closeOrgan()) {
			organPanel.closing();

			config.write(this);

			setVisible(false);
			dispose();
		}
	}

	public void setSession(OrganSession session) {
		statusBar.setStatus(null);

		if (this.session != null) {
			this.session.destroy();

			this.session.removeListener((SessionListener) Spin.over(handler));
		}

		this.session = session;

		if (this.session != null) {
			this.session.addListener((SessionListener) Spin.over(handler));
		}

		constructAction.setSelected(
				this.session != null && this.session.isConstructing());

		saveAction.onSession();
		closeAction.onSession();

		organPanel.setSession(session);

		buildToolBar();
		buildMenu();
	}

	/**
	 * Open the organ contained in the given file.
	 * 
	 * @param file
	 *            file to open organ from
	 */
	public void openOrgan(File file) {
		setSession(null);

		OrganSession session;
		try {
			session = new OrganSession(file);
		} catch (ExtensionException ex) {
			showBoxMessage("openExtensionException", MessageBox.OPTIONS_OK,
					file.getName(), ex.getExtension());
			return;
		} catch (FormatException ex) {
			logger.log(Level.INFO, ex.getClass().getSimpleName(), ex);

			showBoxMessage("openFormatException", MessageBox.OPTIONS_OK,
					file.getName());
			return;
		} catch (IOException ex) {
			showBoxMessage("openIOException", MessageBox.OPTIONS_OK,
					file.getName());
			return;
		}

		String version = session.getOrgan().getVersion();
		if (!version.isEmpty() && !new Version().isCompatible(version)) {
			int option = showBoxMessage("openConversion",
					MessageBox.OPTIONS_OK_CANCEL, version);
			if (option != MessageBox.OPTION_OK) {
				session.destroy();
				return;
			}
		}

		setSession(session);
	}

	/**
	 * Save the current organ.
	 * 
	 * @return was the organ saved
	 */
	public boolean saveOrgan() {
		try {
			session.save();

			showStatusMessage("organSaved");
		} catch (IOException ex) {
			logger.log(Level.INFO, "saving organ failed", ex);

			showBoxMessage("saveIOException", MessageBox.OPTIONS_OK,
					session.getFile().getName());

			return false;
		}

		return true;
	}

	/**
	 * Close the current organ.
	 * 
	 * @return <code>true</code> if organ was closed
	 */
	public boolean closeOrgan() {
		if (session != null && session.isModified()) {
			if (!changes.onClose(this, session)) {
				return false;
			}
		}

		setSession(null);

		return true;
	}

	protected void showStatusMessage(String key, Object... args) {

		if (key == null) {
			statusBar.setStatus(null);
		} else {
			statusBar.setStatus(
					config.get(key).read(new MessageBuilder()).build(args));
		}
	}

	protected int showBoxMessage(String key, int options, Object... args) {

		return config.get(key).read(new MessageBox(options)).show(this, args);
	}

	/**
	 * The action that opens the recent organ.
	 */
	private class RecentAction extends BaseAction {
		private RecentAction(int number, File file) {
			String name = file.getPath();

			putValue(Action.SHORT_DESCRIPTION, name);
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_0 + number));

			int index = name.indexOf(File.separatorChar);
			int lastIndex = name.lastIndexOf(File.separatorChar);
			if (index != lastIndex) {
				name = name.substring(0, index + 1) + "..."
						+ name.substring(lastIndex);
			}
			putValue(Action.NAME, "" + (number) + " " + name);
		}

		public void actionPerformed(ActionEvent ev) {
			if (!closeOrgan()) {
				return;
			}

			File file = new File((String) getValue(Action.SHORT_DESCRIPTION));
			if (!file.exists()) {
				showBoxMessage("openIOException", MessageBox.OPTIONS_OK,
						file.getName());

				return;
			}

			openOrgan(file);
		}
	}

	private class DebugAction extends BaseAction {

		private DebugPanel debugPanel = new DebugPanel();

		private DebugAction() {
			config.get("debug").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			debugPanel.showInDialog(OrganFrame.this);
		}
	}

	/**
	 * The action that opens an organ.
	 */
	private class OpenAction extends BaseAction implements OpenFilesHandler {
		private OpenAction() {
			config.get("open").read(this);

			withDesktop(desktop -> desktop.setOpenFileHandler(this));
		}

		public void actionPerformed(ActionEvent ev) {
			if (!closeOrgan()) {
				return;
			}

			JFileChooser chooser = new JFileChooser(
					new History().getRecentDirectory());
			chooser.setFileFilter(new jorgan.gui.file.DispositionFileFilter());
			if (chooser.showOpenDialog(
					OrganFrame.this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (!file.exists()) {
					showBoxMessage("openNotExists", MessageBox.OPTIONS_OK,
							file.getName());
					return;
				}
				openOrgan(file);
			}
		}

		@Override
		public void openFiles(OpenFilesEvent e) {
			if (!closeOrgan()) {
				return;
			}

			List<File> files = e.getFiles();
			if (files != null && files.size() == 1) {
				openOrgan(files.get(0));
			}
		}
	}

	/**
	 * The action that creates an organ.
	 */
	private class NewAction extends BaseAction {
		private NewAction() {
			config.get("new").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			if (!closeOrgan()) {
				return;
			}

			JFileChooser chooser = new JFileChooser(
					new History().getRecentDirectory());
			chooser.setFileFilter(new jorgan.gui.file.DispositionFileFilter());
			config.get("new/chooser").read(chooser);
			if (chooser.showSaveDialog(
					OrganFrame.this) == JFileChooser.APPROVE_OPTION) {
				File file = DispositionFileFilter
						.addSuffix(chooser.getSelectedFile());
				if (file.exists()) {
					showBoxMessage("newExists", MessageBox.OPTIONS_OK,
							file.getName());
					return;
				}
				openOrgan(file);
			}
		}
	}

	private class CloseAction extends BaseAction {
		private CloseAction() {
			config.get("close").read(this);

			setEnabled(false);
		}

		public void onSession() {
			setEnabled(session != null);
		}

		public void actionPerformed(ActionEvent ev) {
			closeOrgan();
		}
	}

	private class SaveAction extends BaseAction {

		private SaveAction() {
			config.get("save").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			saveOrgan();
		}

		public void onSession() {
			boolean modified = session != null && session.isModified();
			setEnabled(modified);

			// for OS X
			getRootPane().putClientProperty("Window.documentModified",
					Boolean.valueOf(modified));

			statusBar.setStatus(null);
		}
	}

	/**
	 * The action that shows information about jOrgan.
	 */
	private class AboutAction extends BaseAction implements AboutHandler {
		private AboutAction() {
			config.get("about").read(this);

			withDesktop(desktop -> desktop.setAboutHandler(this));
		}

		public void actionPerformed(ActionEvent ev) {
			AboutPanel.showInDialog(OrganFrame.this);
		}

		@Override
		public void handleAbout(AboutEvent e) {
			AboutPanel.showInDialog(OrganFrame.this);
		}
	}

	/**
	 * The action that shows the jOrgan website.
	 */
	private class WebsiteAction extends BaseAction {
		private WebsiteAction() {
			config.get("website").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			withDesktop(desktop -> desktop
					.browse(URI.create("http://jorgan.sourceforge.net")));
		}
	}

	/**
	 * The action that shows the preferences.
	 */
	private class PreferencesAction extends BaseAction
			implements PreferencesHandler {
		private PreferencesAction() {
			config.get("preferences").read(this);

			withDesktop(desktop -> desktop.setPreferencesHandler(this));
		}

		public void actionPerformed(ActionEvent ev) {
			PreferencesDialog.show(OrganFrame.this);
		}

		@Override
		public void handlePreferences(PreferencesEvent e) {
			PreferencesDialog.show(OrganFrame.this);
		}
	}

	/**
	 * The action that exits jOrgan.
	 */
	private class ExitAction extends BaseAction implements QuitHandler {
		private ExitAction() {
			config.get("exit").read(this);

			withDesktop(desktop -> desktop.setQuitHandler(this));
		}

		public void actionPerformed(ActionEvent ev) {
			exit();
		}

		@Override
		public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
			response.cancelQuit();

			exit();
		}
	}

	private class ConstructAction extends BaseAction {
		public ConstructAction() {
			config.get("construct").read(this);

			setSelected(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (session != null) {
				session.setConstructing(isSelected());
			}
		}
	}

	private class EventHandler implements SessionListener {
		public void constructingChanged(boolean constructing) {
			constructAction.setSelected(constructing);
		}

		public void modified() {
			saveAction.onSession();
		}

		public void saved(File file) {
			saveAction.onSession();
		}

		public void destroyed() {
		}
	}

	public static enum Changes {
		DISCARD {
			public boolean onClose(OrganFrame frame, OrganSession session) {
				return true;
			}
		},

		SAVE_REGISTRATIONS {
			public boolean onClose(OrganFrame frame, OrganSession session) {
				if (session.lookup(UndoManager.class).canUndo()) {
					return CONFIRM.onClose(frame, session);
				} else {
					return frame.saveOrgan();
				}
			}
		},
		CONFIRM {
			public boolean onClose(OrganFrame frame, OrganSession session) {
				int option = frame.showBoxMessage("closeOrganConfirmChanges",
						MessageBox.OPTIONS_YES_NO_CANCEL);
				if (option == MessageBox.OPTION_YES) {
					return frame.saveOrgan();
				} else if (option == MessageBox.OPTION_NO) {
					return true;
				}

				return false;
			}
		},
		SAVE {
			public boolean onClose(OrganFrame frame, OrganSession session) {
				return frame.saveOrgan();
			}
		};

		public abstract boolean onClose(OrganFrame frame, OrganSession session);
	}

	private static boolean withDesktop(DesktopConsumer consumer) {
		try {
			consumer.accept(Desktop.getDesktop());

			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
		}

		return false;
	}

	interface DesktopConsumer {
		void accept(Desktop desktop) throws Exception;
	}
}