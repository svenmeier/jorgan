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

import java.io.File;
import java.net.*;
import java.util.Locale;

import javax.swing.*;

import javax.help.*;

import jorgan.util.Installation;

/**
 * Help for jOrgan.
 */
public class Help {
  
  /**
   * Is help available.
   */
  private static Boolean available;
  
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
   * @see jorgan.util.Installation#getInstallDirectory()
   */
  public Help() {

    if (isAvailable()) {
      set = new HelpSet();

      File installDir = Installation.getInstallDirectory(getClass());
      File helpDir    = new File(installDir, "help");
      if (helpDir.exists() && helpDir.isDirectory()) {
        try {
          Locale locale = Locale.getDefault();
          
          String localeSuffix = locale.toString();
          while (true) {
            File helpZip = new File(helpDir, "help_" + localeSuffix + ".zip");
            if (helpZip.exists() && helpZip.isFile()) {
              URL url = new URL("jar:" + helpZip.toURL() + "!/jhelpset.hs");

              set = new HelpSet(getClass().getClassLoader(), url);

              break;
            }
            
            File helpFile = new File(new File(helpDir, localeSuffix), "jhelpset.hs");
            if (helpFile.exists()) {
              set = new HelpSet(getClass().getClassLoader(), helpFile.toURL());
              break;
            }
           
            if (localeSuffix.length() == 0) {
              break; 
            } else {
              int index = localeSuffix.lastIndexOf('_');
              if (index == -1) {
                localeSuffix = "";
              } else {
                localeSuffix = localeSuffix.substring(0, index);
              }
            }
          }
        } catch (Exception e) {
          // keep empty help set
        }
      }

      broker = set.createHelpBroker();
    }
  }
  
  /**
   * Set the associated helpBroker to be displayed or not
   * 
   * @param displayed   should helpBroker be displayed
   */
  public void setDisplayed(boolean displayed) {
    if (isAvailable()) {
      broker.setDisplayed(displayed);  
    }
  }
  
  /**
   * Enable the help key for the given rootPane.
   * 
   * @param rootPane    rootPane to enable help key for
   * @param helpID      ID of help topic
   */
  public void enableHelpKey(JRootPane rootPane, String helpID) {
    if (isAvailable()) {
      broker.enableHelpKey(rootPane, helpID, set);
    }
  }

  public void enableHelpOnButton(JButton button, String helpID) {
    if (isAvailable()) {
      broker.enableHelpOnButton(button, helpID, set);
    }
  }
  
  public void enableHelpOnMenuItem(JMenuItem menuItem, String helpID) {
    if (isAvailable()) {
      broker.enableHelpOnButton(menuItem, helpID, set);
    }
  }
  
  public static void setHelpIDString(JComponent component, String helpID) {
    if (isAvailable()) {
      CSH.setHelpIDString(component, helpID);
    }
  }

  /**
   * Is help available - decided by the existence of JavaHelp classes.
   *  
   * @return    <code>true</code> if help is available
   */
  public static boolean isAvailable() {
    if (available == null) {
      try {
        Class.forName("javax.help.CSH");

        available = Boolean.TRUE;
      } catch (ClassNotFoundException ex) {
        available = Boolean.FALSE;
      }
    }
    return available.booleanValue();
  }   
}
