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

import java.util.*;
import javax.sound.midi.*;

/**
 * Helper for lookup of devices.
 */
public class DevicePool {

  private static Map sharedDevices = new HashMap();
  
  private DevicePool() {
  }
      
  /**
   * Get a device by name.
   *
   * @param name    name of device to get
   * @param out     if <code>true</code> device should support midi-out, otherwise
   *                it should support midi-in.
   * @return        the named device
   * @throws MidiUnavailableException
   */
  public static MidiDevice getMidiDevice(String name, boolean out) throws MidiUnavailableException {

    DeviceKey key = new DeviceKey(name, out);
    
    SharedDevice sharedDevice = (SharedDevice)sharedDevices.get(key);
    if (sharedDevice == null) {
        MidiDevice[] devices = getMidiDevices(out);

        for (int d = 0; d < devices.length; d++) {
          if (name.equals(devices[d].getDeviceInfo().getName())) {
            sharedDevice = new SharedDevice(devices[d]);

            sharedDevices.put(key, sharedDevice);
            
            break;
          }
        }
    }    

    if (sharedDevice == null) {
        throw new MidiUnavailableException(name);
    }

    return new ProxyDevice(sharedDevice);
  }

  /**
   * Get all devices that support midi-out or midi-in.
   *
   * @param out     if <code>true</code> devices should support midi-out, otherwise
   *                they should support midi-in.
   * @return        list of devices
   * @throws MidiUnavailableException
   */
  private static MidiDevice[] getMidiDevices(boolean out) throws MidiUnavailableException {
    MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

    List devices = new ArrayList();

    for (int i = 0; i < infos.length; i++) {
      MidiDevice.Info info = infos[i];

      MidiDevice device = MidiSystem.getMidiDevice(info);
      if (out  && device.getMaxReceivers()    != 0 ||
          !out && device.getMaxTransmitters() != 0) {
        devices.add(device);
      }
    }

    return (MidiDevice[])devices.toArray(new MidiDevice[0]);
  }

  /**
   * Get the name of all devices that support midi-out or midi-in.
   *
   * @param out     if <code>true</code> devices should support midi-out, otherwise
   *                they should support midi-in.
   * @return        list of device names
   * @throws MidiUnavailableException
   */
  public static String[] getMidiDeviceNames(boolean out) {

    String[] names = null;
    try {
      MidiDevice[] devices = getMidiDevices(out);

      names = new String[devices.length];

      for (int d = 0; d < devices.length; d++) {
        names[d] = devices[d].getDeviceInfo().getName();
      }
    } catch (MidiUnavailableException ex) {
      names = new String[0];
    }

    return names;
  }

  private static class ProxyDevice extends DeviceWrapper {
    private boolean open = false;

    private ProxyDevice(MidiDevice device) {
        super(device);
    }

    public void close() {
        assertOpen();
       
        super.close();

        open = false;
    }

    public void open() throws MidiUnavailableException {
        assertClosed();

        super.open();
        
        open = true;
    }

    public boolean isOpen() {
        return open;
    }

    public Receiver getReceiver() throws MidiUnavailableException {
        assertOpen();
        return super.getReceiver();
    }

    public Transmitter getTransmitter() throws MidiUnavailableException {
        assertOpen();
        return super.getTransmitter();
    }

    protected void assertOpen() throws IllegalStateException {
        if (!open) {
            throw new IllegalStateException("not open");
        }
    }

    protected void assertClosed() throws IllegalStateException {
        if (open) {
            throw new IllegalStateException("open");
        }
    }
}

private static class DeviceKey {

    private String deviceName;
    private boolean out;
     
    public DeviceKey(String deviceName, boolean out) {
        this.deviceName = deviceName;
        this.out        = out;  
    }
    
    public boolean equals(Object obj) {

        if (!(obj instanceof DeviceKey)) {
            return false;
        }
        
        DeviceKey key = (DeviceKey)obj;
        
        return key.deviceName.equals(deviceName) &&
               key.out == out;
    }

    public int hashCode() {
        return deviceName.hashCode();
    }
  }
  
  private static class SharedDevice extends DeviceWrapper {
    private int openCount;
 
    public SharedDevice(MidiDevice device) {
      super(device);
    }
    
    public void open() throws MidiUnavailableException {
      if (openCount == 0) {
        super.open();
      }
      openCount++;
    }

    public void close() {
      openCount--;
              
      if (openCount == 0) {
        super.close();
      }
    }
  }
}