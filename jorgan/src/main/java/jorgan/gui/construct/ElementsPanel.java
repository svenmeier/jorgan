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
package jorgan.gui.construct;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;
import swingx.list.AbstractDnDListModel;
import swingx.list.DnDList;

import jorgan.disposition.*;
import jorgan.disposition.event.*;
import jorgan.gui.ElementSelectionModel;
import jorgan.gui.event.*;
import jorgan.play.*;
import jorgan.play.event.*;

/**
 * Panel shows all elements.
 */
public class ElementsPanel extends JPanel {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private static final Icon alphabetIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/alphabet.gif"));

  /**
   * Icon used for indication an element.
   */
  private static final Icon elementIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/element.gif"));

  /**
   * Icon used for indication of a warning.
   */
  private static final Icon warningIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/elementWarning.gif"));

  /**
   * Icon used for indication of an error.
   */
  private static final Icon errorIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/elementError.gif"));

  /**
   * The edited organ.
   */
  private Organ organ;

  /**
   * The handler of selection changes.
   */
  private SelectionHandler selectionHandler = new SelectionHandler();

  /**
   * The model for selection.
   */
  private ElementSelectionModel selectionModel;
  
  private AddAction addAction = new AddAction();
  private RemoveAction removeAction = new RemoveAction();

  private OrganPlay play;
  
  private DnDList list = new DnDList();
  
  private JToolBar toolBar = new JToolBar();
  
  private JToggleButton alphabetButton = new JToggleButton(alphabetIcon);

  private ElementsModel elementsModel = new ElementsModel();
  
  private List elements = new ArrayList();
  
