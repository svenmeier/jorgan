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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;
import swingx.docking.DockedPanel;

import jorgan.disposition.*;
import jorgan.disposition.event.*;
import jorgan.gui.OrganSession;
import jorgan.gui.event.*;
import jorgan.swing.list.ListUtils;

/**
 * Panel shows the references of elements.
 */
public class ReferencesPanel extends DockedPanel {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private static final Icon sortNameIcon =
      new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/sortName.gif"));
  
  private static final Icon sortTypeIcon =
      new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/sortType.gif"));
  
  private static final Icon referencesToIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/referencesTo.gif"));

  private static final Icon referencedFromIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/referencedFrom.gif"));

  private static final Icon referenceIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/reference.gif"));

  /**
   * The edited organ.
   */
  private OrganSession session;

  private List elements = new ArrayList();
  
  private List references = null;
  
  /**
   * The listener to selection changes.
   */
  private SelectionHandler selectionHandler = new SelectionHandler();

  private AddAction addAction = new AddAction();
  private RemoveAction removeAction = new RemoveAction();

  private JList list = new JList();

  private JToggleButton referencesToButton   = new JToggleButton(referencesToIcon);
  private JToggleButton referencedFromButton = new JToggleButton(referencedFromIcon);

  private JToggleButton sortNameButton = new JToggleButton(sortNameIcon);
  private JToggleButton sortTypeButton = new JToggleButton(sortTypeIcon);
  
  private ReferencesModel referencesModel = new ReferencesModel();

