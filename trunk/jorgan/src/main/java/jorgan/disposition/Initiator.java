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

import java.util.Iterator;

public abstract class Initiator extends Momentary {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;

        fireElementChanged(true);
    }

    public abstract void initiate();
    
    protected void notifyObservers() {
        Iterator observers = referrer(Observer.class).iterator();
        while (observers.hasNext()) {
            Observer observer = (Observer) observers.next();
            observer.initiated(this);
        }
    }
    
    public static interface Observer {
        public void initiated(Initiator initiator);
    }
}