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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import jorgan.config.ConfigurationEvent;
import jorgan.config.ConfigurationListener;
import jorgan.disposition.Console;
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.config.ConfigurationDialog;
import jorgan.gui.imports.ImportWizard;
import jorgan.io.DispositionReader;
import jorgan.io.DispositionWriter;
import jorgan.io.disposition.History;
import jorgan.swing.DebugPanel;
import jorgan.swing.StatusBar;
import jorgan.swing.TweakMac;
import jorgan.util.I18N;
import jorgan.xml.XMLFormatException;

/**
 * The jOrgan frame.
 */
public class OrganFrame extends JFrame {

	private static Logger logger = Logger.getLogger(OrganFrame.class.getName());

	private static I18N i18n = I18N.get(OrganFrame.class);

	/**
	 * The suffix used for the frame title.
	 */
	private static final String TITEL_SUFFIX = "jOrgan";

	/**
	 * Tweak Mac Os X appearance.
	 */
	private TweakMac tweakMac;

	/**
	 * The menuBar of this frame.
	 */
	private JMenuBar menuBar = new JMenuBar();

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

	/**
	 * The listener to changes to the configuration.
	 */
	private ConfigurationListener configurationListener = new InternalConfigurationListener();

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

	private ConfigurationAction configurationAction = new ConfigurationAction();

	private AboutAction aboutAction = new AboutAction();

	private JToggleButton constructButton = new JToggleButton();

