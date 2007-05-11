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
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.imports.ImportWizard;
import jorgan.gui.preferences.PreferencesDialog;
import jorgan.io.DispositionStream;
import jorgan.swing.BaseAction;
import jorgan.swing.Browser;
import jorgan.swing.DebugPanel;
import jorgan.swing.StatusBar;
import jorgan.swing.TweakMac;
import bias.Configuration;
import bias.swing.MessageBox;
import bias.util.MessageBuilder;

/**
 * The jOrgan frame.
 */
public class OrganFrame extends JFrame {

	private static Logger logger = Logger.getLogger(OrganFrame.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			OrganFrame.class);

	public static final int REGISTRATION_CHANGES_CONFIRM = 0;

	public static final int REGISTRATION_CHANGES_SAVE = 1;

	public static final int REGISTRATION_CHANGES_IGNORE = 2;

	/**
	 * The suffix used for the frame title.
	 */
	private static final String TITEL_SUFFIX = "jOrgan";

	/**
	 * Tweak Mac Os X appearance.
	 */
	private TweakMac tweakMac;

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
	 * The file the current organ is associated with.
	 */
	private File file;

	/**
	 * The organ session.
	 */
	private OrganSession session = new OrganSession();

	/*
	 * The actions.
	 */
	private DebugAction debugAction = new DebugAction();

	private NewAction newAction = new NewAction();

	private OpenAction openAction = new OpenAction();

	private SaveAction saveAction = new SaveAction();

	private SaveAsAction saveAsAction = new SaveAsAction();

	private ImportAction importAction = new ImportAction();

	private ExitAction exitAction = new ExitAction();

	private FullScreenAction fullScreenAction = new FullScreenAction();

	private PreferencesAction configurationAction = new PreferencesAction();

	private WebsiteAction websiteAction = new WebsiteAction();

	private AboutAction aboutAction = new AboutAction();

	private JToggleButton constructButton = new JToggleButton();

	private JMenu recentsMenu = new JMenu();

	private boolean fullScreenOnLoad = false;

	private int handleRegistrationChanges;

