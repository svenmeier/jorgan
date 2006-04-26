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

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * PropertyEditor for a scale property.
 */
public class ZoomEditor extends PropertyEditorSupport {

    private NumberFormat numberFormat = NumberFormat.getNumberInstance();

    private NumberFormat percentFormat = NumberFormat.getPercentInstance();

    public String getAsText() {

        Float ff = (Float) getValue();

        if (ff == null) {
            return "";
        } else {
            return percentFormat.format(ff);
        }
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || "".equals(text)) {
            setValue(null);
        } else {
            float value;
            try {
                value = percentFormat.parse(text).floatValue();
            } catch (ParseException noPercent) {
                try {
                    value = numberFormat.parse(text).floatValue() / 100f;
                } catch (ParseException noNumber) {
                    throw new IllegalArgumentException("unable to set as text");
                }
            }

            setValue(new Float(value));
        }
    }
}