	/**
	 * Create a new organFrame.
	 */
	public OrganFrame() {

		setJMenuBar(menuBar);

		// not neccessary for XP but for older Windows LookAndFeel
		if (UIManager.getLookAndFeel().getName().indexOf("Windows") != -1) {
			toolBar.setRollover(true);
		}
		toolBar.add(newAction);
		toolBar.add(openAction);
		toolBar.add(saveAction);

		toolBar.addSeparator();

		constructButton.setToolTipText(i18n
				.getString("constructButton.toolTipText"));
		constructButton.setIcon(new ImageIcon(getClass().getResource(
				"img/construct.gif")));
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

		newOrgan();
		
		updateMenu();
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
	 * Update the menu.
	 */
	private void updateMenu() {

		menuBar.removeAll();
		JMenu fileMenu = new JMenu(i18n.getString("fileMenu.text"));
		menuBar.add(fileMenu);
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();
		fileMenu.add(importAction);
		java.util.List recents = jorgan.io.Configuration.instance()
				.getRecentFiles();
		if (recents.size() > 0) {
			fileMenu.addSeparator();
			for (int r = 0; r < recents.size(); r++) {
				fileMenu.add(new RecentAction(r + 1, (File) recents.get(r)));
			}
		}
		if (!tweakMac.isTweaked()) {
			fileMenu.addSeparator();
			fileMenu.add(exitAction);
		}

		JMenu viewMenu = new JMenu(i18n.getString("viewMenu.text"));
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

		menuBar.revalidate();
		menuBar.repaint();
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

			Configuration.instance().setFrameState(getExtendedState());
			Configuration.instance().setFrameBounds(getBounds());
			jorgan.io.Configuration.instance().removeConfigurationListener(
					configurationListener);

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
			JFileChooser chooser = new JFileChooser(jorgan.io.Configuration
					.instance().getRecentDirectory());
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
			DispositionReader reader = new DispositionReader(
					new FileInputStream(file));

			Organ organ = (Organ) reader.read();

			jorgan.io.Configuration.instance().addRecentFile(file);

			setFile(file);

			setOrgan(new OrganSession(organ));

			if (Configuration.instance().getFullScreenOnLoad()) {
				fullScreenAction.goFullScreen();
			}
		} catch (XMLFormatException ex) {
			logger.log(Level.INFO, "opening organ failed", ex);

			showMessage("openOrganInvalid", new String[] { file.getName() });
		} catch (IOException ex) {
			showMessage("openOrganException", new String[] { file.getName() });

			jorgan.io.Configuration.instance().removeRecentFile(file);

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
			new History(file).add();

			DispositionWriter writer = new DispositionWriter(
					new FileOutputStream(file));
			writer.write(session.getOrgan());

			jorgan.io.Configuration.instance().addRecentFile(file);

			setFile(file);

			saveAction.clearChanges();

			showStatus("organSaved", new Object[0]);
		} catch (XMLFormatException ex) {
			logger.log(Level.INFO, "saving organ failed", ex);

			showMessage("saveOrganInvalid", new String[] { file.getName() });

			return false;
		} catch (IOException ex) {
			logger.log(Level.INFO, "saving organ failed", ex);

			showMessage("saveOrganException", new String[] { file.getName() });

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
		JFileChooser chooser = new JFileChooser(jorgan.io.Configuration
				.instance().getRecentDirectory());
		chooser.setFileFilter(new jorgan.gui.file.DispositionFileFilter());
		if (chooser.showSaveDialog(OrganFrame.this) == JFileChooser.APPROVE_OPTION) {
			File file = jorgan.io.DispositionFileFilter.addSuffix(chooser
					.getSelectedFile());
			if (!file.exists()
					|| JOptionPane.showConfirmDialog(OrganFrame.this, i18n
							.getString("saveOrganAsConfirmReplace"), "jOrgan",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
				int option = JOptionPane.showConfirmDialog(this, i18n
						.getString("closeOrganConfirmChanges"), "jOrgan",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (option == JOptionPane.CANCEL_OPTION) {
					return false;
				} else if (option == JOptionPane.NO_OPTION) {
					return true;
				}
			}
			return saveOrgan();
		}

		return true;
	}

	protected void showStatus(String message, Object[] args) {

		message = MessageFormat.format(i18n.getString(message), args);

		statusBar.setStatus(message);
	}

	/**
	 * Show a message.
	 * 
	 * @param key
	 *            identifier of message
	 * @param args
	 *            arguments of message
	 */
	protected void showMessage(String key, Object[] args) {

		String message = MessageFormat.format(i18n.getString(key), args);

		JOptionPane.showMessageDialog(this, message, i18n
				.getString("message.title"), JOptionPane.ERROR_MESSAGE);
	}

	public void setVisible(boolean visible) {
		if (visible) {
			// realize first so changing state has effect
			pack();
			jorgan.io.Configuration.instance().addConfigurationListener(
					configurationListener);
			Rectangle rect = Configuration.instance().getFrameBounds();
			if (rect != null) {
				setBounds(rect);
			}
			setExtendedState(Configuration.instance().getFrameState());			
		}
		super.setVisible(visible);
	}

	/**
	 * The action that initiates a new organ.
	 */
	private class NewAction extends AbstractAction {
		private NewAction() {
			putValue(Action.NAME, i18n.getString("newAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("newAction.shortDescription"));
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
					"img/new.gif")));
		}

		public void actionPerformed(ActionEvent ev) {
			newOrgan();
		}
	}

	/**
	 * The action that opens the recent organ.
	 */
	private class RecentAction extends AbstractAction {
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

	private class DebugAction extends AbstractAction {

		private DebugPanel debugPanel = new DebugPanel();

		private DebugAction() {
			putValue(Action.NAME, i18n.getString("debugAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("debugAction.shortDescription"));
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
					"img/debug.gif")));
		}

		public void actionPerformed(ActionEvent ev) {
			debugPanel.showInDialog(OrganFrame.this);
		}
	}

	/**
	 * The action that opens an organ.
	 */
	private class OpenAction extends AbstractAction {
		private OpenAction() {
			putValue(Action.NAME, i18n.getString("openAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("openAction.shortDescription"));
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
					"img/open.gif")));
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
	private class SaveAction extends AbstractAction implements OrganListener {

		private boolean dispositionChanges = false;

		private boolean registrationChanges = false;

		private SaveAction() {
			putValue(Action.NAME, i18n.getString("saveAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("saveAction.shortDescription"));
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
					"img/save.gif")));
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
			return dispositionChanges
					|| registrationChanges
					&& jorgan.io.Configuration.instance()
							.getRegistrationChanges() != jorgan.io.Configuration.REGISTRATION_CHANGES_IGNORE;
		}

		/**
		 * Are changes to be confirmed.
		 * 
		 * @return <code>true</code> if changes are to be confirmed
		 */
		public boolean confirmChanges() {
			return dispositionChanges
					|| jorgan.io.Configuration.instance()
							.getRegistrationChanges() == jorgan.io.Configuration.REGISTRATION_CHANGES_CONFIRM;
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
	private class SaveAsAction extends AbstractAction {
		private SaveAsAction() {
			putValue(Action.NAME, i18n.getString("saveAsAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("saveAsAction.shortDescription"));
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
					"img/saveAs.gif")));
		}

		public void actionPerformed(ActionEvent ev) {
			saveOrganAs();
		}
	}

	/**
	 * The action that shows information about jOrgan.
	 */
	private class AboutAction extends AbstractAction {
		private AboutAction() {
			putValue(Action.NAME, i18n.getString("aboutAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("aboutAction.shortDescription"));
		}

		public void actionPerformed(ActionEvent ev) {
			AboutPanel.showInDialog(OrganFrame.this);
		}
	}

	/**
	 * The action that starts an import.
	 */
	private class ImportAction extends AbstractAction {
		private ImportAction() {
			putValue(Action.NAME, i18n.getString("importAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("importAction.shortDescription"));
		}

		public void actionPerformed(ActionEvent ev) {
			ImportWizard.showInDialog(OrganFrame.this, session.getOrgan());
		}
	}

	/**
	 * The action that shows the configuration.
	 */
	private class ConfigurationAction extends AbstractAction {
		private ConfigurationAction() {
			putValue(Action.NAME, i18n.getString("configurationAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("configurationAction.shortDescription"));
		}

		public void actionPerformed(ActionEvent ev) {
			ConfigurationDialog dialog = ConfigurationDialog.create(
					OrganFrame.this, jorgan.Configuration.instance(), false);
			dialog.start(600, 600);
			dialog.dispose();
		}
	}

	/**
	 * The action that initiates full screen.
	 */
	private class FullScreenAction extends AbstractAction implements
			ComponentListener {

		private Map dialogs = new HashMap();

		private FullScreenAction() {
			putValue(Action.NAME, i18n.getString("fullScreenAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("fullScreenAction.shortDescription"));

			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), this);
			getRootPane().getActionMap().put(this, this);
		}

		public void actionPerformed(ActionEvent ev) {
			if (dialogs.isEmpty()) {
				goFullScreen();

				if (dialogs.isEmpty()) {
					showMessage("noFullScreen", new Object[0]);
				}
			}
		}

		private void goFullScreen() {
			List consoles = session.getOrgan().getCandidates(Console.class);
			for (int c = 0; c < consoles.size(); c++) {
				Console console = (Console) consoles.get(c);
				String screen = console.getScreen();
				if (screen == null) {
					continue;
				}

				ConsoleDialog dialog = (ConsoleDialog) dialogs.get(screen);
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
			Iterator iterator = dialogs.values().iterator();
			while (iterator.hasNext()) {
				ConsoleDialog dialog = (ConsoleDialog) iterator.next();
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
	private class ExitAction extends AbstractAction {
		private ExitAction() {
			putValue(Action.NAME, i18n.getString("exitAction.name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("exitAction.shortDescription"));
		}

		public void actionPerformed(ActionEvent ev) {
			close();
		}
	}

	/**
	 * Listener to configuration changes.
	 */
	private class InternalConfigurationListener implements
			ConfigurationListener {

		public void configurationChanged(ConfigurationEvent ev) {
			updateMenu();
		}

		public void configurationBackup(ConfigurationEvent event) {
		}
	}
}