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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import jorgan.disposition.Console;

/**
 * JDialog subclass to show a console <em>full screen</em>.
 */
public class ConsoleDialog extends JDialog {

  /**
   * The scrollpane to contain the viewPanel.
   */
  private JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 

  /**
   * The handler of scrolling.
   */
  private ScrollHandler scrollHandler = new ScrollHandler();

  private ConsolePanel consolePanel = new ConsolePanel();
    
  /**
   * Create a dialog.
   */
  public ConsoleDialog(JFrame owner, GraphicsConfiguration configuration) {
    super(owner, null, false, configuration);

    setUndecorated(true);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    // cover whole screen (including windows start bar)
    setBounds(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

    scrollPane.setBorder(null);
    getContentPane().add(scrollPane);

    consolePanel.addMouseListener      (scrollHandler);
    consolePanel.addMouseMotionListener(scrollHandler);
    scrollPane.setViewportView(consolePanel);
  }

  /**
   * Set the console to shown in <em>full screen</em>.
   */
  public void setConsole(Console console) {
    
    consolePanel.setConsole(console);
    
    setTitle(console.getName());
  }

  /**
   * The handler for scrolling.
   */
  private class ScrollHandler extends MouseInputAdapter implements ActionListener {

    private Timer timer;

    private int deltaX;
    private int deltaY;

    public ScrollHandler() {
      timer = new Timer(50, this);
    }

    public void mouseExited(MouseEvent e) {
      if (timer.isRunning()) {
        timer.stop();
      }
    }

    public void mouseDragged(MouseEvent e) {
      mouseMoved(e);
    }
    
    public void mouseMoved(MouseEvent e) {
      
      Rectangle rect = scrollPane.getViewport().getViewRect();

      int x = e.getX() - (rect.x + rect.width/2); 
      int y = e.getY() - (rect.y + rect.height/2); 

      deltaX = (int)(Math.pow((double)x / (rect.width /2), 5) * (rect.width  / 5));
      deltaY = (int)(Math.pow((double)y / (rect.height/2), 5) * (rect.height / 5)); 

      if (deltaX != 0 || deltaY != 0) {
        if (!timer.isRunning()) {
          timer.start();
        }
      } else {
        if (timer.isRunning()) {
          timer.stop();
        }
      }
    }
    
    public void actionPerformed(ActionEvent e) {
      // must change horizontal and vertical value separately
      // or otherwise scrollpane will not use blitting :(
      scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getValue() + deltaX);
      scrollPane.getVerticalScrollBar()  .setValue(scrollPane.getVerticalScrollBar()  .getValue() + deltaY);
    }
  }
  
  public static ConsoleDialog showConsole(JFrame owner, Console console) {
      
    String screen = console.getScreen();
    if (screen != null) {
      GraphicsConfiguration configuration = null;
      if (!"".equals(screen)) {
        GraphicsEnvironment   environment = GraphicsEnvironment.getLocalGraphicsEnvironment();    
        GraphicsDevice[] devices = environment.getScreenDevices();    
          for (int d = 0; d < devices.length; d++) {
          if (devices[d].getIDstring().equals(screen)) {
            configuration = devices[d].getDefaultConfiguration();
          }
        }
      }    

      ConsoleDialog dialog = new ConsoleDialog(owner, configuration);      
      dialog.setConsole(console);
      dialog.setVisible(true);
        
      return dialog;
    }  else {
      return null;
    }
  }
}
