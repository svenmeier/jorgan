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

import java.util.*;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import jorgan.gui.ConsolePanel;
import jorgan.disposition.Active;
import jorgan.disposition.Console;
import jorgan.disposition.ConsoleReference;
import jorgan.disposition.Shortcut;
import jorgan.disposition.event.*;
import jorgan.skin.*;

/**
 * Abstract base class of all views representing a view on
 * one element of an organ.
 */
public abstract class View {
  
  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private java.util.List nameBreaks   = new ArrayList();
  
  private int x;
  private int y;
  private Dimension size;
  
  /**
   * The style of this view.
   */
  protected Style style;
  
  /**
   * The containing viewPanel. 
   */
  private ConsolePanel consolePanel;
  
  /**
   * The element represented by this view.
   */
  private jorgan.disposition.Element element;

  /**
   * Create a view for the given element.
   *
   * @param element element to create view for
   */
  public View(jorgan.disposition.Element element) {

    this.element = element;
  }

  public void setConsolePanel(ConsolePanel consolePanel) {
    this.consolePanel = consolePanel;
    
    if (consolePanel != null) {
      changeUpdate(null);
    }
  }
  
  public ConsolePanel getViewPanel() {
    return consolePanel;
  }
  
  /**
   * Get the element represented by this view.
   *
   * @return  the element
   */
  public jorgan.disposition.Element getElement() {
    return element;
  }

  public boolean contains(int x, int y) {
    
    return (this.x < x) && (this.x + size.width  > x) &&
           (this.y < y) && (this.y + size.height > y);
  }
  
  /**
   * Update this view in response to a change of an element.
   * <br>
   * This default implementation does nothing.
   * 
   * @param event   event of disposition
   */
  public void changeUpdate(OrganEvent event) {

    // issure repaint so old location gets cleared in case
    // of a new position or size
    repaint();

    if (event != null && !event.isDispositionChange()) {
      // nothing else to do in case of a registration change
      return;
    }
    
    style = null;
    Console console = consolePanel.getConsole();
    if (console != null && console.getSkin() != null) {
      Skin skin = SkinManager.instance().getSkin(console.getSkin());
      if (skin != null) {
        style = skin.getStyle(element.getStyle());
      }
    }

    ConsoleReference reference = (ConsoleReference)console.getReference(element);
    x = reference.getX();
    y = reference.getY();
        
    String name = element.getName();
    if ("".equals(name)) {
      name = resources.getString("element.emptyName");                  
    }
    if (style == null) {
      nameBreaks = breakNonStyleName(name); 
    } else {
      nameBreaks = breakStyleName(name); 
    }   

    if (style == null) {
      size = getNonStyleSize();
    } else {
      size = getStyleSize();
    }
    
    repaint();
  }
  
  protected void repaint() {
    if (size != null) {
      consolePanel.repaintView(this);
    }
  }

  /**
   * Break the given name in case this views element has no
   * associated style.
   *
   * @param name name to break
   * @return  list of broken parts
   */
  protected List breakNonStyleName(String name) {
  
    List nameBreaks = new ArrayList();
  
    nameBreaks.add(name);  
  
    return nameBreaks;
  }
  
  /**
   * Break the given name for the style of this views element.
   *
   * @param name name to break
   * @return  list of broken parts
   */
  protected List breakStyleName(String name) {

    // trim leading and trailing whitespace  
    name = name.trim();

    List nameBreaks = new ArrayList();
  
    char[] chars = name.toCharArray();
    int start = 0;
    int end   = chars.length;
    
    // more characters left?
    while (start != end) {
      // take all remaining characters
      int length = end - start;

      // honour line break (multiple whitespace)
      int lineBreak = name.indexOf("  ", start);
      if (lineBreak != -1) {
          length = lineBreak - start;
      }

      // check width for each states label 
      for (int s = 0; s < style.getStateCount(); s++) {
        State state = style.getState(s);
        Label label = state.getLabel();
        if (label != null) {
          FontMetrics metrics = getViewPanel().getFontMetrics(label.getFont());
          Rectangle bounds = label.getBounds();

          // width exceeded?
          while (metrics.charsWidth(chars, start, length) > bounds.width) {

            // seek word break (single whitespace)
            int wordBreak = name.lastIndexOf(' ', start + length - 1);
            if (length > 1) {
              // seek intra word break (hyphen)
              wordBreak = Math.max(wordBreak, name.lastIndexOf('-', start + length - 2) + 1);
            }

            // wordBreak before start?
            if (wordBreak <= start) {
              // decrease length until width fits
              while (length > 1 && metrics.charsWidth(chars, start, length) > bounds.width) {
                length--;
              }
            } else {
              // let wordBreak decide length
              length = wordBreak - start;
            }
          }
        }
      }
      
      nameBreaks.add(new String(chars, start, length));
      start = start + length;

      // trim leading whitespace
      while (start < end && chars[start] == ' ') {
        start++;
      }
    }

    return nameBreaks;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }

  public void setPosition(int x, int y) {
    Console console = consolePanel.getConsole();

    console.setLocation(element, x, y);
  }
  
  public int getWidth() {
    return size.width;
  }
  
  public int getHeight() {
    return size.height;
  }
  
  protected abstract Dimension getNonStyleSize();
    
