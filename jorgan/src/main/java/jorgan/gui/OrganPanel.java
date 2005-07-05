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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import jorgan.config.ConfigurationEvent;
import jorgan.config.ConfigurationListener;
import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.play.PlayerProblem;
import jorgan.play.event.PlayEvent;
import jorgan.play.event.PlayListener;
import jorgan.gui.construct.*;
import jorgan.gui.construct.ElementsPanel;
import jorgan.gui.construct.ReferencesPanel;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.gui.midi.KeyboardPane;
import jorgan.gui.midi.MidiLog;
import spin.Spin;
import swingx.docking.DefaultDockable;
import swingx.docking.Dock;
import swingx.docking.Dockable;
import swingx.docking.DockingPane;
import swingx.docking.border.Eclipse3Border;
import swingx.docking.persistence.XMLPersister;

/**
 * Panel for display and editing of an organ.
 */
public class OrganPanel extends JPanel {
    
  private static Logger logger = Logger.getLogger(OrganPanel.class.getName());
	
  private static final String KEY_CONSOLES     = "consoles";
  private static final String KEY_PROBLEMS     = "problems";
  private static final String KEY_KEYBOARD     = "keyboard";
  private static final String KEY_MIDI_LOG     = "midiLog";
  private static final String KEY_ELEMENTS     = "elements";
  private static final String KEY_REFERENCES   = "references";
  private static final String KEY_PROPERTIES   = "properties";
  private static final String KEY_INSTRUCTIONS = "instructions";
  private static final String KEY_SKINS        = "skins";

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  /**
   * The organ.
   */
  private OrganSession session;
  
  private ConstructAction constructAction = new ConstructAction();

  private JToggleButton constructButton = new JToggleButton(constructAction);
  
  private boolean constructing = false;
  
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
  
  /**
   * The listener to configuration changes.
   */
  private InternalConfigurationListener configurationListener = new InternalConfigurationListener();

  /*
   * The outer dockingPane that holds all views.
   */
  private DockingPane outer = new BordererDockingPane();

  /*
   * The innter dockingPane that holds all consoles.
   */
  private DockingPane inner = new BordererDockingPane();
  
  private ElementPropertiesPanel propertiesPanel   = new ElementPropertiesPanel();
  private ElementsPanel          elementsPanel     = new ElementsPanel();
  private ReferencesPanel        referencesPanel   = new ReferencesPanel();
  private ProblemsPanel          problemsPanel     = new ProblemsPanel();
  private InstructionsPanel      instructionsPanel = new InstructionsPanel();
  private KeyboardPane           keyboardPane      = new KeyboardPane();
  private MidiLog                midiLog           = new MidiLog();
  private PlayMonitor            playMonitor       = new PlayMonitor();

  private ActionDockable problemsDockable     = new ActionDockable(KEY_PROBLEMS    , problemsPanel);
  private ActionDockable keyboardDockable     = new ActionDockable(KEY_KEYBOARD    , keyboardPane);
  private ActionDockable midiLogDockable      = new ActionDockable(KEY_MIDI_LOG    , midiLog);
  private ActionDockable elementsDockable     = new ActionDockable(KEY_ELEMENTS    , elementsPanel); 
  private ActionDockable referencesDockable   = new ActionDockable(KEY_REFERENCES  , referencesPanel); 
  private ActionDockable propertiesDockable   = new ActionDockable(KEY_PROPERTIES  , propertiesPanel);
  private ActionDockable instructionsDockable = new ActionDockable(KEY_INSTRUCTIONS, instructionsPanel);
  private ActionDockable skinsDockable        = new ActionDockable(KEY_SKINS       , null);

  private Map consoleDockables = new HashMap(); 

  private BackAction    backAction     = new BackAction();
  private ForwardAction forwardAction  = new ForwardAction();

  /**
   * Create a new organPanel.
   */
  public OrganPanel() {
    setLayout(new BorderLayout());

    outer.setBorder(new EmptyBorder(2, 2, 2, 2));
    add(outer, BorderLayout.CENTER);   

    setOrgan(new OrganSession());
    
    elementsDockable.setEnabled(false); 
    referencesDockable.setEnabled(false); 
    propertiesDockable.setEnabled(false);
    instructionsDockable.setEnabled(false);       
    skinsDockable.setEnabled(false);       
    
    loadDocking();
  }

