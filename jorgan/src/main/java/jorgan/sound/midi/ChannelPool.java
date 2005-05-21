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
public abstract class ChannelPool {

  private static Map sharedPools = new HashMap();

  public abstract String getDeviceName();
  
  /**
   * Open this pool of channels.
   * <br>
   * Opens the MIDI device on first call.
   *
   * @throws MidiUnavailableException if device is not available
   */
  public abstract void open() throws MidiUnavailableException;

  /**
   * Create a channel.
   * 
   * @return         created channel or <code>null</code> if no channel is available
   */
  public abstract Channel createChannel(int[] blocked);

  /**
   * Close this pool of channels.
   */
  public abstract void close();

  /**
   * Get the instance for a MIDI device.
   *
   * @param deviceName    the name of device to get a pool for
   * @return              channel pool
   * @throws MidiUnavailableException if device is not available
   */
  public static ChannelPool instance(String deviceName) throws MidiUnavailableException {
    SharedChannelPool pool = (SharedChannelPool)sharedPools.get(deviceName);
    if (pool == null) {
      pool = new SharedChannelPool(deviceName);

      sharedPools.put(deviceName, pool);
    }

    return new ProxyChannelPool(pool);
  }  

  private static class SharedChannelPool extends ChannelPool {
      
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
      protected SharedChannelPool(String deviceName) throws MidiUnavailableException {

        this.deviceName = deviceName;
        
        this.device = DevicePool.getMidiDevice(deviceName, true);
      }

      public String getDeviceName() {
        return deviceName;
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
       * Close this pool of channels.
       */
      public void close() {
        opened--;

        if (opened == 0) {
          receiver.close();
          receiver = null;
          
          for (int c = 0; c < channels.length; c++) {
              channels[c] = null;
          }

          device.close();
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

        public int getNumber() {
          return channel + 1;
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
  }
  
  private static class ProxyChannelPool extends ChannelPool {
      private boolean open = false;
      
      private SharedChannelPool pool;

      protected ProxyChannelPool(SharedChannelPool pool) {

        this.pool = pool;
      }

      public String getDeviceName() {
        return pool.getDeviceName();
      }

      public void open() throws MidiUnavailableException {
        assertClosed();
        
        pool.open();
        
        open = true;
      }

      /**
       * Create a channel.
       * 
       * @return         created channel or <code>null</code> if no channel is available
       */
      public Channel createChannel(int[] blocked) {
        assertOpen();
        
        return pool.createChannel(blocked);
      }

      /**
       * Close this pool of channels.
       */
      public void close() {
        assertOpen();
        
        pool.close();
        
        open = false;
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
}