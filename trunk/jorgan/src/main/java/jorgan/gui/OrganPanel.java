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
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.play.OrganPlay;
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
import jorgan.gui.midi.MidiMonitor;
import spin.Spin;
import swingx.docking.DefaultDockable;
import swingx.docking.Dockable;
import swingx.docking.DockingPane;
import swingx.docking.border.Eclipse3Border;
import swingx.docking.persistence.XMLPersister;

/**
 * Panel for display and editing of an organ.
 */
public class OrganPanel extends JPanel {
    
  private static Logger logger = Logger.getLogger(OrganPanel.class.getName());
	
  private static final String KEY_CONSOLES = "CONSOLES";
  private static final String KEY_PROBLEMS = "PROBLEMS";
  private static final String KEY_KEYBOARD = "KEYBOARD";
  private static final String KEY_MIDI_LOG = "MIDI_LOG";
  
  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private static final Icon propertiesIcon =
    new ImageIcon(OrganPanel.class.getResource("img/properties.gif"));

  private static final Icon elementsIcon =
    new ImageIcon(OrganPanel.class.getResource("img/elements.gif"));

  private static final Icon referencesIcon =
    new ImageIcon(OrganPanel.class.getResource("img/references.gif"));

  private static final Icon problemsIcon =
    new ImageIcon(OrganPanel.class.getResource("img/problems.gif"));

  private static final Icon keyboardIcon =
    new ImageIcon(OrganPanel.class.getResource("img/keyboard.gif"));

  private static final Icon logIcon =
    new ImageIcon(OrganPanel.class.getResource("img/log.gif"));

  /**
   * The organ.
   */
  private Organ organ;
  
  /**
   * The play of the organ
   */
  private OrganPlay play;
  
  private ConstructAction constructAction = new ConstructAction();

  private JToggleButton constructButton = new JToggleButton(constructAction);
  
  private boolean constructing = false;
  
  /**
   * Is the organ played.
   */
  private boolean playing = true;

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

  /**
   * The model for selection.
   */
  private ElementSelectionModel selectionModel;

  /*
   * The outer dockingPane that holds all views.
   */
  private DockingPane outer = new DockingPane();

  /*
   * The innter dockingPane that holds all consoles.
   */
  private DockingPane inner = new DockingPane();
  
  private ElementPropertiesPanel propertiesPanel = new ElementPropertiesPanel();
  private ElementsPanel          elementsPanel   = new ElementsPanel();
  private ReferencesPanel        referencesPanel = new ReferencesPanel();
  private KeyboardPane           keyboardPane    = new KeyboardPane();
  private ProblemsPanel          problemsPanel   = new ProblemsPanel();
  private MidiLog                midiLog         = new MidiLog();
  private MidiMonitor            midiMonitor     = new MidiMonitor();

  private Dockable elementsDockable   = new DefaultDockable(elementsPanel  , resources.getString("dock.elements"  ), elementsIcon); 
  private Dockable referencesDockable = new DefaultDockable(referencesPanel, resources.getString("dock.references"), referencesIcon); 
  private Dockable propertiesDockable = new DefaultDockable(propertiesPanel, resources.getString("dock.properties"), propertiesIcon);
  private Dockable keyboardDockable   = new DefaultDockable(keyboardPane   , resources.getString("dock.keyboard"  ), keyboardIcon);
  private Dockable problemsDockable   = new DefaultDockable(problemsPanel  , resources.getString("dock.problems"  ), problemsIcon);
  private Dockable midiLogDockable    = new DefaultDockable(midiLog        , resources.getString("dock.log"       ), logIcon);
  private Map      consoleDockables   = new HashMap(); 

  private BackAction     backAction     = new BackAction();
  private ForwardAction  forwardAction  = new ForwardAction();
  private ProblemsAction problemsAction = new ProblemsAction();
  private KeyboardAction keyboardAction = new KeyboardAction();
  private MidiLogAction  midiLogAction  = new MidiLogAction();