  public void addNotify() {
    super.addNotify();

    Configuration configuration = Configuration.instance();
    configuration.addConfigurationListener(configurationListener);
  }
  
  
  public void removeNotify() {
    Configuration.instance().removeConfigurationListener(configurationListener);
    
    saveDocking();

    super.removeNotify();
  }

  public List getToolBarWidgets() {
    List widgets = new ArrayList();
    
    widgets.add(backAction);
    widgets.add(forwardAction);
    widgets.add(constructButton);
    
    return widgets;
  }

  public List getStatusBarWidgets() {
    List widgets = new ArrayList();
    
    widgets.add(playMonitor);
    
    return widgets;
  }

  public List getMenuWidgets() {
    List actions = new ArrayList();
    actions.add(problemsDockable);
    actions.add(keyboardDockable);
    actions.add(midiLogDockable);
    actions.add(elementsDockable);
    actions.add(propertiesDockable);
    actions.add(referencesDockable);
    actions.add(instructionsDockable);
    actions.add(skinsDockable);

    return actions;
  }

  /**
   * Set the organ to be displayed.
   *
   * @param organ   the organ to be displayed
   */
  public void setOrgan(OrganSession session) {
    if (this.session != null) {
      this.session.getOrgan().removeOrganListener((OrganListener)Spin.over(organListener));
      this.session.getPlay().removePlayerListener((PlayListener)Spin.over(playerListener));
      this.session.getSelectionModel().removeSelectionListener(selectionListener);
      
      if (this.session.getPlay().isOpen()) {
        this.session.getPlay().close();
      }

      propertiesPanel.setOrgan(null);
      referencesPanel.setOrgan(null);
      elementsPanel.setOrgan(null);
      problemsPanel.setOrgan(null);
      instructionsPanel.setOrgan(null);
      
      for (int e = 0; e < this.session.getOrgan().getElementCount(); e++) {
        Element element = this.session.getOrgan().getElement(e);
        if (element instanceof Console) {
          removeConsoleDockable((Console)element);
        }
      }
    }

    this.session = session;

    if (this.session != null) {
      this.session.getOrgan().addOrganListener((OrganListener)Spin.over(organListener));
      this.session.getPlay().addPlayerListener((PlayListener)Spin.over(playerListener));
      this.session.getSelectionModel().addSelectionListener(selectionListener);
        
      if (!constructing) {
        this.session.getPlay().open();
      }
      
      propertiesPanel.setOrgan(this.session);
      referencesPanel.setOrgan(this.session);
      elementsPanel.setOrgan(this.session);
      problemsPanel.setOrgan(this.session);
      instructionsPanel.setOrgan(this.session);

      for (int e = 0; e < this.session.getOrgan().getElementCount(); e++) {
        Element element = this.session.getOrgan().getElement(e);
        if (element instanceof Console) {
          addConsoleDockable((Console)element);
        }
      }
    }
    
    updateHistory();
  }

  protected void updateHistory() {
    if (session == null || !isConstructing()) {
      backAction.setEnabled(false);
      forwardAction.setEnabled(false);
    } else {
      backAction.setEnabled(session.getSelectionModel().hasPrevious());
      forwardAction.setEnabled(session.getSelectionModel().hasNext());
    }
  }
  
  protected void addConsoleDockable(Console console) {

    ConsolePanel consolePanel = new ConsolePanel();
    consolePanel.setOrgan(session);
    consolePanel.setConsole(console);
    consolePanel.setConstructing(constructing);
    
    JScrollPane scrollPane = new JScrollPane(consolePanel);
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    
    Dockable dockable = new DefaultDockable(scrollPane, ElementUtils.getElementName(console));
            
    inner.putDockable(console, dockable);
            
    consoleDockables.put(console, dockable);
  }
  
  protected void updateConsoleDockable(Console console) {
    DefaultDockable dockable = (DefaultDockable)consoleDockables.get(console);

    dockable.setName(ElementUtils.getElementName(console));
    
    inner.putDockable(console, dockable);
  }
  
