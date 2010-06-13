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
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import jorgan.Info;
import jorgan.gui.action.spi.ActionRegistry;
import jorgan.gui.file.DispositionFileFilter;
import jorgan.gui.preferences.PreferencesDialog;
import jorgan.gui.undo.UndoManager;
import jorgan.io.DispositionStream;
import jorgan.io.disposition.ExtensionException;
import jorgan.io.disposition.FormatException;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import jorgan.swing.DebugPanel;
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

	private static Logger logger = Logger.getLogger(OrganFrame.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			OrganFrame.class);

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

	private OpenAction openAction = new OpenAction();

	private SaveAction saveAction = new SaveAction();

	private CloseAction closeAction = new CloseAction();

	private ExitAction exitAction = new ExitAction();

	private PreferencesAction configurationAction = new PreferencesAction();

	private WebsiteAction websiteAction = new WebsiteAction();

	private AboutAction aboutAction = new AboutAction();

	private JToggleButton constructButton = new JToggleButton();

	private EventHandler handler = new EventHandler();

	private Changes changes = Changes.CONFIRM;

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
				exit();
			}
		});

		if (MacAdapter.getInstance().isInstalled()) {
			MacAdapter.getInstance().setQuitListener(exitAction);
			MacAdapter.getInstance()
					.setPreferencesListener(configurationAction);
			MacAdapter.getInstance().setAboutListener(aboutAction);
		}

		config.get("construct").read(constructButton);
		constructButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (session != null) {
					session.setConstructing(constructButton.isSelected());
				}
			};
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

		if (session != null && !this.session.isSealed()) {
			toolBar.addSeparator();
			toolBar.add(constructButton);

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

		JMenu fileMenu = new JMenu();
		config.get("fileMenu").read(fileMenu);
		menuBar.add(fileMenu);
		fileMenu.add(openAction);

		JMenu recentsMenu = new JMenu();
		config.get("recentsMenu").read(recentsMenu);
		List<File> recents = new DispositionStream().getRecentFiles();
		for (int r = 0; r < recents.size(); r++) {
			recentsMenu.add(new RecentAction(r + 1, recents.get(r)));
		}
		fileMenu.add(recentsMenu);

		fileMenu.addSeparator();
		fileMenu.add(saveAction);
		fileMenu.add(closeAction);

		if (session != null) {
			List<Action> actions = ActionRegistry.createMenuActions(session,
					this);
			if (actions.size() > 0) {
				fileMenu.addSeparator();

				for (Action action : actions) {
					fileMenu.add(action);
				}
			}
		}

		if (!MacAdapter.getInstance().isInstalled()) {
			fileMenu.addSeparator();
			fileMenu.add(exitAction);
		}

		JMenu viewMenu = new JMenu();
		config.get("viewMenu").read(viewMenu);
		menuBar.add(viewMenu);
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

		menuBar.revalidate();
		menuBar.repaint();

		String title;
		if (session == null) {
			title = config.get("titleNoSession").read(new MessageBuilder())
					.build(new Info().getVersion());
		} else {
			title = config.get("titleSession").read(new MessageBuilder())
					.build(
							new Info().getVersion(),
							DispositionFileFilter.removeSuffix(session
									.getFile()));
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

		constructButton.setSelected(this.session != null
				&& this.session.isConstructing());

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
		closeOrgan();

		OrganSession session;
		try {
			session = new OrganSession(file);
		} catch (ExtensionException ex) {
			showBoxMessage("openExtensionException", MessageBox.OPTIONS_OK,
					file.getName(), ex.getExtension());
			return;
		} catch (FormatException ex) {
			logger.log(Level.INFO, ex.getClass().getSimpleName(), ex);

			showBoxMessage("openFormatException", MessageBox.OPTIONS_OK, file
					.getName());
			return;
		} catch (IOException ex) {
			showBoxMessage("openIOException", MessageBox.OPTIONS_OK, file
					.getName());
			return;
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

			showBoxMessage("saveIOException", MessageBox.OPTIONS_OK, session
					.getFile().getName());

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
			statusBar.setStatus(config.get(key).read(new MessageBuilder())
					.build(args));
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
				showBoxMessage("openIOException", MessageBox.OPTIONS_OK, file
						.getName());

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
	private class OpenAction extends BaseAction {
		private OpenAction() {
			config.get("open").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			if (!closeOrgan()) {
				return;
			}

			JFileChooser chooser = new JFileChooser(new DispositionStream()
					.getRecentDirectory());
			chooser.setFileFilter(new jorgan.gui.file.DispositionFileFilter());
			if (chooser.showOpenDialog(OrganFrame.this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (!file.exists()) {
					file = DispositionFileFilter.addSuffix(file);
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
			setEnabled(session != null && session.isModified());

			statusBar.setStatus(null);
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
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				Desktop.getDesktop().browse(
						URI.create("http://jorgan.sourceforge.net"));
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
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
	 * The action that exits jOrgan.
	 */
	private class ExitAction extends BaseAction {
		private ExitAction() {
			config.get("exit").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			exit();
		}
	}

	private class EventHandler implements SessionListener {
		public void constructingChanged(boolean constructing) {
			constructButton.setSelected(constructing);
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
				if (option == MessageBox.OPTION_CANCEL) {
					return false;
				} else if (option == MessageBox.OPTION_NO) {
					return true;
				}

				return frame.saveOrgan();
			}
		},
		SAVE {
			public boolean onClose(OrganFrame frame, OrganSession session) {
				return frame.saveOrgan();
			}
		};

		public abstract boolean onClose(OrganFrame frame, OrganSession session);
	}
}