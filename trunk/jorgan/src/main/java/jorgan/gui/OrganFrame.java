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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jorgan.UI;
import jorgan.config.ConfigurationEvent;
import jorgan.config.ConfigurationListener;
import jorgan.disposition.Console;
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.config.ConfigurationDialog;
import jorgan.gui.help.Help;
import jorgan.gui.imports.ImportWizard;
import jorgan.gui.mac.TweakMac;
import jorgan.io.DispositionReader;
import jorgan.io.DispositionWriter;
import jorgan.swing.StatusBar;
import jorgan.xml.XMLFormatException;
import spin.Spin;
import spin.over.SpinOverEvaluator;

/**
 * The jOrgan frame.
 */
public class OrganFrame extends JFrame implements UI {

    private static ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.gui.resources");

    /**
     * The suffix used for the frame title.
     */
    private static final String TITEL_SUFFIX = "jOrgan";

    /**
     * Tweak Mac Os X appearance.
     */
    private TweakMac tweakMac = new TweakMac(this);

    /**
     * The javaHelp used by this UI.
     */
    private Help help = new Help();

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
    private NewAction newAction = new NewAction();

    private OpenAction openAction = new OpenAction();

    private SaveAction saveAction = new SaveAction();

    private SaveAsAction saveAsAction = new SaveAsAction();

    private ImportAction importAction = new ImportAction();

    private ExitAction exitAction = new ExitAction();

    private FullScreenAction fullScreenAction = new FullScreenAction();

    private ConfigurationAction configurationAction = new ConfigurationAction();

    private HelpAction helpAction = new HelpAction();

    private AboutAction aboutAction = new AboutAction();