  /**
   * Create a new organPanel.
   */
  public OrganPanel() {
    setLayout(new BorderLayout());

    inner.setDockBorder(new Eclipse3Border());

    outer.setBorder(new EmptyBorder(2, 2, 2, 2));
    outer.setDockBorder(new Eclipse3Border());
    add(outer, BorderLayout.CENTER);   

    setSelectionModel(new ElementSelectionModel());
    
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
    
    widgets.add(midiMonitor);
    
    return widgets;
  }

  public List getMenuWidgets() {
    List actions = new ArrayList();
    actions.add(problemsAction);
    actions.add(keyboardAction);
    actions.add(midiLogAction);

    return actions;
  }
  
  public void setSelectionModel(ElementSelectionModel selectionModel) {
    if (selectionModel == null) {
      throw new IllegalArgumentException("selectionModel must not be null");
    }

    // only null if called from constructor
    if (this.selectionModel != null) {
      this.selectionModel.removeSelectionListener(selectionListener);
    }

    this.selectionModel = selectionModel;
    
    selectionModel.addSelectionListener(selectionListener);
    
    propertiesPanel.setSelectionModel(selectionModel);
    elementsPanel.setSelectionModel(selectionModel);
    referencesPanel.setSelectionModel(selectionModel);
    problemsPanel.setSelectionModel(selectionModel);
    Iterator iterator = consoleDockables.values().iterator();
    while (iterator.hasNext()) {
      Dockable consoleDockable = (Dockable)iterator.next();
          
      JScrollPane scrollPane = (JScrollPane)consoleDockable.getComponent();
      ConsolePanel consolePanel = (ConsolePanel)scrollPane.getViewport().getView();
      consolePanel.setSelectionModel(selectionModel);        
    }
  }
  
  /**
   * Set the organ to be displayed.
   *
   * @param organ   the organ to be displayed
   */
  public void setOrgan(Organ organ) {

    if (this.organ != null) {
      play.removePlayerListener((PlayListener)Spin.over(playerListener));
      referencesPanel.setOrgan(null);
      elementsPanel.setOrgan(null);
      elementsPanel.setPlay(null);
      problemsPanel.setPlay(null);
      
      for (int e = 0; e < this.organ.getElementCount(); e++) {
        Element element = this.organ.getElement(e);
        if (element instanceof Console) {
          removeConsoleDockable((Console)element);
        }
      }

      if (play.isOpen()) {
        play.close();
      }
      play.dispose();
      play = null;      
      
      selectionModel.clear();
      
      this.organ.removeOrganListener((OrganListener)Spin.over(organListener));
    }

    this.organ = organ;

    if (this.organ != null) {
      this.organ.addOrganListener((OrganListener)Spin.over(organListener));

      play = new OrganPlay(organ);    
      play.addPlayerListener((PlayListener)Spin.over(playerListener));

      for (int e = 0; e < organ.getElementCount(); e++) {
        Element element = organ.getElement(e);
        if (element instanceof Console) {
          addConsoleDockable((Console)element);
        }
      }

      referencesPanel.setOrgan(organ);
      elementsPanel.setOrgan(organ);
      elementsPanel.setPlay(play);
      problemsPanel.setPlay(play);

      if (playing && !constructing) {
        play.open();
      }
    }
  }

  protected void addConsoleDockable(Console console) {

    ConsolePanel consolePanel = new ConsolePanel();
    consolePanel.setConsole(console);
    consolePanel.setSelectionModel(selectionModel);
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
    consolePanel.setSelectionModel(new ElementSelectionModel());
    
    inner.removeDockable(console);
  }

  /**
   * Get the organ.
   *
   * @return  the organ
   */
  public Organ getOrgan() {
    return organ;
  }

  /**
   * Should the organ be played.
   * 
   * @param playing
   */
  public void setPlaying(boolean playing) {
    if (this.playing != playing) {
      this.playing = playing;

      if (playing) {
        if (!play.isOpen() && !constructing) {
          play.open();
        }
      } else {
        if (play.isOpen()) {
          play.close();
        }
      }
    }
  }
  
