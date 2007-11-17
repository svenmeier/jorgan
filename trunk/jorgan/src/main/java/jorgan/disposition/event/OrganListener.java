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
package jorgan.disposition.event;

/**
 * A listener to organ changes.
 */
public interface OrganListener {

  /**
   * An element was changed.
   *
   * @param event   event describing the change
   */
  public void elementChanged(OrganEvent event);

  /**
   * An element was added to the organ.
   *
   * @param event   event describing the change
   */
  public void elementAdded(OrganEvent event);

  /**
   * An element was removed from the organ.
   *
   * @param event   event describing the change
   */
  public void elementRemoved(OrganEvent event);
}