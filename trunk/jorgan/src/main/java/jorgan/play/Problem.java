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
package jorgan.play;

/**
 * Problem of a player.
 */
public class Problem {
  
  public static final String WARNING = "warning";
  public static final String ERROR   = "error";

  private String  level;
  private String  property;
  private Object  value;

  public Problem(String level, String property) {

    this.level    = level;
    this.property = property;
  }

  public String getLevel() {
    return level;
  }

  public String getProperty() {
    return property;
  }

  public Object getValue() {
    return value;
  }
  
  public Problem value(Object value) {
    this.value = value;
    
    return this;
  }
    
  public boolean equals(Object object) {
    if (object == null || !(object instanceof Problem)) {
      return false;
    }
    
    Problem problem = (Problem)object;
    
    if (this.level != problem.level) {
      return false;
    }
    if (!this.property.equals(problem.property)) {
      return false;
    }
    
    return true;
  }

  public int hashCode() {
    return toString().hashCode();
  }
  
  public String toString() {
    return level + "." + property;
  }
}