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
package jorgan.riff;

/**
 * Basic representation of a chunk.
 */
public class Chunk {
 
  /**
   * The id of the chunk.
   */ 
  private String id;
  
  /**
   * The length of the data contained in this chunk.
   */
  private int dataLength;
  
  /**
   * Create a chunk for the given id and data length.
   * 
   * @param id         id of chunk
   * @param dataLength length of data in this chunk
   */
  public Chunk(String id, int dataLength) {
    this.id     = id;
    this.dataLength = dataLength;
  }
  
  /**
   * Get the id.
   * 
   * @return  id
   */
  public String getId() {
    return id;
  }
  
  /**
   * Get the length of data contained in this chunk.
   * 
   * @return length of data
   */
  public int getDataLength() {
    return dataLength;  
  }
  
  /**
   * Get the size.
   * 
   * @return  size
   */
  public int getSize() {
    return 4 + 4 + dataLength;
  }
}
