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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import jorgan.disposition.Console;
import jorgan.disposition.Organ;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganObserver;
import jorgan.disposition.event.UndoableChange;
import jorgan.gui.preferences.PreferencesDialog;
import jorgan.gui.spi.ActionRegistry;
import jorgan.io.DispositionStream;
import jorgan.io.disposition.ExtensionException;
import jorgan.io.disposition.FormatException;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import jorgan.swing.DebugPanel;
import jorgan.swing.Desktop;
import jorgan.swing.MacAdapter;
import jorgan.swing.StatusBar;
import spin.Spin;
import bias.Configuration;
import bias.swing.MessageBox;
import bias.util.MessageBuilder;

/**
 * The jOrgan frame.
 */
public class OrganFrame extends JFrame implements SessionAware {

	public static final int REGISTRATION_CHANGES_CONFIRM = 0;

	public static final int REGISTRATION_CHANGES_SAVE = 1;

	public static final int REGISTRATION_CHANGES_DISCARD = 2;

	private static Logger logger = Logger.getLogger(OrganFrame.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			OrganFrame.class);

	/**
	 * The suffix used for the frame title.
	 */
	private static final String TITEL_SUFFIX = "jOrgan";

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

	/*
	 * The actions.
	 */
	private DebugAction debugAction = new DebugAction();

	private NewAction newAction = new NewAction();

	private OpenAction openAction = new OpenAction();

	private SaveAction saveAction = new SaveAction();

	private SaveAsAction saveAsAction = new SaveAsAction();

	private ExitAction exitAction = new ExitAction();

	private FullScreenAction fullScreenAction = new FullScreenAction();

	private PreferencesAction configurationAction = new PreferencesAction();

	private WebsiteAction websiteAction = new WebsiteAction();

	private AboutAction aboutAction = new AboutAction();

	private JToggleButton constructButton = new JToggleButton();

	private EventHandler handler = new EventHandler();

	private boolean fullScreenOnLoad = false;

	private int handleRegistrationChanges;

