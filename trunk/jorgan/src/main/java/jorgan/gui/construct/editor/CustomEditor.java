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
import java.beans.*;

/**
 * Abstract base class for propertyEditors that support a custom editor.
 */
public abstract class CustomEditor extends PropertyEditorSupport  {

  public final String getAsText() {
    return format(super.getValue());
  }

  public final boolean supportsCustomEditor() {
    return true;
  }

  public final Component getCustomEditor() {

    return getCustomEditor(super.getValue());
  }

  public abstract Component getCustomEditor(Object value);

  public final Object getValue() {

    Object editedValue = getEditedValue();
    if (editedValue == null && super.getValue() != null              ||
        editedValue != null && !editedValue.equals(super.getValue())) {
      setValue(editedValue);
    }

    return super.getValue();
  }

  protected abstract Object getEditedValue();
  
  protected abstract String format(Object value);
}
