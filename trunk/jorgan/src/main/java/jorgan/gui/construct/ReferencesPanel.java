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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;

import jorgan.disposition.*;
import jorgan.disposition.event.*;
import jorgan.gui.ElementSelectionModel;
import jorgan.gui.event.*;

/**
 * Panel shows the references of elements.
 */
public class ReferencesPanel extends JPanel {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private static final Icon alphabetIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/alphabet.gif"));

  private static final Icon referencesToIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/referencesTo.gif"));

  private static final Icon referencedFromIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/referencedFrom.gif"));

  private static final Icon referenceTotalIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/referenceTotal.gif"));

  private static final Icon referencePartialIcon =
    new ImageIcon(ElementsPanel.class.getResource("/jorgan/gui/img/referencePartial.gif"));

  /**
   * The edited organ.
   */
  private Organ organ;

  private List elements = new ArrayList();
  
  private List references          = new ArrayList();
  private Map  referencesByElement = new HashMap();
  
  /**
   * The listener to selection changes.
   */
  private SelectionHandler selectionHandler = new SelectionHandler();

  /**
   * The model for selection.
   */
  private ElementSelectionModel selectionModel;
  
  private AddAction addAction = new AddAction();
  private RemoveAction removeAction = new RemoveAction();

  private JList list = new JList();

  private JToolBar toolBar = new JToolBar();
  
  private JToggleButton alphabetButton = new JToggleButton(alphabetIcon);
  private JToggleButton referencesToButton   = new JToggleButton(referencesToIcon);
  private JToggleButton referencedFromButton = new JToggleButton(referencedFromIcon);

  private ReferencesModel referencesModel = new ReferencesModel();

