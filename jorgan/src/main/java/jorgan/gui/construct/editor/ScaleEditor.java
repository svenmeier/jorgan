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
import java.beans.*;

/**
 * PropertyEditor for a scale property.
 */
public class ScaleEditor extends PropertyEditorSupport {

  private NumberFormat numberFormat  = NumberFormat.getNumberInstance();
  private NumberFormat percentFormat = NumberFormat.getPercentInstance();
  
  public String getAsText() {

    Double dd = (Double)getValue();

    if (dd == null) {
      return "";
    } else {
      return percentFormat.format(dd);
    }
  }

  public void setAsText(String text) throws IllegalArgumentException {
    if (text == null || "".equals(text)) {
      setValue(null);
    } else {
      double value;
      try {
        value = percentFormat.parse(text).doubleValue();
      } catch (ParseException noPercent) {
        try {
          value = numberFormat.parse(text).doubleValue() / 100d;
        } catch (ParseException noNumber) {
          throw new IllegalArgumentException("unable to set as text");
        }
      }

      setValue(new Double(value));
    }
  }
}