  protected void removeConsoleDockable(Console console) {
    Dockable dockable = (Dockable)consoleDockables.get(console);
    
    JScrollPane scrollPane = (JScrollPane)dockable.getComponent();
    ConsolePanel consolePanel = (ConsolePanel)scrollPane.getViewport().getView();
    consolePanel.setConsole(null);
    consolePanel.setOrgan(null);
    
    inner.removeDockable(console);
  }

  /**
   * Get the organ.
   *
   * @return  the organ
   */
  public OrganSession getOrgan() {
    return session;
  }

  /**
   * Is this organ panel currently constructing.
   *
   * @return  <code>true</code> if currently constructing
   */
  public boolean isConstructing() {
    return constructing;
  }

  /**
   * Construct.
   *
   * @param construct
   */
  protected void setConstructing(boolean constructing) {

    if (this.constructing != constructing) {
        saveDocking();

        this.constructing = constructing;
        
        loadDocking();

        constructButton.setSelected(constructing);
        
        elementsDockable.setEnabled(constructing); 
        referencesDockable.setEnabled(constructing); 
        propertiesDockable.setEnabled(constructing);
        instructionsDockable.setEnabled(constructing);       
        
        if (constructing) {
          if (session.getPlay().isOpen()) {
            session.getPlay().close();
          }
        } else {
          session.getSelectionModel().setSelectedElement(null);
          
          if (!session.getPlay().isOpen()) {
            session.getPlay().open();
          }
        }

        Iterator iterator = consoleDockables.values().iterator();
        while (iterator.hasNext()) {
          Dockable consoleDockable = (Dockable)iterator.next();
          
          JScrollPane scrollPane = (JScrollPane)consoleDockable.getComponent();
          ConsolePanel consolePanel = (ConsolePanel)scrollPane.getViewport().getView();
          consolePanel.setConstructing(constructing);        
        }
        
        updateHistory();
    }
  }

