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
package jorgan.disposition;

/**
 * A console.
 */
public class Console extends Element {
  
  public static final double MAX_ZOOM = 2.0d;
  
  public static final double MIN_ZOOM = 0.5d;
  
  public static final String DEFAULT_SCREEN = "";

  /**
   * The device for input.
   */
  private String device;
  
  /**
   * The skin.
   */
  private String skin;
  
  /**
   * The zoom.
   */
  private double zoom = 1.0d;
  
  private String screen;
  
  protected boolean canReference(Class clazz) {
    return Element.class.isAssignableFrom(clazz) && Console.class != clazz;  
  }

  protected Reference createReference(Element element) {
    return new ConsoleReference(element);
  }
  
  public String getDevice() {
    return device;
  }

  public String getSkin() {
    return skin;
  }

  public double getZoom() {
    return zoom;
  }

  public String getScreen() {
    return screen;
  }

  public void setDevice(String device) {
    this.device = device;

    fireElementChanged(true);
  }

  public void setSkin(String skin) {
    this.skin = skin;

    fireElementChanged(true);
  }

  public void setZoom(double zoom) {
    if (zoom < MIN_ZOOM) {
      zoom = MIN_ZOOM;
    }
    if (zoom > MAX_ZOOM) {
      zoom = MAX_ZOOM;
    }

    this.zoom = zoom;

    fireElementChanged(true);
  }

  public void setScreen(String screen) {
    this.screen = screen;

    fireElementChanged(true);
  }

  public void setLocation(Element element, int x, int y) {
    ConsoleReference reference = (ConsoleReference)getReference(element);
    
    reference.setX(x);
    reference.setY(y);
    
    fireReferenceChanged(reference, true);
  }
}