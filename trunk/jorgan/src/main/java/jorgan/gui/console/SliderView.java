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
 * A view for a slider.
 */
public class SliderView extends View {

  private static final int HEIGHT = 13;
  private static final int WIDTH  = 80;
  private static final int INSET  = 4;

  private boolean pressed = false;
  private int     oldX;
  private int     oldY;

  public SliderView(Slider slider) {
    super(slider);
  }
    
  protected Slider getSlider() {
    return (Slider)getElement();
  }
  
  protected int getStateIndex() {
    Slider slider = (Slider)getElement();

    return slider.getPosition() * style.getStateCount() / 128; 
  }

  protected Font getNonStyleFont() {
    Font font = null;
    if (getSlider() instanceof Swell) {
      font = Configuration.instance().getSwellFont();
    } else if (getSlider() instanceof Crescendo) {
      font = Configuration.instance().getCrescendoFont();
    }
    return font;  
  }

  protected Dimension getNonStyleSize() {
    
    Dimension dim = getNameSize();
    
    dim.width   = Math.max(dim.width, WIDTH) + 2*INSET;
    dim.height += HEIGHT + 3*INSET;

    return dim;
  }

  protected void paintNonStyle(Graphics2D g) {
    Dimension size = getNonStyleSize();

    g.setColor(Color.black);
    g.setFont(getNonStyleFont());
    paintName(g, INSET, INSET, size.width - 2*INSET, size.height - 3*INSET - HEIGHT);   

    g.setColor(Color.black);
    paintSwell(g, (size.width - WIDTH)/2, size.height - HEIGHT - INSET, WIDTH, HEIGHT);
  }
  
  private void paintSwell(Graphics2D g, int x, int y, int width, int height) {
    Slider slider = (Slider)getElement();

    g.drawRect(x, y, width - 1, height - 1);
    if (pressed) {
      g.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
    
    int delta = (width - 2*3) * slider.getPosition() / 127;
    g.fillRect(x + 3, y + 3, delta, height - 2*3);
  }
  
  protected boolean isNonStylePressable(int x, int y, MouseEvent ev) {
    return true; 
  }
  
  public void pressed(int x, int y, MouseEvent ev) {
    pressed = true; 

    oldX = x;
    oldY = y;
        
    dragged(x, y, ev);
  }

  public void released(int x, int y, MouseEvent ev) {
    pressed = false; 

    // issue repaint since slider is actually not changed
    repaint();
  }

  public void dragged(int x, int y, MouseEvent ev) {
    Slider slider = (Slider)getElement();

    int delta;
    if (!isStyled()) {
      delta = (x - oldX) * 127 / getWidth();
    } else { 
      if (getWidth() > getHeight()) {
        delta = (x - oldX) * 127 / getWidth();
      } else {
        delta = (oldY - y) * 127 / getHeight();
      }
    }

    slider.setPosition(slider.getPosition() + delta);
                
    oldX = x;
    oldY = y;
  }  
}