  public void loadDocking() {
    try {
      String docking;
      if (constructing) {
        docking = Configuration.instance().getConstructDocking();
      } else {
        docking = Configuration.instance().getPlayDocking();
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
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(dockingXml));
        OrganPanelPersister persister = new OrganPanelPersister(reader); 
        persister.load();
      } catch (Exception error) {
        throw new Error("unable to load standard docking");
      }
    }      
  }

  public void saveDocking() {
    try {
      Writer writer = new StringWriter();
      OrganPanelPersister persister = new OrganPanelPersister(writer);
      persister.save();
      String docking = writer.toString();
      if (constructing) {
        Configuration.instance().setConstructDocking(docking);
      } else {
        Configuration.instance().setPlayDocking(docking);
      }
    } catch (Exception ex) {
      logger.log(Level.FINE, "unable to save docking", ex);
    }
  }
  
  private class InternalConfigurationListener implements ConfigurationListener {

    public void configurationChanged(ConfigurationEvent ev) { }
    
    public void configurationBackup(ConfigurationEvent event) {
      saveDocking();
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

    public void playerAdded(PlayEvent ev) { }
  
    public void playerRemoved(PlayEvent ev) { }

    public void problemAdded(PlayEvent ev) {
      if (PlayerProblem.ERROR.equals(ev.getProblem().getLevel())) {
        outer.putDockable(KEY_PROBLEMS, problemsDockable);
      }
    }
    
    public void problemRemoved(PlayEvent ev) { }
  }
  
  /**
   * The listener to organ events.
   */
  private class InternalOrganListener extends OrganAdapter {

    public void elementChanged(OrganEvent event) {
      jorgan.disposition.Element element = event.getElement();

      if (element instanceof Console) {
        updateConsoleDockable((Console)element);
      }
    }
    
    public void elementAdded(OrganEvent event) {

      jorgan.disposition.Element element = event.getElement();

      if (element instanceof Console) {
        addConsoleDockable((Console)element);      
      }
    }

    public void elementRemoved(OrganEvent event) {

      jorgan.disposition.Element element = event.getElement();

      if (element instanceof Console) {
        removeConsoleDockable((Console)element);      
      }
    }
  }

  /**
   * The listener to selection events.
   */
  private class InternalSelectionListener implements ElementSelectionListener {
    public void selectionChanged(ElementSelectionEvent ev) {
      if (session.getSelectionModel().isElementSelected()) {
        setConstructing(true);
      }
      
      if (session.getSelectionModel().getSelectionCount() == 1) {
        Element element = session.getSelectionModel().getSelectedElement();
        if (element instanceof Console) {
          Console console = (Console)element;

          Dockable dockable = (Dockable)consoleDockables.get(console);
          if (dockable == null) {
            addConsoleDockable(console);
            
            dockable = (Dockable)consoleDockables.get(console);
          }
          inner.putDockable(console, dockable);
        }
      }

      updateHistory();
    }
  }  
  
  private class ActionDockable extends AbstractAction implements Dockable {

    private String     key;
    private JComponent component;
    private String     title;
    private Icon       icon;

    public ActionDockable(String key, JComponent component) {
      this.key       = key;
      this.component = component;
      this.title     = resources.getString("dock." + key);
      this.icon      = new ImageIcon(getClass().getResource("img/" + key + ".gif"));

      putValue(Action.NAME      , title);
      putValue(Action.SMALL_ICON, icon);      
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
    
    public String getDescription() {
      return null;
    }
    
    public Icon getIcon() {
      return icon;
    }
    
    public List getMenuItems() {
      return null;
    }
    
    public String getName() {
      return title;
    }
  }
    
  /**
   * The action that steps back to the previous element.
   */
  private class BackAction extends AbstractAction {
    public BackAction() {
      putValue(Action.NAME             , resources.getString("action.back.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("action.back.description"));
      putValue(Action.SMALL_ICON       , new ImageIcon(getClass().getResource("img/back.gif")));

      setEnabled(false);
    }

    public void actionPerformed(ActionEvent ev) {
      session.getSelectionModel().previous();
    }
  }

  /**
   * The action that steps forward to the next element.
   */
  private class ForwardAction extends AbstractAction {
    public ForwardAction() {
      putValue(Action.NAME             , resources.getString("action.forward.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("action.forward.description"));
      putValue(Action.SMALL_ICON       , new ImageIcon(getClass().getResource("img/forward.gif")));

      setEnabled(false);
    }

    public void actionPerformed(ActionEvent ev) {
      session.getSelectionModel().next();
    }
  }
  
  /**
   * The action that toggles construction.
   */
  private class ConstructAction extends AbstractAction {

    public ConstructAction() {
      putValue(Action.NAME             , resources.getString("action.construct.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("action.construct.description"));
      putValue(Action.SMALL_ICON       , new ImageIcon(getClass().getResource("img/construct.gif")));
    }

    public void actionPerformed(ActionEvent ev) {
      setConstructing(constructButton.isSelected());
    }
  }
  
  private class OrganPanelPersister extends XMLPersister {
    public OrganPanelPersister(Reader reader) {
      super(outer, reader);
    }
    public OrganPanelPersister(Writer writer) {
      super(outer, writer);
    }
    
    protected JComponent resolveComponent(Object key) {
      if (KEY_CONSOLES.equals(key)) {
        return inner;
      } else {
        return null;          
      }
    }
    
    protected Dockable resolveDockable(Object key) {
      if (KEY_KEYBOARD.equals(key)) {
        return keyboardDockable;
      } else if (KEY_PROBLEMS.equals(key)) {
        return problemsDockable;
      } else if (KEY_MIDI_LOG.equals(key)) {
        return midiLogDockable;
      } else if (constructing) {
        if (KEY_ELEMENTS.equals(key)) {
          return elementsDockable;
        } else if (KEY_REFERENCES.equals(key)) {
          return referencesDockable;
        } else if (KEY_PROPERTIES.equals(key)) {
          return propertiesDockable;
        } else if (KEY_INSTRUCTIONS.equals(key)) {
          return instructionsDockable;
        }
      }
      return null;
    }
  }
  
  private class BordererDockingPane extends DockingPane {
    protected Dock createDock() {
        Dock dock = super.createDock();
        dock.setBorder(new Eclipse3Border());
        return dock;
    }
  }
}