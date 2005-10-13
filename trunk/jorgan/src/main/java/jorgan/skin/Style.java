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
package jorgan.skin;

import java.util.*;

/**
 * Style.
 */
public class Style {

  public static final int DRAG_NONE  = 0;
  public static final int DRAG_LEFT_TO_RIGHT = 1;
  public static final int DRAG_RIGHT_TO_LEFT = 2;
  public static final int DRAG_TOP_TO_BOTTOM = 3;
  public static final int DRAG_BOTTOM_TO_TOP = 4;
  
  private Skin   skin;  
  private String name        = "";
  private String description = null;
  private int    drag        = DRAG_NONE;
    
  private ArrayList states = new ArrayList();

  public Style() {
  }
  
  public Skin getSkin() {
    return skin;
  }

  public void setSkin(Skin skin) {
    this.skin = skin;
  }

  public void setName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    this.name = name;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public void setDrag(int movement) {
      this.drag = movement;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public int getDrag() {
      return drag;
  }
  
  public int getStateCount() {
    return states.size();
  }
  
  public State getState(int index) {
    return (State)states.get(index);
  }
  
  public void addState(State state) {
    states.add(state);
  }  

  public void removeState(State state) {
    states.remove(state);
  }  
}