  /**
   * Is the organ currently played.
   * 
   * @return  <code>true</code> if organ is played
   */
  public boolean isPlaying() {
    return playing;
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
        this.constructing = constructing;

        constructButton.setSelected(constructing);
        
        if (constructing) {
          if (play.isOpen()) {
            play.close();
          }

          outer.putDockable("ELEMENTS"  , elementsDockable);
          outer.putDockable("REFERENCES", referencesDockable);
          outer.putDockable("PROPERTIES", propertiesDockable);
        } else {
          outer.putDockable("ELEMENTS"  , null);
          outer.putDockable("REFERENCES", null);
          outer.putDockable("PROPERTIES", null);
          
          selectionModel.clear();

          if (playing && !play.isOpen()) {
            play.open();
          }
        }

        Iterator iterator = consoleDockables.values().iterator();
        while (iterator.hasNext()) {
          Dockable consoleDockable = (Dockable)iterator.next();
          
          JScrollPane scrollPane = (JScrollPane)consoleDockable.getComponent();
          ConsolePanel consolePanel = (ConsolePanel)scrollPane.getViewport().getView();
          consolePanel.setConstructing(constructing);        
        }
    }
  }

  public void loadDocking() {
    try {
      Reader reader = new StringReader(Configuration.instance().getDocking());
      OrganPanelPersister persister = new OrganPanelPersister(reader); 
      persister.load();
    } catch (Exception keepStandardDocking) {
      try {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("docking.xml"));
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
      Configuration.instance().setDocking(writer.toString());
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

    public void io(boolean input, boolean output) {
      if (input) {
        midiMonitor.input();
      }
      if (output) {
        midiMonitor.output();
      }
    }

    public void playerAdded(PlayEvent ev) { }
  
    public void playerRemoved(PlayEvent ev) { }

    public void problemAdded(PlayEvent ev) {
      if (PlayerProblem.ERROR.equals(ev.getProblem().getLevel())) {
        problemsAction.show();
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
      
      selectionModel.setSelectedElement(element);
    }

    public void elementRemoved(OrganEvent event) {

      jorgan.disposition.Element element = event.getElement();

      if (element instanceof Console) {
        removeConsoleDockable((Console)element);      
      }
      
      selectionModel.clear(element);
    }
    
    public void referenceAdded(OrganEvent event) {
    }
    
    public void referenceRemoved(OrganEvent event) {
    }
  }

  /**
   * The listener to selection events.
   */
  private class InternalSelectionListener implements ElementSelectionListener {
    public void selectionChanged(ElementSelectionEvent ev) {
      if (selectionModel.isElementSelected()) {
        setConstructing(true);
      }
      
      if (selectionModel.getSelectionCount() == 1) {
        Element element = selectionModel.getSelectedElement();
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
      
      backAction.setEnabled(selectionModel.hasPrevious());
      forwardAction.setEnabled(selectionModel.hasNext());
    }
  }  
  
  /**
   * The action that initiates the problems.
   */
  private class ProblemsAction extends AbstractAction {

    public ProblemsAction() {
      putValue(Action.NAME             , resources.getString("action.problems.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("action.problems.description"));
    }

    public void actionPerformed(ActionEvent ev) {
      show();
    }
    
    public void show() {
      outer.putDockable(KEY_PROBLEMS, problemsDockable);
    }
  }
  
  /**
   * The action that initiates the keyboard.
   */
  private class KeyboardAction extends AbstractAction {

    public KeyboardAction() {
      putValue(Action.NAME             , resources.getString("action.keyboard.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("action.keyboard.description"));    
    }

    public void actionPerformed(ActionEvent ev) {
      show();
    }
    
    public void show() {
      outer.putDockable(KEY_KEYBOARD, keyboardDockable);
    }
  }
  
  /**
   * The action that initiates the midiLog.
   */
  private class MidiLogAction extends AbstractAction {

    public MidiLogAction() {
      putValue(Action.NAME             , resources.getString("action.log.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("action.log.description"));
    }

    public void actionPerformed(ActionEvent ev) {
      show();
    }
    
    public void show() {
      outer.putDockable(KEY_MIDI_LOG, midiLogDockable);
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
      selectionModel.previous();
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
      selectionModel.next();
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
      } else {
        return null;
      }
    }
  }
}