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
package jorgan.gui.console;

import java.awt.*;
import java.awt.event.*;

import jorgan.disposition.*;

/**
 * A view that shows a piston.
 *
 * @see jorgan.disposition.Piston
 */
public class PistonView extends View {
 
  private static final int PADDING = 1;
  private static final int H_INSET = 8;
  private static final int V_INSET = 4;

  private boolean pressed = false;
  
  public PistonView(Piston piston) {
    super(piston);
  }

  protected Piston getPiston() {
    return (Piston)getElement();
  }
  
  protected Dimension getNonStyleSize() {

    Dimension dim = getNameSize();

    dim.width  += 2*(H_INSET + PADDING);
    dim.height += 2*(V_INSET + PADDING);

    return dim;
  }

  protected void paintNonStyle(Graphics2D g) {

    Dimension size = getNonStyleSize();
    
    g.setColor(Color.black);
    paintPiston(g, PADDING, PADDING, size.width - 2*PADDING, size.height - 2*PADDING);

    g.setColor(Color.black);
    g.setFont(getNonStyleFont());
    paintName(g, PADDING, PADDING, size.width - 2*PADDING, size.height - 2*PADDING);
  }

  private void paintPiston(Graphics2D g, int x, int y, int width, int height) {  
    g.drawRect(x, y, width - 1, height - 1);
    if (pressed) {
      g.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
  }
  
  protected boolean isNonStylePressable(int x, int y, MouseEvent ev) {
    return true; 
  }
  
  public void pressed(int x, int y, MouseEvent ev) {
    pressed = true;

    Piston piston = getPiston();

    if ((ev.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == 0) {
      piston.get();
    } else {
      if (!piston.isFixed()) {
        piston.set();
      }
    }

    // issue repaint since piston is actually not changed
    repaint();
  }
  
  public void released(int x, int y, MouseEvent ev) {
    pressed = false;

    // issue repaint since piston is actually not changed
    repaint();
  }
  
  public void pressed(KeyEvent ev) {

    Piston piston = getPiston();

    Shortcut shortcut = piston.getShortcut();
    if (shortcut != null && shortcut.match(ev)) {
      if ((ev.getModifiers() & KeyEvent.CTRL_MASK) == 0) {
        piston.get();
      } else {
        piston.set();        
      }
    }
  } 

  protected int getStateIndex() {
    return Math.min(style.getStateCount() - 1, pressed ? 1 : 0);
  }
    
  protected Font getNonStyleFont() {
    return Configuration.instance().getPistonFont();  
  }
}

