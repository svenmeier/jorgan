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

import javax.swing.*;

/**
 * A single page of a wizard.
 */
public interface Page {

  /**
   * Set the containing wizard of this page.
   * 
   * @param wizard    containing wizard
   */
  public void setWizard(Wizard wizard);
  
  /**
   * Notification that this page is entered from the next page.
   */
  public void enteringFromNext();
   
  /**
   * Notification that this page is entered from the previous page.
   */
  public void enteringFromPrevious();  

  /**
   * Get a description.
   * 
   * @return  description
   */
  public String getDescription();
  
  /**
   * Get the component for this page.
   * 
   * @return  the component used to display this page
   */
  public JComponent getComponent();    

  /**
   * Is leaving to the next page allowed.
   * 
   * @return  <code>true</code> if this page can be leaved to the next page
   */
  public boolean allowsNext();
   
  /**
   * Is leaving to the previous page allowed.
   * 
   * @return  <code>true</code> if this page can be leaved to the previous page
   */
  public boolean allowsPrevious();

  /**
   * Notification that this page is leaved to the next page.
   */       
  public boolean leavingToNext();
   
  /**
   * Notification that this page is leaved to the previous page.
   */       
  public boolean leavingToPrevious();  
}