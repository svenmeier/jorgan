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
package jorgan.gui.construct.editor;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;

import jorgan.disposition.Shortcut;

/**
 * PropertyEditor for a shortcut property.
 */
public class ShortcutEditor extends CustomEditor {

  private ShortcutField shortcutField = new ShortcutField();

  public String format(Object value) {

    Shortcut shortcut = (Shortcut)value;

    if (shortcut == null) {
      return "";
    } else {
      return shortcut.toString();
    }
  }

  public Component getCustomEditor(Object value) {

    shortcutField.setShortcut((Shortcut)value);

    return shortcutField;
  }

  public Object getEditedValue() {
    return shortcutField.getShortcut();
  }
  
  private class ShortcutField extends JTextField implements KeyEventDispatcher {
     
    private Shortcut shortcut;
    
    public ShortcutField() {
      setEditable(false);
    }
    
    public void addNotify() {
        super.addNotify();
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }
      
    public void removeNotify() {       
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
        
        super.removeNotify();
    }
      
    public void setShortcut(Shortcut shortcut) {
      this.shortcut = shortcut;
      
      if (shortcut == null){
        setText("");
      } else {
        setText(shortcut.toString());
      }
    }
    
    public Shortcut getShortcut() {
      return shortcut;
    }
    
    public boolean dispatchKeyEvent(KeyEvent e) {
      if (e.getID() == KeyEvent.KEY_PRESSED) {
        setShortcut(Shortcut.createShortCut(e));            
        return true;
      }                

      return false;
    }
  }
}