  /**
   * Create a tree panel.
   */
  public ReferencesPanel() {

    addTool(addAction);
    addTool(removeAction);

    addToolSeparator();
    
    sortNameButton.setSelected(true);
    sortNameButton.setToolTipText(resources.getString("sort.name"));
    sortNameButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (sortNameButton.isSelected()) {
          sortTypeButton.setSelected(false);
        }
        updateReferences();
      }
    });
    addTool(sortNameButton);

    sortTypeButton.setToolTipText(resources.getString("sort.type"));
    sortTypeButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (sortTypeButton.isSelected()) {
          sortNameButton.setSelected(false);
        }
        updateReferences();
      }
    });
    addTool(sortTypeButton);

    addToolSeparator();

    ButtonGroup toFromGroup = new ButtonGroup();
    referencesToButton.getModel().setGroup(toFromGroup);
    referencesToButton.setSelected(true);
    referencesToButton.getModel().addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        updateReferences();
      }
    });
    addTool(referencesToButton);

    referencedFromButton.getModel().setGroup(toFromGroup);
    addTool(referencedFromButton);

    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    list.setModel(referencesModel);
    list.setCellRenderer(new ReferenceListCellRenderer());
    list.addListSelectionListener(selectionHandler);
    ListUtils.addActionListener(list, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Element element = (Element)references.get(list.getSelectedIndex());
            
        session.getSelectionModel().setSelectedElement(element);
      }
    });
        
    setScrollableBody(list, true, false);
  }
  
  /**
   * Set the organ to be edited.
   *
   * @param organ organ to be edited
   */
  public void setOrgan(OrganSession session) {
    if (this.session != null) {
      this.session.getOrgan().removeOrganListener((OrganListener)Spin.over(referencesModel));
      this.session.getSelectionModel().removeSelectionListener(selectionHandler);
    }

    this.session = session;
        
    if (this.session != null) {
      this.session.getOrgan().addOrganListener((OrganListener)Spin.over(referencesModel));
      this.session.getSelectionModel().addSelectionListener(selectionHandler);
    }
    
    elements.clear();
    updateReferences();    
  }

  private void updateReferences() {

    if (references != null) {
      int size = references.size();
      references = null;
      
      referencesModel.fireRemoved(size);
    }

    for (int e = 0; e < elements.size(); e++) {
      Element element = (Element)elements.get(e);

      List next;
      if (getShowReferencesTo()) {
        next = element.referenced(); 
      } else {       
        next = new ArrayList(element.getReferrer()); 
      }
      
      if (e == 0) {
        references = next;
      } else {
        if (sortNameButton.isSelected() || sortTypeButton.isSelected()) {
          if (!references.containsAll(next) || !next.containsAll(references)) {
            references = null;
            break;
          }
        } else {
          if (!references.equals(next)) {
            references = null;
            break;
          }
        }
      }
    }
    
    if (references != null) {
      if (sortNameButton.isSelected()) {
        Collections.sort(references, new ElementComparator(true));
      } else if (sortTypeButton.isSelected()) {
        Collections.sort(references, new ElementComparator(false));
      }
      referencesModel.fireAdded(references.size());
    }

    addAction.update();
  }
  
  public void setShowReferencesTo(boolean showReferencesTo) {
    if (showReferencesTo != referencesToButton.isSelected()) {
      if (showReferencesTo) {
        referencesToButton.setSelected(true);
      } else {
        referencedFromButton.setSelected(true);
      }
    }
  }

  public boolean getShowReferencesTo() {
    return referencesToButton.isSelected();
  }
  
  /**
   * The handler of selections.
   */
  private class SelectionHandler implements ElementSelectionListener, ListSelectionListener {

   public void selectionChanged(ElementSelectionEvent ev) {
  
     elements.clear();
     elements.addAll(session.getSelectionModel().getSelectedElements());
     updateReferences();
   }

   public void valueChanged(ListSelectionEvent e) {
     removeAction.update();
   }
 }

  /**
   * Note that <em>Spin</em> ensures that the methods of this listeners are called
   * on the EDT, although a change in the organ might be triggered by a change
   * on a MIDI thread.
   */
  private class ReferencesModel extends AbstractListModel implements OrganListener {

    public int getSize() {
      if (references == null) {
        return 0;
      } else {
        return references.size();
      }
    }

    public Object getElementAt(int index) {
      return references.get(index);
    }
        
    public void elementAdded(OrganEvent event) { }

    public void elementRemoved(OrganEvent event) { }

    public void elementChanged(final OrganEvent event) {
      if (references != null) {
        int index = references.indexOf(event.getElement());
        if (index != -1) {          
          fireContentsChanged(this, index, index);
        }
      }
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
    
    public void referenceChanged(OrganEvent event) { }

    public void referenceAdded(OrganEvent event) {
      updateReferences();
    }

    public void referenceRemoved(OrganEvent event) {
      updateReferences();
    }
  }
  
  public class ReferenceListCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

      Element element = (Element)value;

      String name = ElementUtils.getElementAndTypeName(element, sortNameButton.isSelected());

      super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
      
      setIcon(referenceIcon);
            
      return this;
    }
  }
    
  private class AddAction extends AbstractAction  {

    public AddAction() {
      putValue(Action.NAME             , resources.getString("construct.action.reference.add.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("construct.action.reference.add.description"));
      putValue(Action.SMALL_ICON       , new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/add.gif")));

      setEnabled(false);
   }

    public void actionPerformed(ActionEvent ev) {
      if (elements.size() > 0) {
       CreateReferencesWizard.showInDialog((JFrame)SwingUtilities.getWindowAncestor(ReferencesPanel.this), session.getOrgan(), elements);
      }
    }

    public void update() {
      setEnabled(references != null && elements.size() > 0);
    }
  }
  
  private class RemoveAction extends AbstractAction  {

    public RemoveAction() {
      putValue(Action.NAME             , resources.getString("construct.action.reference.remove.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("construct.action.reference.remove.description"));
      putValue(Action.SMALL_ICON, new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/remove.gif")));

      setEnabled(false);
   }

    public void actionPerformed(ActionEvent ev) {
      int[] indices = list.getSelectedIndices();
      if (indices != null) {
        for (int i = indices.length - 1; i >= 0; i--) {
          Element remove = (Element)references.get(indices[i]);

          for (int e = 0; e < elements.size(); e++) {
            Element element = (Element)elements.get(e);
            if (getShowReferencesTo()) {
              element.unreference(remove);
            } else {
              remove.unreference(element);
            }
          }
        }
      }
    }
    
    public void update() {
      setEnabled(references != null && list.getSelectedIndex() != -1);
    }
  }
}