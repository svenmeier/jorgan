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
 * A pool of channels.
 */
public class ChannelPool {

  private static Map pools = new HashMap();

  /**
   * Name of device.
   */
  private String deviceName;

  /**
   * Device to use.
   */
  private MidiDevice device;

  /**
   * Receiver to use by this factory.
   */
  private Receiver receiver;

  /**
   * Count of openings.
   */
  private int opened = 0;
  
  /**
   * Created channels.
   */
  private ChannelImpl[] channels = new ChannelImpl[16];

  /**
   * Use {@link #instance(String)}.
   */
  protected ChannelPool(String deviceName) throws MidiUnavailableException {

    this.deviceName = deviceName;
    this.device     = PooledDevice.getMidiDevice(deviceName, true);

    pools.put(deviceName, this);
  }

  public String getDeviceName() {
      return deviceName;
  }
  
  /**
   * Create a channel.
   * 
   * @return         created channel or <code>null</code> if no channel is available
   */
  public Channel createChannel(int[] blocked) {

    if (opened == 0) {
        throw new IllegalStateException("not opened");
    }
    
    CHANNELS:
    for (int c = 0; c < channels.length; c++) {
      if (channels[c] == null) {
        for (int b = 0; b < blocked.length; b++) {
          if (blocked[b] == c) {
            continue CHANNELS;
          }
        }
        return new ChannelImpl(c);
      }
    }

    return null;
  }

  /**
   * Open this pool of channels.
   * <br>
   * Opens the MIDI device on first call.
   *
   * @throws MidiUnavailableException if device is not available
   */
  public void open() throws MidiUnavailableException {
    if (opened == 0) {
      device.open();

      receiver = device.getReceiver();
    }
    opened++;
  }

  /**
   * Close this pool of channels.
   */
  public void close() {
    opened--;

    if (opened == 0) {
      receiver.close();
      receiver = null;

      device.close();

      pools.remove(deviceName);
    }
  }

  /**
   * A channel implementation.
   */
  private class ChannelImpl implements Channel {

    /**
     * The MIDI channel of this sound.
     */
    private int channel;

    /**
     * Create a channel.
     *
     * @param channel   the channel to use
     */
    public ChannelImpl(int channel) {
      this.channel = channel;

      channels[channel] = this;
    }

    /**
     * Release.
     */
    public void release() {

      channels[channel] = null;
    }

    /**
     * Convenience method to send a MIDI message.
     *
     * @param command   command
     * @param data1     data1
     * @param data2     data2
     */
    public void sendMessage(int command, int data1, int data2) {
      if (receiver != null) {
        try {
          ShortMessage message = new ShortMessage();
          message.setMessage(command, channel, data1, data2);
          receiver.send(message, -1);
        } catch (InvalidMidiDataException ex) {
          throw new RuntimeException("unexpected invalid midi data", ex);
        }
      }
    }
  }

  /**
   * Get the instance for a MIDI device.
   *
   * @param deviceName    the name of device to get a pool for
   * @return              channel pool
   * @throws MidiUnavailableException if device is not available
   */
  public static ChannelPool instance(String deviceName) throws MidiUnavailableException {
    ChannelPool pool = (ChannelPool)pools.get(deviceName);
    if (pool == null) {
      pool = new ChannelPool(deviceName);
    }

    return pool;
  }  
}