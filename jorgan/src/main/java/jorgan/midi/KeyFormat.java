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
package jorgan.midi;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * A format for keys.
 */
public class KeyFormat extends Format {

  private static final String[] names = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

  @Override
public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

    if (!(obj instanceof Integer)) {
        throw new IllegalArgumentException(obj.toString());
    }
    
    int pitch = ((Integer)obj).intValue();
    
    if (pitch < 0 || pitch > 127) {
        throw new IllegalArgumentException(obj.toString());       
    }

    toAppendTo.append(names[pitch % 12] + (pitch/12 - 1));

    return toAppendTo;
  }

  @Override
public Object parseObject(String source, ParsePosition pos) {
    try {
      int pitch;
      
      String note;
      if ('#' == source.charAt(1)) {
        note  = source.substring(0, 2);
        pitch = (Integer.parseInt(source.substring(2)) + 1) * 12;
      } else {
        note = source.substring(0, 1);
        pitch = (Integer.parseInt(source.substring(1)) + 1) * 12;
      }
      for (int n = 0; ; n++) {
        if (names[n].equals(note)) {
          break;
        }
        pitch++;
      }

      return new Integer(pitch);
    } catch (Exception ex) {
      throw new IllegalArgumentException(source);
    }
  }
}
