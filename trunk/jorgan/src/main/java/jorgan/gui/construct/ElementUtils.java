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
package jorgan.gui.construct;

import java.util.ResourceBundle;

import jorgan.disposition.Element;
import jorgan.docs.Documents;

public class ElementUtils {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");
  
  public static String getElementName(Element element) {

    String name = element.getName();
    if ("".equals(name)) {
      name = getTypeName(element.getClass());                  
    }

    return name;
  }

  public static String getElementAndTypeName(Element element, boolean alphabetic) {

    String elementName = getElementName(element);
    String typeName = getTypeName(element.getClass());
    
    if (elementName.equals(typeName)) {
        return elementName;
    }
    
    if (alphabetic) {
      return elementName + " - " + typeName;  
    } else {
      return typeName + " - " + elementName;  
    }
  }
  
  public static String getTypeName(Class clazz) {

    return Documents.getInstance().getDisplayName(clazz);
  }
}