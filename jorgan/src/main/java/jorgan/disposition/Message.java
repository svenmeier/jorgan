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

import java.io.Serializable;

/**
 * A message is an immutable value object.
 */
public class Message implements Serializable {

    public static int WILDCARD_ANY = -1;

    public static int WILDCARD_POSITIVE = -2;

    private int status;

    private int data1;

    private int data2;

    /**
     * Create a message.
     * 
     * @param status
     *            status of message
     * @param data1
     *            data1 of message or {@link #WILDCARD_ANY} or
     *            {@link #WILDCARD_POSITIVE}
     * @param data2
     *            data2 of message or {@link #WILDCARD_ANY} or
     *            {@link #WILDCARD_POSITIVE}
     */
    public Message(int status, int data1, int data2) {
        if (status < 0 || status > 255) {
            throw new IllegalArgumentException("status '" + status + "'");
        }
        if (data1 < -2 || data1 > 127) {
            throw new IllegalArgumentException("data1 '" + data1 + "'");
        }
        if (data2 < -2 || data2 > 127) {
            throw new IllegalArgumentException("data2 '" + data2 + "'");
        }

        this.status = status;
        this.data1 = data1;
        this.data2 = data2;
    }

    public int getStatus() {
        return status;
    }

    public int getData1() {
        return data1;
    }

    public int getData2() {
        return data2;
    }

    public boolean hasWildcard() {
        return isWildcard(data1) || isWildcard(data2);
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof Message)) {
            return false;
        }
        Message message = (Message) object;

        return (this.status == message.status && this.data1 == message.data1 && this.data2 == message.data2);
    }

    public int hashCode() {
        return (status + data1 + data2);
    }

    public int wildcard(int data1, int data2) {
        if (isWildcard(this.data1)) {
            return data1;
        }
        if (isWildcard(this.data2)) {
            return data2;
        }

        return -1;
    }

    public boolean match(int status, int data1, int data2) {
        return (this.status == status && matchData(this.data1, data1) && matchData(
                this.data2, data2));
    }

    private boolean matchData(int myData, int data) {
        return myData == WILDCARD_ANY
                || (myData == WILDCARD_POSITIVE && data > 0)
                || (myData == data);
    }
    
    private boolean isWildcard(int data) {
        return data == WILDCARD_ANY || data == WILDCARD_POSITIVE;
    }
}