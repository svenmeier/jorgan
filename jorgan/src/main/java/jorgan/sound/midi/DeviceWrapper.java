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
package jorgan.sound.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * A wrapper of a device.
 */
public abstract class DeviceWrapper implements MidiDevice {

    private MidiDevice device;

    public DeviceWrapper(MidiDevice device) {
        this.device = device;
    }

    public int getMaxReceivers() {
        return device.getMaxReceivers();
    }

    public int getMaxTransmitters() {
        return device.getMaxTransmitters();
    }

    public long getMicrosecondPosition() {
        return device.getMicrosecondPosition();
    }

    public void close() {
        device.close();
    }

    public void open() throws MidiUnavailableException {
        device.open();
    }

    public boolean isOpen() {
        return device.isOpen();
    }

    public Info getDeviceInfo() {
        return device.getDeviceInfo();
    }

    public Receiver getReceiver() throws MidiUnavailableException {
        return device.getReceiver();
    }

    public Transmitter getTransmitter() throws MidiUnavailableException {
        return device.getTransmitter();
    }
    
//    public List getReceivers() {
//        return device.getReceivers();
//    }
    
//    public List getTransmitters() {
//        return device.getTransmitters();
//    }
}