	/**
	 * Create a new organFrame.
	 */
	public OrganFrame() {
		config.read(this);

		// not neccessary for XP but for older Windows LookAndFeel
		if (UIManager.getLookAndFeel().getName().indexOf("Windows") != -1) {
			toolBar.setRollover(true);
		}
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
				close();
			}
		});

		if (MacAdapter.getInstance().isInstalled()) {
			MacAdapter.getInstance().setQuitListener(exitAction);
			MacAdapter.getInstance()
					.setPreferencesListener(configurationAction);
			MacAdapter.getInstance().setAboutListener(aboutAction);
		}

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		// don't build menuBar, will be called automatically for new session

		newOrgan();
	}

	private void buildStatusBar() {
		for (Object widget : organPanel.getStatusBarWidgets()) {
			statusBar.addStatus((JComponent) widget);
		}
	}

	private void buildToolBar() {

		toolBar.add(newAction);
		toolBar.add(openAction);
		toolBar.add(saveAction);

		toolBar.addSeparator();

		config.get("construct").read(constructButton);
		constructButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				session.setConstructing(constructButton.isSelected());
			};
		});
		toolBar.add(constructButton);

		toolBar.addSeparator();

		List<?> toolBarWidgets = organPanel.getToolBarWidgets();
		for (int w = 0; w < toolBarWidgets.size(); w++) {
			if (toolBarWidgets.get(w) instanceof Action) {
				toolBar.add((Action) toolBarWidgets.get(w));
			} else {
				toolBar.add((JComponent) toolBarWidgets.get(w));
			}
		}
	}

	private void buildMenu() {
		JMenuBar menuBar = getJMenuBar();
		menuBar.removeAll();

		JMenu fileMenu = new JMenu();
		config.get("fileMenu").read(fileMenu);
		menuBar.add(fileMenu);
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		JMenu recentsMenu = new JMenu();
		config.get("recentsMenu").read(recentsMenu);
		fileMenu.add(recentsMenu);
		fileMenu.addSeparator();
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);

		List<Action> actions = ActionRegistry.createActions(session, this);
		if (actions.size() > 0) {
			fileMenu.addSeparator();

			for (Action action : actions) {
				fileMenu.add(action);
			}
		}

		if (!MacAdapter.getInstance().isInstalled()) {
			fileMenu.addSeparator();
			fileMenu.add(exitAction);
		}

		JMenu viewMenu = new JMenu();
		config.get("viewMenu").read(viewMenu);
		menuBar.add(viewMenu);
		viewMenu.add(fullScreenAction);
		viewMenu.addSeparator();
		viewMenu.add(debugAction);
		viewMenu.addSeparator();
		for (Object widget : organPanel.getMenuWidgets()) {
			if (widget == null) {
				viewMenu.addSeparator();
			} else {
				viewMenu.add((Action) widget);
			}
		}
		if (!MacAdapter.getInstance().isInstalled()) {
			viewMenu.addSeparator();
			viewMenu.add(configurationAction);
		}

		JMenu helpMenu = new JMenu();
		config.get("helpMenu").read(helpMenu);
		menuBar.add(helpMenu);
		helpMenu.add(websiteAction);
		if (!MacAdapter.getInstance().isInstalled()) {
			helpMenu.add(aboutAction);
		}

		List<File> recents = new DispositionStream().getRecentFiles();
		for (int r = 0; r < recents.size(); r++) {
			recentsMenu.add(new RecentAction(r + 1, recents.get(r)));
		}

		menuBar.revalidate();
		menuBar.repaint();

		if (session.getFile() == null) {
			setTitle(TITEL_SUFFIX);
		} else {
			setTitle(jorgan.io.disposition.DispositionFileFilter
					.removeSuffix(session.getFile())
					+ " - " + TITEL_SUFFIX);
		}
	}

	public boolean getFullScreenOnLoad() {
		return fullScreenOnLoad;
	}

	public void setFullScreenOnLoad(boolean fullScreenOnLoad) {
		this.fullScreenOnLoad = fullScreenOnLoad;
	}

	public int getHandleRegistrationChanges() {
		return handleRegistrationChanges;
	}

	public void setHandleRegistrationChanges(int handleRegistrationChanges) {
		this.handleRegistrationChanges = handleRegistrationChanges;
	}

	/**
	 * Stop.
	 */
	private void close() {
		if (canCloseOrgan()) {
			session.destroy();

			organPanel.closing();

			config.write(this);

			setVisible(false);
		}
	}

	public void setSession(OrganSession session) {
		statusBar.setStatus(null);

		if (this.session != null) {
			this.session.destroy();

			this.session.getOrgan().removeOrganObserver(
					(OrganObserver) Spin.over(saveAction));
			this.session.addListener(handler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganObserver(
					(OrganObserver) Spin.over(saveAction));
			this.session.addListener(handler);

			constructButton.setSelected(this.session.isConstructing());
		}

		saveAction.clearChanges();

		organPanel.setSession(session);

		buildMenu();
	}

	/**
	 * Create a new organ without a file.
	 */
	public void newOrgan() {

		if (canCloseOrgan()) {
			setSession(new OrganSession());
		}
	}

	/**
	 * Open an organ.
	 */
	public void openOrgan() {
		if (canCloseOrgan()) {
			JFileChooser chooser = new JFileChooser(new DispositionStream()
					.getRecentDirectory());
			chooser.setFileFilter(new jorgan.gui.file.DispositionFileFilter());
			if (chooser.showOpenDialog(OrganFrame.this) == JFileChooser.APPROVE_OPTION) {
				openOrgan(chooser.getSelectedFile());
			}
		}
	}

	/**
	 * Open the organ contained in the given file.
	 * 
	 * @param file
	 *            file to open organ from
	 */
	public void openOrgan(File file) {
		// start with empty session in case the following opening fails *or*
		// the new session interferes with the current session
		setSession(new OrganSession());

		Organ organ;
		try {
			organ = new DispositionStream().read(file);
		} catch (ExtensionException ex) {
			logger.log(Level.INFO, ex.getClass().getSimpleName(), ex);

			showBoxMessage("openExtensionException", file.getName(), ex
					.getExtension());
			return;
		} catch (FormatException ex) {
			logger.log(Level.INFO, ex.getClass().getSimpleName(), ex);

			showBoxMessage("openFormatException", file.getName());
			return;
		} catch (IOException ex) {
			showBoxMessage("openIOException", file.getName());
			return;
		}

		setSession(new OrganSession(organ, file));

		if (fullScreenOnLoad) {
			fullScreenAction.goFullScreen();
		}
	}

	/**
	 * Save the current organ.
	 * 
	 * @return was the organ saved
	 */
	public boolean saveOrgan() {
		if (session.getFile() == null) {
			return saveOrganAs();
		} else {
			return saveOrgan(session.getFile());
		}
	}

	/**
	 * Save the current organ to the given file.
	 * 
	 * @param file
	 *            file to save organ to
	 * @return was the organ saved
	 */
	public boolean saveOrgan(File file) {
		try {
			new DispositionStream().write(session.getOrgan(), file);

			showStatusMessage("organSaved", new Object[0]);
		} catch (IOException ex) {
			logger.log(Level.INFO, "saving organ failed", ex);

			showBoxMessage("saveException", file.getName());

			return false;
		}

		session.setFile(file);

		saveAction.clearChanges();

		buildMenu();

		return true;
	}

	/**
	 * Save the current organ.
	 * 
	 * @return was the organ saved
	 */
	public boolean saveOrganAs() {
		JFileChooser chooser = new JFileChooser(new DispositionStream()
				.getRecentDirectory());
		chooser.setFileFilter(new jorgan.gui.file.DispositionFileFilter());
		if (chooser.showSaveDialog(OrganFrame.this) == JFileChooser.APPROVE_OPTION) {
			File file = jorgan.io.disposition.DispositionFileFilter
					.addSuffix(chooser.getSelectedFile());

			MessageBox box = new MessageBox(MessageBox.OPTIONS_YES_NO);
			config.get("saveOrganAsConfirmReplace").read(box);
			if (!file.exists() || box.show(this) == MessageBox.OPTION_YES) {
				return saveOrgan(file);
			}
		}
		return false;
	}

	/**
	 * Can the current organ be closed.
	 * 
	 * @return <code>true</code> if organ can be closed
	 */
	public boolean canCloseOrgan() {
		if (saveAction.mustSave()) {
			if (saveAction.mustConfirm()) {
				MessageBox box = new MessageBox(
						MessageBox.OPTIONS_YES_NO_CANCEL);
				config.get("closeOrganConfirmChanges").read(box);
				int option = box.show(this);
				if (option == MessageBox.OPTION_CANCEL) {
					return false;
				} else if (option == MessageBox.OPTION_NO) {
					return true;
				}
			}
			return saveOrgan();
		}

		return true;
	}

	protected void showStatusMessage(String key, Object... args) {

		statusBar.setStatus(config.get(key).read(new MessageBuilder()).build(
				args));
	}

	protected void showBoxMessage(String key, Object... args) {

		config.get(key).read(new MessageBox(MessageBox.OPTIONS_OK)).show(this,
				args);
	}

	/**
	 * The action that initiates a new organ.
	 */
	private class NewAction extends BaseAction {
		private NewAction() {
			config.get("new").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			newOrgan();
		}
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
			if (canCloseOrgan()) {
				openOrgan(new File((String) getValue(Action.SHORT_DESCRIPTION)));
			}
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
	private class OpenAction extends BaseAction {
		private OpenAction() {
			config.get("open").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			openOrgan();
		}
	}

	/**
	 * The action that saves an organ. <br/> Note that <em>Spin</em> ensures
	 * that the methods of this listeners are called on the EDT, although a
	 * change in the organ might be triggered by a change on a MIDI thread.
	 */
	private class SaveAction extends BaseAction implements OrganObserver {

		private boolean changes = false;

		private boolean undoableChanges = false;

		private SaveAction() {
			config.get("save").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			saveOrgan();
		}

		public boolean mustSave() {
			return undoableChanges
					|| (changes && (handleRegistrationChanges != REGISTRATION_CHANGES_DISCARD));
		}

		public boolean mustConfirm() {
			return undoableChanges
					|| (handleRegistrationChanges == REGISTRATION_CHANGES_CONFIRM);
		}

		/**
		 * Clear changes.
		 */
		public void clearChanges() {
			changes = false;
			undoableChanges = false;

			setEnabled(session.getFile() == null);
		}

		public void beforeChange(Change change) {
		}

		public void afterChange(Change change) {
			changes = true;
			if (change instanceof UndoableChange) {
				undoableChanges = true;
			}

			setEnabled(true);
		}
	}

	/**
	 * The action that saves an organ under a new name.
	 */
	private class SaveAsAction extends BaseAction {
		private SaveAsAction() {
			config.get("saveAs").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			saveOrganAs();
		}
	}

	/**
	 * The action that shows information about jOrgan.
	 */
	private class AboutAction extends BaseAction {
		private AboutAction() {
			config.get("about").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			AboutPanel.showInDialog(OrganFrame.this);
		}
	}

	/**
	 * The action that shows the jOrgan website.
	 */
	private class WebsiteAction extends BaseAction {
		private WebsiteAction() {
			config.get("website").read(this);

			setEnabled(Desktop.isSupported());
		}

		public void actionPerformed(ActionEvent ev) {
			Desktop.browse("http://jorgan.sourceforge.net");
		}
	}

	/**
	 * The action that shows the preferences.
	 */
	private class PreferencesAction extends BaseAction {
		private PreferencesAction() {
			config.get("preferences").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			PreferencesDialog.show(OrganFrame.this);
		}
	}

	/**
	 * The action that initiates full screen.
	 */
	private class FullScreenAction extends BaseAction implements
			ComponentListener {

		private Map<String, ConsoleDialog> dialogs = new HashMap<String, ConsoleDialog>();

		private FullScreenAction() {
			config.get("fullScreen").read(this);

			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), this);
			getRootPane().getActionMap().put(this, this);
		}

		public void actionPerformed(ActionEvent ev) {
			if (dialogs.isEmpty()) {
				goFullScreen();

				if (dialogs.isEmpty()) {
					showBoxMessage("noFullScreen");
				}
			}
		}

		private void goFullScreen() {
			for (Console console : session.getOrgan()
					.getElements(Console.class)) {
				String screen = console.getScreen();
				if (screen == null) {
					continue;
				}

				ConsoleDialog dialog = dialogs.get(screen);
				if (dialog == null) {
					dialog = ConsoleDialog.create(OrganFrame.this, session,
							screen);
					dialogs.put(screen, dialog);
				}
				dialog.addConsole(console);
				dialog.addComponentListener(this);
				dialog.setVisible(true);
			}
		}

		public void componentHidden(ComponentEvent e) {
			Iterator<ConsoleDialog> iterator = dialogs.values().iterator();
			while (iterator.hasNext()) {
				ConsoleDialog dialog = iterator.next();
				dialog.setVisible(false);
				dialog.dispose();
			}
			dialogs.clear();
		}

		public void componentMoved(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {
		}

		public void componentShown(ComponentEvent e) {
		}
	}

	/**
	 * The action that exits jOrgan.
	 */
	private class ExitAction extends BaseAction {
		private ExitAction() {
			config.get("exit").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			close();
		}
	}

	private class EventHandler implements SessionListener {
		public void constructingChanged(boolean constructing) {
			constructButton.setSelected(constructing);
		}

		public void destroyed() {
		}
	}
}