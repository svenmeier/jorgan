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
package jorgan.swing.wizard;

import java.util.*;

/**
 * A wizard implementation.
 */
public class BasicWizard implements Wizard {

  private ArrayList listeners = new ArrayList();
  
  private ArrayList pages = new ArrayList();

  protected Page current = null;
  
  public void addPage(Page page) {
    addPage(pages.size(), page);
  }
  
  public void addPage(int index, Page page) {
    pages.add(index, page);
    page.setWizard(this);
    
    if (current == null) {
      setCurrentPage(page);
    }
    
    fireWizardChanged();
  }
  
  public void removePage(Page page) {
    if (current == page) {
      setCurrentPage((Page)pages.get(0));
    }

    pages.remove(page);
    page.setWizard(null);

    fireWizardChanged();
  }
  
  public boolean hasPrevious() {
    return pages.indexOf(current) > 0;
  }

  public boolean hasNext() {
    return pages.indexOf(current) < pages.size() - 1;
  }

  public Page getCurrentPage() {
    return current;  
  }

  public void next() {  
    if (current.leavingToNext()) {
      int index = pages.indexOf(current);

      Page page = (Page)pages.get(index + 1);
        
      page.enteringFromPrevious();

      setCurrentPage(page);
    }
  }

  public void previous() {  
    if (current.leavingToPrevious()) {
      int index = pages.indexOf(current);

      Page page = (Page)pages.get(index - 1);
        
      page.enteringFromNext();

      setCurrentPage(page);      
    }
  }

  public boolean allowsFinish() {
    return true;
  }
    
  public final void finish() {
    if (current.leavingToNext() && finishImpl()) {
      
      fireWizardFinished();
    }
  }
  
  protected boolean finishImpl() {
    return true;
  }
  
  public void cancel() {
    cancelImpl();
      
    fireWizardCanceled();
  }
  
  protected void cancelImpl() {
  }
  
  public void pageChanged(Page page) {
    fireWizardChanged(); 
  }

  protected void setCurrentPage(Page page) {
    current = page;
       
    fireWizardChanged();
  }

  public void addWizardListener(WizardListener listener) {
    listeners.add(listener);  
  }
  
  public void removeWizardListener(WizardListener listener) {
    listeners.remove(listener);  
  }

  private void fireWizardChanged() {
    for (int l = 0; l < listeners.size(); l++) {
      WizardListener listener = (WizardListener)listeners.get(l);

      listener.wizardChanged();
    }
  }
    
  private void fireWizardFinished() {
    for (int l = 0; l < listeners.size(); l++) {
      WizardListener listener = (WizardListener)listeners.get(l);

      listener.wizardFinished();
    }
  }
  
  private void fireWizardCanceled() {
    for (int l = 0; l < listeners.size(); l++) {
      WizardListener listener = (WizardListener)listeners.get(l);

      listener.wizardCanceled();
    }
  }
}