  /**
   * Create a tree panel.
   */
  public ElementsPanel() {
    
    setLayout(new BorderLayout());
    
    toolBar.setRollover(true);
    toolBar.setFloatable(false);

    toolBar.add(addAction);
    
    toolBar.add(removeAction);
    
    toolBar.addSeparator();
    
    alphabetButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        setOrgan(organ);
      }
    });
    toolBar.add(alphabetButton);
    
    add(toolBar, BorderLayout.NORTH);

    list.setModel(elementsModel);
    list.setCellRenderer(new ElementListCellRenderer());
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    list.addListSelectionListener(selectionHandler);
    
    JScrollPane scrollPane = new JScrollPane(list);
    scrollPane.setBorder(new EmptyBorder(0,0,0,0));
    add(scrollPane, BorderLayout.CENTER);

    setSelectionModel(new ElementSelectionModel());
  }
  
  public void setSelectionModel(ElementSelectionModel selectionModel) {
    if (selectionModel == null) {
      throw new IllegalArgumentException("selectionModel must not be null");
    }

    // only null if called from constructor
    if (this.selectionModel != null) {
      this.selectionModel.removeSelectionListener(selectionHandler);
    }

    this.selectionModel = selectionModel;

    selectionModel.addSelectionListener(selectionHandler);
    
    removeAction.update();
  }
  
  public Organ getOrgan() {
    return organ;
  }
  
  /**
   * Set the organ to be edited.
   *
   * @param organ organ to be edited
   */
  public void setOrgan(Organ organ) {
    
    if (this.organ != null) {
      this.organ.removeOrganListener((OrganListener)Spin.over(elementsModel));

      int removed = elements.size();
      
      elements = new ArrayList();
      
      elementsModel.fireRemoved(removed);
    }

    this.organ = organ;

    if (organ != null) {
      organ.addOrganListener((OrganListener)Spin.over(elementsModel));
  
      elements = organ.getElements();
      Collections.sort(elements, new ElementComparator(alphabetButton.isSelected()));

      elementsModel.fireAdded(elements.size());
    }
  }
  
  public void setPlay(OrganPlay play) {
    if (this.play != null) {
      this.play.removePlayerListener(elementsModel);
    }
    
    this.play = play;
    
    if (this.play != null) {
      this.play.addPlayerListener(elementsModel);
    }
  }
  
  /**
   * The handler of selections.
   */
  private class SelectionHandler implements ElementSelectionListener, ListSelectionListener {

    public void selectionChanged(ElementSelectionEvent ev) {
      list.removeListSelectionListener(this);
         
      list.clearSelection();
      
      java.util.List selectedElements = selectionModel.getSelectedElements();    
      for (int e = 0; e < selectedElements.size(); e++) {
        Element element = (Element)selectedElements.get(e);
    
        int index = elements.indexOf(element);
        if (index != -1) {
            list.addSelectionInterval(index, index);
        
            if (e == 0) {
              list.scrollRectToVisible(list.getCellBounds(index, index));
            }
        }
      }

      list.addListSelectionListener(this);
      
      removeAction.update();
    }
        
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        selectionModel.removeSelectionListener(this);

        Object[] values = list.getSelectedValues();
        if (values.length == 1) {
          selectionModel.setSelectedElement((Element)values[0]);
        }else {
          selectionModel.setSelectedElements(Arrays.asList(values));
        }
        
        selectionModel.addSelectionListener(this);
      }

      removeAction.update();
    }
  }

  /**
   * Note that <em>Spin</em> ensures that the organListener methods are called
   * on the EDT, although a change in the organ might be triggered by a change
   * on a MIDI thread.
   */
  private class ElementsModel extends AbstractDnDListModel implements PlayListener, OrganListener {

    public Object getElementAt(int index) {
      return elements.get(index);
    }

    public int indexOf(Object element) {
        return elements.indexOf(element);
    }
    
    public int getSize() {
      if (organ == null) {
        return 0;
      } else {
        return elements.size();
      }
    }

    protected void insertElementAt(Object element, int index) {
    }

    protected void removeElement(Object element) {
    }
    
    public void io(boolean input, boolean output) {
    }

    public void playerAdded(PlayEvent ev) { }
  
    public void playerRemoved(PlayEvent ev) { }

    public void problemAdded(PlayEvent ev) {
      updateProblem(ev);
    }
    
    public void problemRemoved(PlayEvent ev) {
      updateProblem(ev);
    }

    private void updateProblem(PlayEvent ev) {
      
      Element element = ev.getElement();
      int index = elements.indexOf(element);
      
      fireContentsChanged(this, index, index);
    }

    public void elementChanged(final OrganEvent event) {
      Element element = event.getElement();
      int index = elements.indexOf(element);
      
      fireContentsChanged(this, index, index);
    }

    public void elementAdded(OrganEvent event) {
      elements.add(event.getElement());
      
      int index = elements.size() - 1;
      fireIntervalAdded(this, index, index);
      
      Collections.sort(elements, new ElementComparator(alphabetButton.isSelected()));
      fireContentsChanged(this, 0, index);
      
      selectionHandler.selectionChanged(null);
    }

    public void elementRemoved(OrganEvent event) {
      int index = elements.indexOf(event.getElement());
      
      elements.remove(event.getElement());
      
      fireIntervalRemoved(this, index, index);
    }
    
    public void referenceAdded(OrganEvent event) {
    }

    public void referenceChanged(OrganEvent event) {
    }
    
    public void referenceRemoved(OrganEvent event) {
    }
     
    public void fireRemoved(int count) {
      
      if (count > 0) {
        fireIntervalRemoved(this, 0, count - 1);
      }   
    }
    
    public void fireAdded(int count) {
      if (count > 0) {
        fireIntervalAdded(this, 0, count - 1);   
      }   
    }
  }
  
  public class ElementListCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

      Element element = (Element)value;

      String name = ElementUtils.getElementAndTypeName(element, alphabetButton.isSelected());

      super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
      
      setIcon(elementIcon);
      if (play != null) {
        if (play.hasErrors(element)) {
          setIcon(errorIcon);
        } else if (play.hasWarnings(element)) {
          setIcon(warningIcon);
        }
      }
      
      return this;
    }
  }
    
  private class AddAction extends AbstractAction  {

    public AddAction() {
      putValue(Action.NAME             , resources.getString("construct.action.element.add.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("construct.action.element.add.description"));
      putValue(Action.SMALL_ICON       , new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/add.gif")));
   }

    public void actionPerformed(ActionEvent ev) {
        if (organ != null) {
          Element prototype = null;
          if (selectionModel.getSelectionCount() == 1) {
            prototype = selectionModel.getSelectedElement();
          }
          CreateElementWizard.showInDialog((JFrame)SwingUtilities.getWindowAncestor(ElementsPanel.this), organ, prototype);
        }
    }
  }
  
  private class RemoveAction extends AbstractAction  {

    public RemoveAction() {
      putValue(Action.NAME             , resources.getString("construct.action.element.remove.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("construct.action.element.remove.description"));
      putValue(Action.SMALL_ICON, new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/remove.gif")));
   }

    public void actionPerformed(ActionEvent ev) {
      List selectedElements = selectionModel.getSelectedElements();
        
      for (int e = selectedElements.size() - 1; e >= 0; e--) {
        organ.removeElement((Element)selectedElements.get(e));
      }
    }
    
    public void update() {
      setEnabled(selectionModel.isElementSelected());
    }
  }
}