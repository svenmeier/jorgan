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
package jorgan.gui.imports;

import java.util.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

import javax.swing.*;

import jorgan.swing.wizard.*;

import jorgan.disposition.*;
import jorgan.gui.imports.spi.ImportProvider;

/**
 * A wizard for importing of sounds.
 */
public class ImportWizard extends BasicWizard {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");
  
  private Organ organ;

  private ImportProvider provider;
  private java.util.List stops;
  private java.util.List selectedStops;

  /**
   * Create a new wizard.
   */    
  public ImportWizard(Organ organ) {
    this.organ = organ;
    
    addPage(new ProviderSelectionPage());
    addPage(new ImportOptionsPage());
    addPage(new StopSelectionPage());
  }
    
  /**
   * Allows finish only if stops are selected.
   * 
   * @return  <code>true</code> if stops are selected
   */
  public boolean allowsFinish() {
    return selectedStops != null && selectedStops.size() > 0;
  }

  /**
   * Finish.
   */
  protected boolean finishImpl() {

    for (int s = 0; s < selectedStops.size(); s++) {
      organ.addElement((Element)selectedStops.get(s));
    }
 
    return true;
  }
  
  /**
   * Page for selection of an import provider.
   */
  private class ProviderSelectionPage extends AbstractPage {

    private ProviderSelectionPanel providerSelectionPanel = new ProviderSelectionPanel();

    public ProviderSelectionPage() {
      
      providerSelectionPanel.addPropertyChangeListener(this);
    }
  
    public String getDescription() {
      return resources.getString("import.method.description");
    }
  
    public JComponent getComponent() {
      return providerSelectionPanel;
    }    

    public boolean allowsNext() {
      return providerSelectionPanel.getSelectedImportProvider() != null;
    }  

    public boolean leavingToNext() {
      provider = providerSelectionPanel.getSelectedImportProvider();
      
      return true;
    }
  }
 
  /**
   * Page for altering of options of the selected importMethod.
   */
  private class ImportOptionsPage extends AbstractPage {

    private JPanel optionsPanel;
    
    public ImportOptionsPage() {
    }
  
    public void enteringFromPrevious() {
      if (this.optionsPanel != null) {
        this.optionsPanel.removePropertyChangeListener(this);         
      }

      this.optionsPanel = provider.getOptionsPanel();

      if (this.optionsPanel != null) {
        this.optionsPanel.addPropertyChangeListener(this);         
      }
    }

    public String getDescription() {
      return provider.getDescription();
    }
    
    public JComponent getComponent() {
      return optionsPanel;
    }
  
    public boolean allowsNext() {
      return provider.hasStops();
    }

    public boolean leavingToNext() {
      stops = provider.getStops();
    
      return stops.size() > 0;
    }
  }

  /**
   * Page for selecting of stops to import.
   */
  private class StopSelectionPage extends AbstractPage {

    private StopSelectionPanel stopSelectionPanel = new StopSelectionPanel();

    public StopSelectionPage() {
      stopSelectionPanel.addPropertyChangeListener(this);
    }
 
    public void enteringFromPrevious() {
      stopSelectionPanel.setStops(stops);  
    }

    public String getDescription() {
      return resources.getString("import.stop.description");
    }

    public JComponent getComponent() {
      return stopSelectionPanel;
    }
  
    public boolean leavingToPrevious() {
      selectedStops = null;

      return true;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
      selectedStops = stopSelectionPanel.getSelectedStops();

      super.propertyChange(evt);
    }
  }
   
  /**
   * Show an import wizard in a dialog.
   * 
   * @param owner   owner of dialog
   * @param organ   organ to import into
   */ 
  public static void showInDialog(Frame owner, Organ organ) {

    WizardDialog dialog = new WizardDialog(owner);
        
    dialog.setTitle(resources.getString("import.title"));  
    
    dialog.setWizard(new ImportWizard(organ));
    
    dialog.start();
    
    dialog.dispose();
  }
}