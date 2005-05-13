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

import javax.sound.midi.*;

import jorgan.sound.midi.Channel;
import jorgan.sound.midi.ChannelPool;

import jorgan.midi.Configuration;

import jorgan.play.sound.SoundFactory;
import jorgan.play.sound.Sound;

/**
 * An abstract base class for sound factories that are based on channels.
 */
public abstract class ChanneledSoundFactory extends SoundFactory {

  /**
   * The pool of channels used by this factory.
   */
  protected ChannelPool pool;
  
  public ChanneledSoundFactory(ChannelPool pool) throws MidiUnavailableException {
    if (pool == null) {
        throw new IllegalArgumentException("pool must not be null");
    }
      
    this.pool = pool;
      
    pool.open();
  }
  
  protected int[] getBlockedChannels() {
    return new int[0];
  }

  /**
   * Factory method.
   *
   * @return         sound
   */
  public Sound createSound() {
    Channel channel = pool.createChannel(getBlockedChannels());

    if (channel != null) {
      return createSoundImpl(channel);
    } else {
      return null;
    }
  }

  /**
   * Factory method.
   *
   * @param channel  channel to use for sound
   * @return         sound
   */
  protected abstract Sound createSoundImpl(Channel channel);

  /**
   * Close this factory.
   */
  public void close() {
    pool.close();
  }

  /**
   * Sound on a channel.
   */
  public abstract class ChannelSound extends AbstractSound {

    /**
     * The MIDI channel of this sound.
     */
    private Channel channel;
    
    /**
     * Create a new sound.
     *
     * @return         sound
     */
    public ChannelSound(Channel channel) {

      if (channel == null) {
          throw new IllegalArgumentException("channel must not be null");
      }
      this.channel = channel;      
    }

    protected void sendMessage(int command, int data1, int data2) {
        if (channel == null) {
            throw new IllegalStateException("already stopped");
        }
        channel.sendMessage(command, data1, data2);
    }    

    /**
     * Stop this sound.
     */
    public void stop() {
        if (channel == null) {
            throw new IllegalStateException("already stopped");
        }

        if (Configuration.instance().getSendAllNotesOff()) {
          sendMessage(ShortMessage.CONTROL_CHANGE, CONTROL_ALL_NOTES_OFF, UNUSED_DATA);
        }

        channel.release();
        channel = null;

        super.stop();
    }
  }
}