    /**
     * Create a new organFrame.
     */
    public OrganFrame() {

        setJMenuBar(menuBar);

        help.enableHelpKey(getRootPane(), "index");

        // not neccessary for XP but for older Windows LookAndFeel
        if (UIManager.getLookAndFeel().getName().indexOf("Windows") != -1) {
            toolBar.setRollover(true);
        }
        toolBar.add(newAction);
        toolBar.add(openAction);
        toolBar.add(saveAction);
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
            private boolean released;

            public void windowClosing(WindowEvent ev) {
                stop();
            }

            public void windowActivated(WindowEvent e) {
                if (released) {
                    session.getPlay().open();
                }
            }

            public void windowDeactivated(WindowEvent e) {
                if (jorgan.play.Configuration.instance()
                        .getReleaseDevicesWhenDeactivated()) {
                    if (!released && session.getPlay().isOpen()) {
                        session.getPlay().close();
                        released = true;
                    }
                }
            }
        });

        newOrgan();
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
        JMenu fileMenu = new JMenu(resources.getString("menu.file"));
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

        JMenu viewMenu = new JMenu(resources.getString("menu.view"));
        menuBar.add(viewMenu);
        viewMenu.add(fullScreenAction);
        viewMenu.addSeparator();
        List actions = organPanel.getMenuWidgets();
        for (int a = 0; a < actions.size(); a++) {
            viewMenu.add((Action) actions.get(a));
        }
        if (!tweakMac.isTweaked()) {
            viewMenu.addSeparator();
            viewMenu.add(configurationAction);
        }

        JMenu helpMenu = new JMenu(resources.getString("menu.help"));
        menuBar.add(helpMenu);
        helpMenu.add(helpAction);
        if (!tweakMac.isTweaked()) {
            helpMenu.addSeparator();
            helpMenu.add(aboutAction);
        }

        menuBar.revalidate();
        menuBar.repaint();
    }

    /**
     * Stop.
     */
    public void stop() {
        if (canCloseOrgan()) {
            if (session.getPlay().isOpen()) {
                session.getPlay().close();
            }

            Configuration.instance().setFrameState(getExtendedState());
            Configuration.instance().setFrameBounds(getBounds());
            jorgan.io.Configuration.instance().removeConfigurationListener(
                    configurationListener);

            dispose();

            exitAction.notifyStop();
        }
    }

    /**
     * Set the organ edited by this frame.
     * 
     * @param organ
     *            organ to be edited
     */
    private void setOrgan(OrganSession session) {
        statusBar.setStatus(null);

        if (this.session != null) {
            this.session.getOrgan().removeOrganListener(
                    (OrganListener) Spin.over(saveAction));
        }

        this.session = session;

        if (this.session != null) {
            this.session.getOrgan().addOrganListener(
                    (OrganListener) Spin.over(saveAction));
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

            return;
        } catch (XMLFormatException ex) {
            ex.printStackTrace();

            showMessage("action.open.exception.invalid", new String[] { file
                    .getName() });
        } catch (IOException ex) {
            showMessage("action.open.exception",
                    new String[] { file.getName() });
        }
        jorgan.io.Configuration.instance().removeRecentFile(file);
    }

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
     */
    public boolean saveOrgan(File file) {
        try {
            DispositionWriter writer = new DispositionWriter(
                    new FileOutputStream(file));
            writer.write(session.getOrgan());

            jorgan.io.Configuration.instance().addRecentFile(file);

            setFile(file);

            saveAction.clearChanges();

            showStatus("action.save.confirm", new Object[0]);
        } catch (XMLFormatException ex) {
            ex.printStackTrace();

            showMessage("action.save.exception.invalid", new String[] { file
                    .getName() });

            return false;
        } catch (IOException ex) {
            ex.printStackTrace();

            showMessage("action.save.exception",
                    new String[] { file.getName() });

            return false;
        }
        return true;
    }

    /**
     * Save the current organ.
     */
    public boolean saveOrganAs() {
        JFileChooser chooser = new JFileChooser(jorgan.io.Configuration
                .instance().getRecentDirectory());
        chooser.setFileFilter(new jorgan.gui.file.DispositionFileFilter());
        if (chooser.showSaveDialog(OrganFrame.this) == JFileChooser.APPROVE_OPTION) {
            File file = jorgan.io.DispositionFileFilter.addSuffix(chooser
                    .getSelectedFile());
            if (!file.exists()
                    || JOptionPane.showConfirmDialog(OrganFrame.this, resources
                            .getString("action.saveAs.replace"), "jOrgan",
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
                int option = JOptionPane.showConfirmDialog(this, resources
                        .getString("action.save.edited"), "jOrgan",
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

    public void showConfiguration() {
        ConfigurationDialog dialog = ConfigurationDialog.create(this,
                jorgan.Configuration.instance(), false);
        dialog.start(600, 750);
        dialog.dispose();
    }

    public void showAbout() {
        AboutPanel.showInDialog(this);
    }

    /**
     * Start the user interaction.
     * 
     * @param file
     *            optional file that contains an organ
     */
    public void start(final File file) {

        if (jorgan.gui.Configuration.instance().getShowAboutOnStartup()) {
            AboutPanel.showInWindow();
        }

        // realize first so changing state has effect
        pack();
        jorgan.io.Configuration.instance().addConfigurationListener(
                configurationListener);
        Rectangle rect = Configuration.instance().getFrameBounds();
        if (rect != null) {
            setBounds(rect);
        }
        setExtendedState(Configuration.instance().getFrameState());

        updateMenu();

        setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (file == null) {
                    setFile(null);
                } else {
                    openOrgan(file);
                }
            }
        });

        exitAction.waitForStop();
    }

    protected void showStatus(String message, Object[] args) {

        message = MessageFormat.format(resources.getString(message), args);

        statusBar.setStatus(message);
    }

    /**
     * Show a message.
     * 
     * @param message
     *            identifier of message
     * @param args
     *            arguments of message
     */
    protected void showMessage(String message, Object[] args) {

        message = MessageFormat.format(resources.getString(message), args);

        JOptionPane.showMessageDialog(this, message, resources
                .getString("exception.title"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * The action that initiates a new organ.
     */
    private class NewAction extends AbstractAction {
        public NewAction() {
            putValue(Action.NAME, resources.getString("action.new.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.new.description"));
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
        public RecentAction(int number, File file) {
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

    /**
     * The action that opens an organ.
     */
    private class OpenAction extends AbstractAction {
        public OpenAction() {
            putValue(Action.NAME, resources.getString("action.open.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.open.description"));
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

        public SaveAction() {
            putValue(Action.NAME, resources.getString("action.save.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.save.description"));
            putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
                    "img/save.gif")));
        }

        public void actionPerformed(ActionEvent ev) {
            saveOrgan();
        }

        public boolean hasChanges() {
            return dispositionChanges
                    || registrationChanges
                    && jorgan.io.Configuration.instance()
                            .getRegistrationChanges() != jorgan.io.Configuration.REGISTRATION_CHANGES_IGNORE;
        }

        public boolean confirmChanges() {
            return dispositionChanges
                    || jorgan.io.Configuration.instance()
                            .getRegistrationChanges() == jorgan.io.Configuration.REGISTRATION_CHANGES_CONFIRM;
        }

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
        public SaveAsAction() {
            putValue(Action.NAME, resources.getString("action.saveAs.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.saveAs.description"));
        }

        public void actionPerformed(ActionEvent ev) {
            saveOrganAs();
        }
    }

    /**
     * The action that shows information about jOrgan.
     */
    private class AboutAction extends AbstractAction {
        public AboutAction() {
            putValue(Action.NAME, resources.getString("action.about.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.about.description"));
        }

        public void actionPerformed(ActionEvent ev) {
            showAbout();
        }
    }

    /**
     * The action that shows the help contents.
     */
    private class HelpAction extends AbstractAction {
        public HelpAction() {
            putValue(Action.NAME, resources.getString("action.help.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.help.description"));
            putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
                    "img/help.gif")));
        }

        public void actionPerformed(ActionEvent ev) {
            if (help.isAvailable()) {
                help.setDisplayed(true);
            } else {
                showMessage("action.help.failure", new String[0]);
            }
        }
    }

    /**
     * The action that starts an import.
     */
    private class ImportAction extends AbstractAction {
        public ImportAction() {
            putValue(Action.NAME, resources.getString("action.import.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.import.description"));
        }

        public void actionPerformed(ActionEvent ev) {
            ImportWizard.showInDialog(OrganFrame.this, session.getOrgan());
        }
    }

    /**
     * The action that shows the configuration.
     */
    private class ConfigurationAction extends AbstractAction {
        public ConfigurationAction() {
            putValue(Action.NAME, resources
                    .getString("action.configuration.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.configuration.description"));
        }

        public void actionPerformed(ActionEvent ev) {
            showConfiguration();
        }
    }

    /**
     * The action that initiates full screen.
     */
    private class FullScreenAction extends AbstractAction implements
            ComponentListener {

        private Map dialogs = new HashMap();

        public FullScreenAction() {
            putValue(Action.NAME, resources.getString("action.fullScreen.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.fullScreen.description"));

            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), this);
            getRootPane().getActionMap().put(this, this);
        }

        public void actionPerformed(ActionEvent ev) {
            if (dialogs.isEmpty()) {
                organPanel.setConstructing(false);

                List consoles = session.getOrgan().getCandidates(Console.class);
                for (int c = 0; c < consoles.size(); c++) {
                    Console console = (Console) consoles.get(c);
                    String screen = console.getScreen();
                    if (screen == null) {
                        continue;
                    }
                    if (Console.DEFAULT_SCREEN.equals(screen)) {
                        screen = ConsoleDialog.getDefaultSceen();
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

                if (dialogs.isEmpty()) {
                    showMessage("action.fullScreen.failure", new Object[0]);
                }
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
        public ExitAction() {
            putValue(Action.NAME, resources.getString("action.exit.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("action.exit.description"));
        }

        public void actionPerformed(ActionEvent ev) {
            stop();
        }

        /**
         * Called on the main thread to wait for stop on event dispatch thread.
         */
        public synchronized void waitForStop() {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new Error("unexpected interruption", ex);
            }
        }

        /**
         * Called on event dispatch thread to notify wait on main thread.
         */
        public synchronized void notifyStop() {
            notify();
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

    /***************************************************************************
     * Initialization of visual appearance.
     */
    static {
        if (Configuration.instance().getUseSystemLookAndFeel()) {
            try {
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // keep default look and feel
            }
        }

        Toolkit.getDefaultToolkit().setDynamicLayout(true);

        // IMPORTANT:
        // Never wait for the result of a spin-over or we'll
        // run into deadlocks!! (player lock <-> Swing EDT)
        SpinOverEvaluator.setDefaultWait(false);
    }
}