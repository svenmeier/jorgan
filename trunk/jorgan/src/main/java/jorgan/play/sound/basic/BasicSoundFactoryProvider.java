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
package jorgan.play.sound.basic;

// jorgan
import javax.sound.midi.MidiUnavailableException;

import jorgan.play.sound.SoundFactory;
import jorgan.play.sound.spi.SoundFactoryProvider;
import jorgan.sound.midi.ChannelPool;

/**
 * A provider of default sound factories.
 */
public class BasicSoundFactoryProvider implements SoundFactoryProvider {

  /**
   * Get the type of this provider.
   * 
   * @return    the type
   */
  public String getType() {
    return null;
  }

  /**
   * Create a factory.
   */
  public SoundFactory createSoundFactory(String deviceName) throws MidiUnavailableException {

    ChannelPool pool = ChannelPool.instance(deviceName);
      
    return createSoundFactory(pool);
  }
  
  protected SoundFactory createSoundFactory(ChannelPool channelPool) throws MidiUnavailableException {
    return new BasicSoundFactory(channelPool);      
  }
}