  /**
   * Create a tree panel.
   */
  public ReferencesPanel() {
    
    setLayout(new BorderLayout());

    toolBar.setRollover(true);
    toolBar.setFloatable(false);

    toolBar.add(addAction);
    toolBar.add(removeAction);

    toolBar.addSeparator();

    alphabetButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        updateReferences();
      }
    });
    toolBar.add(alphabetButton);

    toolBar.addSeparator();

    ButtonGroup group = new ButtonGroup();
    referencesToButton.getModel().setGroup(group);
    referencesToButton.setSelected(true);
    referencesToButton.getModel().addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        updateReferences();
      }
    });
    toolBar.add(referencesToButton);

    referencedFromButton.getModel().setGroup(group);
    toolBar.add(referencedFromButton);

    add(toolBar, BorderLayout.NORTH);

    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    list.setModel(referencesModel);
    list.setCellRenderer(new ReferenceListCellRenderer());
    list.addListSelectionListener(selectionHandler);
    list.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int index = list.getSelectedIndex();
          if (index != -1) {
            Reference reference = (Reference)references.get(index); 
            Element   element   = reference.getElement();
            
            selectionModel.setSelectedElement(element);
          }
        }
      }
    });
        
    JScrollPane scrollPane = new JScrollPane(list);
    scrollPane.setBorder(new EmptyBorder(0,0,0,0));
    add(scrollPane, BorderLayout.CENTER);

    setSelectionModel(new ElementSelectionModel());
  }
  
  public void setSelectionModel(ElementSelectionModel selectionModel) {
    if (selectionModel == null) {
      throw new IllegalArgumentException("selectionModel must not be null");
    }

    if (this.selectionModel != null) {
      this.selectionModel.removeSelectionListener(selectionHandler);
    }

    this.selectionModel = selectionModel;

    selectionModel.addSelectionListener(selectionHandler);
  }

  /**
   * Set the organ to be edited.
   *
   * @param organ organ to be edited
   */
  public void setOrgan(Organ organ) {
    if (this.organ != null) {
      this.organ.removeOrganListener((OrganListener)Spin.over(referencesModel));
    }

    this.organ = organ;
        
    if (organ != null) {
      organ.addOrganListener((OrganListener)Spin.over(referencesModel));
    }    
  }

  private void updateReferences() {

    referencesByElement.clear();
    int size = references.size();
    references.clear();
    referencesModel.fireRemoved(size);

    for (int e = 0; e < elements.size(); e++) {
      Element element = (Element)elements.get(e);
      
      if (getShowReferencesTo()) {
        for (int r = 0; r < element.getReferencesCount(); r++) {
          Element referenced = element.getReference(r).getElement();
          
          Reference reference = (Reference)referencesByElement.get(referenced);
          if (reference == null) {
            reference = new Reference(referenced);
            referencesByElement.put(referenced, reference);
            references.add(reference);
          } else {
            reference.increase();
          }
        }
      } else {       
        Iterator iterator = element.getReferrer();
        while (iterator.hasNext()) {
          Element referrer = (Element)iterator.next();

          Reference reference = (Reference)referencesByElement.get(referrer);
          if (reference == null) {
            reference = new Reference(referrer);
            referencesByElement.put(referrer, reference);
            references.add(reference);
          } else {
            reference.increase();
          }
        }
      }
    }
    Collections.sort(references);      
    referencesModel.fireAdded(references.size());

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
     elements.addAll(selectionModel.getSelectedElements());
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
      return references.size();
    }

    public Object getElementAt(int index) {
      return references.get(index);
    }
        
    public void elementAdded(OrganEvent event) { }

    public void elementRemoved(OrganEvent event) { }

    public void elementChanged(final OrganEvent event) {
      Reference reference = (Reference)referencesByElement.get(event.getElement());
      if (reference != null) {
        int index = references.indexOf(reference);
        fireContentsChanged(this, index, index);
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

      if (getShowReferencesTo()) {
        if (elements.contains(event.getElement())) {
          Element referenced = event.getReference().getElement();

          Reference reference = (Reference)referencesByElement.get(referenced);
          if (reference == null) {
            reference = new Reference(referenced);
            referencesByElement.put(referenced, reference);
            references.add(reference);

            int index = references.size() - 1;
            fireIntervalAdded(this, index, index);
          } else {
            reference.increase();

            int index = references.indexOf(reference);
            fireContentsChanged(this, index, index);
          }
        }
      } else {
        if (elements.contains(event.getReference().getElement())) {
          Element referrer = event.getElement();

          Reference reference = (Reference)referencesByElement.get(referrer);
          if (reference == null) {
            reference = new Reference(referrer);
            referencesByElement.put(referrer, reference);
            references.add(reference);

            int index = references.size() - 1;
            fireIntervalAdded(this, index, index);
          } else {
            reference.increase();

            int index = references.indexOf(reference);
            fireContentsChanged(this, index, index);
          }
        }        
      }
    }

    public void referenceRemoved(OrganEvent event) {
      if (getShowReferencesTo()) {
        if (elements.contains(event.getElement())) {
          Element dereferenced = event.getReference().getElement();

          Reference reference = (Reference)referencesByElement.get(dereferenced);
          reference.decrease();
          if (reference.getCount() == 0) {
            referencesByElement.remove(dereferenced);
            int index = references.indexOf(reference);
            references.remove(reference);
            
            fireIntervalRemoved(this, index, index);
          } else {
            int index = references.indexOf(reference);
            fireContentsChanged(this, index, index);
          }
        }
      } else {
        if (elements.contains(event.getReference().getElement())) {
          Element dereferrer = event.getElement();

          Reference reference = (Reference)referencesByElement.get(dereferrer);
          reference.decrease();
          if (reference.getCount() == 0) {
            referencesByElement.remove(dereferrer);
            int index = references.indexOf(reference);
            references.remove(reference);
            
            fireIntervalRemoved(this, index, index);
          } else {
            int index = references.indexOf(reference);
            fireContentsChanged(this, index, index);
          }
        }
      }
    }
  }
  
  private class Reference implements Comparable {
    private Element element;
    private int     count = 1;
    
    public Reference(Element element) {
      this.element = element;
    }
    
    public Element getElement() {
      return element;
    }
    
    public int getCount() {
      return count;
    }
    
    public void increase() {
      count++;
    }
    
    public void decrease() {
      count--;
    }

    public int compareTo(Object o) {
      Reference reference = (Reference)o;
      return new ElementComparator(alphabetButton.isSelected()).compare(this.element, reference.element);
    }
  }
  
  public class ReferenceListCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

      Reference reference = (Reference)value;
      
      Element element = reference.getElement();

      String name = ElementUtils.getElementAndTypeName(element, alphabetButton.isSelected());

      super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
      
      if (reference.getCount() == elements.size()) {
        setIcon(referenceTotalIcon);
      } else {
        setIcon(referencePartialIcon);
      }
            
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
       CreateReferencesWizard.showInDialog((JFrame)SwingUtilities.getWindowAncestor(ReferencesPanel.this), organ, elements);
      }
    }

    public void update() {
      setEnabled(elements.size() > 0);
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
          Reference reference = (Reference)references.get(indices[i]); 
          Element   remove    = reference.getElement();

          for (int e = 0; e < elements.size(); e++) {
            Element element = (Element)elements.get(e);
            if (getShowReferencesTo()) {
              if (element.getReference(remove) != null) {
                element.unreference(remove);
              }
            } else {
              if (remove.getReference(element) != null) {
                remove.unreference(element);
              }
            }
          }
        }
      }
    }
    
    public void update() {
      setEnabled(list.getSelectedIndex() != -1);
    }
  }
}