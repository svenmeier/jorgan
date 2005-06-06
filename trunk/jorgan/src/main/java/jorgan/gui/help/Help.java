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
package jorgan.gui.help;

import java.net.*;

import javax.swing.*;

import javax.help.*;

import jorgan.docs.Documents;

/**
 * Help for jOrgan.
 */
public class Help {

  private boolean available;
  
  /**
   * The help set.
   */
  private HelpSet set;

  /**
   * The broker used to show the help set.
   */
  private HelpBroker broker;
  
  /**
   * Create a JavaHelp wrapper.
   * <br>
   * The helpSet must be located in the directory './docs/help/' or
   * the file './docs/help.zip' relative to the installation directory,
   * otherwise an empty helpSet is used.
   * 
   * @see jorgan.docs.Documents#getHelp()
   */
  public Help() {
  }
  
  public boolean isAvailable() {
      getBroker();
      
      return available;
  }
  
  /**
   * Set the associated helpBroker to be displayed or not
   * 
   * @param displayed   should helpBroker be displayed
   */
  public void setDisplayed(boolean displayed) {
      getBroker().setDisplayed(displayed);  
  }
  
  /**
   * Enable the help key for the given rootPane.
   * 
   * @param rootPane    rootPane to enable help key for
   * @param helpID      ID of help topic
   */
  public void enableHelpKey(JRootPane rootPane, String helpID) {
    getBroker().enableHelpKey(rootPane, helpID, set);
  }

  public void enableHelpOnButton(JButton button, String helpID) {
    getBroker().enableHelpOnButton(button, helpID, set);
  }
  
  public void enableHelpOnMenuItem(JMenuItem menuItem, String helpID) {
    getBroker().enableHelpOnButton(menuItem, helpID, set);
  }
  
  public static void setHelpIDString(JComponent component, String helpID) {
    CSH.setHelpIDString(component, helpID);
  }
  
  protected HelpBroker getBroker() {
    if (broker == null) {
      set = new HelpSet();
           
      try {
        URL url = Documents.getInstance().getHelp();
        set = new HelpSet(getClass().getClassLoader(), url);
        available = true;
      } catch (Exception e) {
        available = false;
      }
      broker = set.createHelpBroker();
    }
    return broker;
  }
}