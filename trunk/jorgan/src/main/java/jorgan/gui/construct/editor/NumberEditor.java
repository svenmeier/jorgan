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

import java.text.*;
import java.awt.*;
import javax.swing.*;

/**
 * Property editor for numbers.
 */
public class NumberEditor extends CustomEditor {

  private JSpinner spinner;

  public NumberEditor() {

    spinner = new JSpinner(new SpinnerNumberModel(0, getMinimum(), getMaximum(), 1));

    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
    editor.getTextField().setBorder(null);
  }

  protected int getMinimum() {
    return Integer.MIN_VALUE;
  }

  protected int getMaximum() {
    return Integer.MAX_VALUE;
  }

  public String format(Object value) {
    return "" + value;
  }
  
  public Component getCustomEditor(Object value) {

    spinner.setValue(value);

    return spinner;
  }

  public Object getEditedValue() {

    try {
      JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
      editor.commitEdit();
    } catch (ParseException ex) {
      // invalid value so keep previous value
    }

    return spinner.getValue();
  }
}
