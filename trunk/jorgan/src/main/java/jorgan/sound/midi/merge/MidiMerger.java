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
package jorgan.sound.midi.merge;

import java.util.*;

import javax.sound.midi.*;

import jorgan.sound.midi.PooledDevice;
import jorgan.sound.midi.Loopback;


/**
 * <code>MidiDevice</code> for merging of mutiple other devices. 
 */
public class MidiMerger extends Loopback {
  
  /**
   * The list of inputs to merge.
   */
  private List mergeInputs = new ArrayList();
  
  /**
   * Create a new midiMerger.
   * 
   * @param info        info to use
   */
  public MidiMerger(MidiDevice.Info info) {
    super(info, false, true);
  }
  
  /**
   * Set the inputs to merge.
   * <br>
   * This change has immediate effect only If this midiMerger is not currently
   * open, otherwise it is delayed until the next opening. 
   * 
   * @param mergeInputs the inputs to merge
   */
  public void setMergeInputs(List mergeInputs) {
    if (mergeInputs == null) {
      throw new IllegalArgumentException("mergeInputs must not be null");
    }

    this.mergeInputs = mergeInputs;
  }

  /**
   * Overriden to create receivers for all devices to merge. 
   */
  public void open() throws MidiUnavailableException {
    super.open();

    try {
      for (int i = 0; i < mergeInputs.size(); i++) {
        MergeInput input = (MergeInput)mergeInputs.get(i);
        
        receivers.add(new MergeReceiver(input.getDevice(), input.getChannel()));
      }
    } catch (MidiUnavailableException ex) {
      close();
        
      throw ex;
    }
  }

  /**
   * One receiver used for each input device.
   */
  private class MergeReceiver implements Receiver {

    /**
     * The input device to receive messages from.
     */
    private MidiDevice device;
    
    /**
     * The transmitter of the input device.
     */
    private Transmitter transmitter;
    
    /**
     * The channel to map message to or <code>-1</code> if no
     * mapping should be performed.
     */
    private int channel;
    
    /**
     * Create a new receiver for the given input.
     * 
     * @param  device  name of device to create receiver for
     * @param  channel channel to map messages to
     * @throws MidiUnavailableException if input device is unavailable
     */
    public MergeReceiver(String device, int channel) throws MidiUnavailableException {
  
      this.device = PooledDevice.getMidiDevice(device, false);
      this.device.open();
      
      transmitter = this.device.getTransmitter();
      transmitter.setReceiver(this);
    
      this.channel = channel;
    }
    
    /**
     * Send messages are optionally mapped before loopbacked.
     */
    public void send(MidiMessage message, long timestamp) {
      if (isOpen()) {
        if (message instanceof ShortMessage) {
          message = mapChannel((ShortMessage)message);
        }

        loopbackReceiver.send(message, timestamp);
      }
    }
    
    /**
     * Map the channel of the given message.
     * 
     * @param message   message to map channel
     * @return          new message with mapped channel
     */
    private MidiMessage mapChannel(ShortMessage message) {

      int command = message.getCommand();
      int data1   = message.getData1();
      int data2   = message.getData2();

      if (command < 0xF0 && channel != -1) {
        try {
          ShortMessage mapped = new ShortMessage();
          mapped.setMessage(command, channel, data1, data2);
          message = mapped;
        } catch (InvalidMidiDataException ex) {
          throw new Error("unexpected invalid data in MidiMerger channel mapping");
        }
      }
      return message;
    }

    /**
     * Closing this receiver also closes the device listened to.
     */
    public void close() {
      transmitter.close();
      device.close();
    }
  }
}