  protected Dimension getStyleSize() {

    int width  = 0;
    int height = 0;

    for (int s = 0; s < style.getStateCount(); s++) {
      State state = style.getState(s);
      
      jorgan.skin.Image image = state.getImage();
      if (image != null) {
        java.awt.Point location = image.getLocation();
        java.awt.Image img      = SkinManager.instance().getImage(style.getSkin(), image); 
        if (img != null) {
          width  = Math.max(width , location.x + img.getWidth (null));
          height = Math.max(height, location.y + img.getHeight(null));
        }
      }

      jorgan.skin.Label label = state.getLabel();
      if (label != null) {
        java.awt.Rectangle bounds = label.getBounds();

        width  = Math.max(width , bounds.x + bounds.width);
        height = Math.max(height, bounds.y + bounds.height);
      }

      jorgan.skin.Mouse mouse = state.getMouse();
      if (mouse != null) {
        java.awt.Rectangle bounds = mouse.getBounds();

        width  = Math.max(width , bounds.x + bounds.width);
        height = Math.max(height, bounds.y + bounds.height);
      }
    }

    return new Dimension(width, height);
  }
 
  /**
   * Paint this view.
   * 
   * @param graphics    graphics to paint on
   */
  public void paint(Graphics2D g) {
    if (style == null) {
      paintNonStyle(g);
    } else {
      paintStyle(g);
    }
  }
  
  protected boolean isStyled() {
    return style != null;
  }
  
  protected abstract void paintNonStyle(Graphics2D g);

  protected void paintStyle(Graphics2D g) {
    jorgan.disposition.Element element = getElement();
    
    State state = style.getState(getStateIndex());
    jorgan.skin.Image image = state.getImage();
    if (image != null) {
      Point location = image.getLocation();
      Image img      = SkinManager.instance().getImage(style.getSkin(), image); 
      if (img != null) {
        g.drawImage(img, location.x, location.y, null);
      }
    }
    
    Label label = state.getLabel();
    if (label != null) {
      Rectangle bounds      = label.getBounds();
      int       rotation    = label.getRotation();
      boolean   antialiased = label.isAntialiased();

      Object wasAntialiased = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);     
      if (antialiased) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }   

      AffineTransform transform = g.getTransform();
      g.rotate(rotation * Math.PI*2 / 360, getX() + bounds.x + bounds.width/2,
                                           getY() + bounds.y + bounds.height/2);

      g.setColor(label.getColor());
      g.setFont(label.getFont());
      paintName(g, bounds.x, bounds.y, bounds.width, bounds.height);

      g.setTransform(transform);

      if (antialiased) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, wasAntialiased);   
      }
    }
    
    if (Configuration.instance().getShowShortcut() && element instanceof Active) { 
      Shortcut shortcut = ((Active)element).getShortcut();

      if (shortcut != null) {
        Color color = Configuration.instance().getShortcutColor(); 
        Font  font  = Configuration.instance().getShortcutFont(); 
        g.setColor(color);
        g.setFont (font);
        
        String string = shortcut.toString(); 
        g.drawString(string, 0, 0 + font.getSize());
      }
    }
  }

  protected abstract Font getNonStyleFont();

  /**
   * Get the index of the currently state.
   * 
   * @return  state index
   */  
  protected int getStateIndex() {
    return 0;
  }
  
  public boolean isPressable(int x, int y, MouseEvent ev) {
    if (style == null) {
      return isNonStylePressable(x, y, ev);
    } else {
      return isStylePressable(x, y, ev);
    }
  }
  
  protected boolean isNonStylePressable(int x, int y, MouseEvent ev) {
    return false; 
  }
  
  protected boolean isStylePressable(int x, int y, MouseEvent ev) {
    if (style == null) {
      return false;
    }
    
    Mouse mouse = style.getState(getStateIndex()).getMouse();
    if (mouse == null) {
      return false;
    } else {
      Rectangle bounds = mouse.getBounds(); 
      return bounds.x < x && (bounds.x + bounds.width ) > x &&
             bounds.y < y && (bounds.y + bounds.height) > y;
    }
  }
  
  public void pressed(int x, int y, MouseEvent ev) {
  }
  
  public void dragged(int x, int y, MouseEvent ev) {
  }

  public void released(int x, int y, MouseEvent ev) {    
  }
  
  public void pressed(KeyEvent ev) {
  }
  
  /**
   * Get the size.
   * 
   * @return
   */
  protected Dimension getNameSize() {
    int width  = 0;
    int height = 0;
    
    Font font = null;
    if (style == null) {
       font = getNonStyleFont();
    } else {
      Label label = style.getState(getStateIndex()).getLabel();
      if (label != null) {
        font = label.getFont();
      }
    }
    
    if (font != null) {
      FontMetrics metrics = getViewPanel().getFontMetrics(font);

      for (int b = 0; b < nameBreaks.size(); b++) {
        String nameBreak = (String)nameBreaks.get(b);
       
        width = Math.max(width, metrics.stringWidth(nameBreak));
      
        if (b > 0) {
          height += metrics.getLeading();
        }
        height += metrics.getAscent() + metrics.getDescent();
      }
    }

    return new Dimension(width, height);
  } 

  /**
   * Paint text.
   * 
   * @param g   graphics to paint on
   */
  protected void paintName(Graphics2D g, int x, int y, int width, int height) {

    Dimension size = getNameSize();
    
    FontMetrics metrics = g.getFontMetrics();

    y += height/2 - size.height/2;
    x += width/2;
    for (int b = 0; b < nameBreaks.size(); b++) {
      String nameBreak = (String)nameBreaks.get(b);
      
      y += metrics.getAscent();
      
      g.drawString(nameBreak, x - metrics.stringWidth(nameBreak)/2, y);

      y += metrics.getDescent() + metrics.getLeading();
    }    
  }
}