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
package jorgan.play.sound;

import java.util.*;
import javax.sound.midi.*;

import sun.misc.Service;

import jorgan.play.sound.spi.SoundFactoryProvider;

/**
 * An factory of sounds.
 */
public abstract class SoundFactory {

    public static final int UNUSED_DATA               = 0;
    public static final int CONTROL_BANK_SELECT_MSB   = 0;
    public static final int CONTROL_MODULATION        = 1;
    public static final int CONTROL_VOLUME            = 7;
    public static final int CONTROL_PAN               = 10;
    public static final int CONTROL_GENERAL_PURPOSE_1 = 16;
    public static final int CONTROL_GENERAL_PURPOSE_2 = 17;
    public static final int CONTROL_GENERAL_PURPOSE_3 = 18;
    public static final int CONTROL_GENERAL_PURPOSE_4 = 19;
    public static final int CONTROL_BANK_SELECT_LSB   = 32;
    public static final int CONTROL_BRIGHTNESS        = 74;
    public static final int CONTROL_REVERB            = 91;
    public static final int CONTROL_CHORUS            = 93;
    public static final int CONTROL_RESET_ALL         = 121;
    public static final int CONTROL_ALL_NOTES_OFF     = 123;

    public static final int CONTROL_NRPN_MSB      = 99;
    public static final int CONTROL_NRPN_LSB      = 98;
    public static final int CONTROL_NRPN_MSB_DATA = 6;
    public static final int CONTROL_NRPN_LSB_DATA = 38;

  protected List sounds = new ArrayList();
  
  protected int bank;
  protected String samples;
  
  /**
   * Use {@link #instance(String)}.
   */
  protected SoundFactory() {
  }
  
  /**
   * Set the bank of this soundFactory.
   * 
   * @param bank      bank
   */
  public void setBank(int bank) {
  	this.bank = bank;
  }

  /**
   * Set the samples of this soundFactory.
   * 
   * @param samples		samples
   */
  public void setSamples(String samples) {
  	this.samples = samples;
  }

  /**
   * Create a sound.
   *
   * @return         created sound or <code>null</code> if no further sound
   *                 is available
   */
  public abstract Sound createSound();

  /**
   * Close this factory.
   */
  public abstract void close();

  /**
   * Abstract implementation of a sound.
   */
  public abstract class AbstractSound implements Sound {

    private int[] pitches = new int[128];

    /**
     * Create a new sound.
     */
    public AbstractSound() {
      
      sounds.add(this);
    }

    public void noteOff(int pitch) {
        
      pitches[pitch]--;

      if (pitches[pitch] == 0) {
        noteOffImpl(pitch);
      }
    }

	protected abstract void noteOffImpl(int pitch);

	public void noteOn(int pitch, int velocity) {
      if (pitches[pitch] == 0) {
        noteOnImpl(pitch, velocity);
      }
            
      pitches[pitch]++;
    }
    
	protected abstract void noteOnImpl(int pitch, int velocity);

	/**
     * Stop this sound.
     */
    public void stop() {

      sounds.remove(this);
    }
  }
  
  /**
   * Get the supported factory types.
   *
   * @return  supported types
   */
  public static String[] getFactoryTypes() {

    Iterator providers = getProviders();
    
    List types = new ArrayList();
    while (providers.hasNext()) {
      SoundFactoryProvider provider = (SoundFactoryProvider)providers.next();
      String type = provider.getType();
      if (type != null) {
        types.add(type);
      }
    }

    return (String[])types.toArray(new String[types.size()]);
  }

  /**
   * Get the instance for a MIDI device.
   *
   * @param deviceName    the name of device to get a factory for
   * @param type          type of device
   * @return              sound factory
   * @throws MidiUnavailableException if device is not available
   */
  public static SoundFactory instance(String deviceName, String type) throws MidiUnavailableException {
    
    SoundFactory factory = getProvider(type).createSoundFactory(deviceName);

    return factory;
  }

  private static SoundFactoryProvider getProvider(String type) {

    Iterator providers = getProviders();
    
    while (providers.hasNext()) {
      SoundFactoryProvider provider = (SoundFactoryProvider)providers.next();
      if (type == null && provider.getType() == null ||
          type != null && type.equals(provider.getType())) {
        return provider;
      }
    }
    throw new IllegalArgumentException(type);
  }

  private static Iterator getProviders() {
    return Service.providers(SoundFactoryProvider.class);
  }
}