	/**
	 * Create a new organFrame.
	 */
	public OrganFrame() {
		config.read(this);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// not neccessary for XP but for older Windows LookAndFeel
		if (UIManager.getLookAndFeel().getName().indexOf("Windows") != -1) {
			toolBar.setRollover(true);
		}
		toolBar.add(newAction);
		toolBar.add(openAction);
		toolBar.add(saveAction);

		toolBar.addSeparator();

		config.get("construct").read(constructButton);
		constructButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (constructButton.isSelected()) {
					session.getPlay().close();
				} else {
					session.getPlay().open();
				}
			};
		});
		toolBar.add(constructButton);

		toolBar.addSeparator();

		List toolBarWidgets = organPanel.getToolBarWidgets();
		for (int w = 0; w < toolBarWidgets.size(); w++) {
			if (toolBarWidgets.get(w) instanceof Action) {
				toolBar.add((Action) toolBarWidgets.get(w));
			} else {
				toolBar.add((JComponent) toolBarWidgets.get(w));
			}
		}

		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		List statusBarWidgets = organPanel.getStatusBarWidgets();
		for (int w = 0; w < statusBarWidgets.size(); w++) {
			statusBar.addStatus((JComponent) statusBarWidgets.get(w));
		}
		getContentPane().add(statusBar, BorderLayout.SOUTH);

		getContentPane().add(organPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				close();
			}
		});

		tweakMac = new TweakMac(configurationAction, aboutAction, exitAction);

		JMenu fileMenu = new JMenu();
		config.get("fileMenu").read(fileMenu);
		menuBar.add(fileMenu);
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		config.get("recentsMenu").read(recentsMenu);
		fileMenu.add(recentsMenu);
		fileMenu.addSeparator();
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();
		fileMenu.add(importAction);
		if (!tweakMac.isTweaked()) {
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
		List actions = organPanel.getMenuWidgets();
		for (int a = 0; a < actions.size(); a++) {
			viewMenu.add((Action) actions.get(a));
		}
		if (!tweakMac.isTweaked()) {
			viewMenu.addSeparator();
			viewMenu.add(configurationAction);
		}

		JMenu helpMenu = new JMenu();
		config.get("helpMenu").read(helpMenu);
		menuBar.add(helpMenu);
		helpMenu.add(websiteAction);
		helpMenu.add(aboutAction);

		buildRecentsMenu();

		newOrgan();
	}

	private void buildRecentsMenu() {
		recentsMenu.removeAll();

		List<File> recents = new DispositionStream().getRecentFiles();
		for (int r = 0; r < recents.size(); r++) {
			recentsMenu.add(new RecentAction(r + 1, recents.get(r)));
		}

		recentsMenu.revalidate();
		recentsMenu.repaint();
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
	 * Set the file of the current organ.
	 */
	private void setFile(File file) {

		this.file = file;
		if (file == null) {
			setTitle(TITEL_SUFFIX);
		} else {
			setTitle(jorgan.io.DispositionFileFilter.removeSuffix(file) + " - "
					+ TITEL_SUFFIX);
		}
	}

	/**
	 * Stop.
	 */
	private void close() {
		if (canCloseOrgan()) {
			if (session.getPlay().isOpen()) {
				session.getPlay().close();
			}
			session.getPlay().dispose();

			config.write(this);

			setVisible(false);
		}
	}

	/**
	 * Set the organ edited by this frame.
	 * 
	 * @param session
	 *            organ to be edited
	 */
	private void setOrgan(OrganSession session) {
		statusBar.setStatus(null);

		if (this.session != null) {
			if (this.session.getPlay().isOpen()) {
				this.session.getPlay().close();
			}
			this.session.getPlay().dispose();

			this.session.removeOrganListener(saveAction);
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(saveAction);

			if (!constructButton.isSelected()) {
				session.getPlay().open();
			}
		}

		saveAction.clearChanges();

		organPanel.setOrgan(session);
	}

	/**
	 * Create a new organ without a file.
	 */
	public void newOrgan() {

		if (canCloseOrgan()) {
			setFile(null);

			setOrgan(new OrganSession());
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
		try {
			Organ organ = new DispositionStream().read(file);

			setFile(file);

			setOrgan(new OrganSession(organ));

			buildRecentsMenu();
			if (fullScreenOnLoad) {
				fullScreenAction.goFullScreen();
			}
		} catch (IOException ex) {
			showBoxMessage("openOrganException", file.getName());
		} catch (Exception ex) {
			logger.log(Level.INFO, "opening organ failed", ex);

			showBoxMessage("openOrganInvalid", file.getName());
		}
	}

	/**
	 * Save the current organ.
	 * 
	 * @return was the organ saved
	 */
	public boolean saveOrgan() {
		if (file == null) {
			return saveOrganAs();
		} else {
			return saveOrgan(file);
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

			setFile(file);

			saveAction.clearChanges();

			buildRecentsMenu();
			showStatusMessage("organSaved", new Object[0]);
		} catch (IOException ex) {
			logger.log(Level.INFO, "saving organ failed", ex);

			showBoxMessage("saveOrganException", file.getName());

			return false;
		} catch (Exception ex) {
			logger.log(Level.INFO, "saving organ failed", ex);

			showBoxMessage("saveOrganInvalid", file.getName());

			return false;
		}
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
			File file = jorgan.io.DispositionFileFilter.addSuffix(chooser
					.getSelectedFile());

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
		if (saveAction.hasChanges()) {
			if (saveAction.confirmChanges()) {
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

		config.get(key).read(new MessageBox(MessageBox.OPTIONS_OK)).show(this);
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
	private class SaveAction extends BaseAction implements OrganListener {

		private boolean dispositionChanges = false;

		private boolean registrationChanges = false;

		private SaveAction() {
			config.get("save").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			saveOrgan();
		}

		/**
		 * Are changes known.
		 * 
		 * @return <code>true</code> if changes are known
		 */
		public boolean hasChanges() {
			return dispositionChanges || registrationChanges
					&& handleRegistrationChanges != REGISTRATION_CHANGES_IGNORE;
		}

		/**
		 * Are changes to be confirmed.
		 * 
		 * @return <code>true</code> if changes are to be confirmed
		 */
		public boolean confirmChanges() {
			return dispositionChanges
					|| handleRegistrationChanges == REGISTRATION_CHANGES_CONFIRM;
		}

		/**
		 * Clear changes.
		 */
		public void clearChanges() {
			dispositionChanges = false;
			registrationChanges = false;

			setEnabled(file == null);
		}

		public void elementChanged(OrganEvent event) {
			analyseEvent(event);
		}

		public void elementAdded(OrganEvent event) {
			analyseEvent(event);
		}

		public void elementRemoved(OrganEvent event) {
			analyseEvent(event);
		}

		public void referenceAdded(OrganEvent event) {
			analyseEvent(event);
		}

		public void referenceChanged(OrganEvent event) {
			analyseEvent(event);
		}

		public void referenceRemoved(OrganEvent event) {
			analyseEvent(event);
		}

		private void analyseEvent(final OrganEvent event) {
			if (event.isDispositionChange()) {
				dispositionChanges = true;
			} else {
				registrationChanges = true;
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

			setEnabled(Browser.isSupported());
		}

		public void actionPerformed(ActionEvent ev) {
			Browser.open("http://jorgan.sourceforge.net");
		}
	}

	/**
	 * The action that starts an import.
	 */
	private class ImportAction extends BaseAction {
		private ImportAction() {
			config.get("import").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			ImportWizard.showInDialog(OrganFrame.this, session.getOrgan());
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
			List consoles = session.getOrgan().getElements(Console.class);
			for (int c = 0; c < consoles.size(); c++) {
				Console console = (Console) consoles.get(c);
				String screen = console.getScreen();
				if (screen == null) {
					continue;
				}

				ConsoleDialog dialog = dialogs.get(screen);
				if (dialog == null) {
					dialog = ConsoleDialog.create(OrganFrame.this, screen);
					dialog.setOrgan(session);
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
				dialog.setOrgan(null);
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
}