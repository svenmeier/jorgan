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
package jorgan.midi.merge;

import javax.sound.midi.*;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.sound.midi.merge.*;

/**
 * The provider of <code>MidiMerger</code> devices.
 * 
 * @see soundx.midi.merge.MidiMerger
 */
public class MidiMergerProvider extends MidiDeviceProvider {

  /**
   * The name of this device.
   */
  public static final String DEVICE_NAME = "jOrgan Midi Merger";

  /**
   * The device info for this providers device.
   */
  private static final Info info = new Info();
  
  /**
   * The device.
   */
  private static MidiMerger midiMerger;

  public MidiDevice.Info[] getDeviceInfo() {

    return new MidiDevice.Info[]{info};
  }
  
  public MidiDevice getDevice(MidiDevice.Info info) {
    if (MidiMergerProvider.info == info) {
      if (midiMerger == null) {
        midiMerger = new MidiMerger(info);         
      }
      if (!midiMerger.isOpen()) {
        midiMerger.setMergeInputs(Configuration.instance().getInputs());
      }
      return midiMerger;
    }

    return null;
  }
  
  /**
   * The info class for this device.
   */
  protected static class Info extends MidiDevice.Info {

    public Info() {
      super(DEVICE_NAME, "jOrgan", "Midi-Merger of jOrgan", "1.0");
    }
  }
}
