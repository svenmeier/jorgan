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
import javax.swing.*;
import javax.swing.text.*;

/**
 * PropertyEditor for a shortcut property.
 */
public class ShortcutEditor extends CustomEditor {

  private JTextField textField = new JTextField(new ShortcutDocument(), "", 1);

  public String format(Object value) {

    Character shortcut = (Character)value;

    if (shortcut == null) {
      return "";
    } else {
      return shortcut.toString();
    }
  }

  public Component getCustomEditor(Object value) {

    Character shortcut = (Character)value;
    if (shortcut == null){
      textField.setText("");
    } else {
      textField.setText(shortcut.toString());
    }

    return textField;
  }

  public Object getEditedValue() {
    String text = textField.getText();
    if ("".equals(text)) {
      return null;
    } else {
      return new Character(text.charAt(0));
    }
  }

  private class ShortcutDocument extends PlainDocument {
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str.length() > 0) {
        str = str.toUpperCase().substring(0, 1);
      }
    
      remove(0, getLength());
      super.insertString(0, str, a);
    }
  }
}
