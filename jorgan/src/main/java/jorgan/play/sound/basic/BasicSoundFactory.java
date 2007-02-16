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

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import jorgan.play.sound.ChanneledSoundFactory;
import jorgan.play.sound.Sound;
import jorgan.sound.midi.Channel;
import jorgan.sound.midi.ChannelPool;

/**
 * An implementation of a sound factory that should work for most MIDI devices.
 */
public class BasicSoundFactory extends ChanneledSoundFactory {

	public BasicSoundFactory(ChannelPool pool) throws MidiUnavailableException {
		super(pool);
	}

	/**
	 * Factory method.
	 * 
	 * @param channel
	 *            channel to use for sound
	 * @return sound
	 */
	protected Sound createSoundImpl(Channel channel) {
		return new BasicSound(channel);
	}

	/**
	 * Basic implementation of a sound.
	 */
	public class BasicSound extends ChannelSound {

		/**
		 * Create a new sound.
		 */
		public BasicSound(Channel channel) {

			super(channel);

			sendMessage(ShortMessage.CONTROL_CHANGE, CONTROL_RESET_ALL,
					UNUSED_DATA);

			sendMessage(ShortMessage.CONTROL_CHANGE, CONTROL_BANK_SELECT_MSB,
					bank % 128);
		}

		/**
		 * Set the program of the sound.
		 * 
		 * @param program
		 *            program
		 */
		public void setProgram(int program) {
			sendMessage(ShortMessage.PROGRAM_CHANGE, program, UNUSED_DATA);
		}

		/**
		 * Set the pan of the sound.
		 * 
		 * @param pan
		 *            pan to set
		 */
		public void setPan(int pan) {
			sendMessage(ShortMessage.CONTROL_CHANGE, CONTROL_PAN, pan);
		}

		/**
		 * Set the pitch bend of the sound.
		 * 
		 * @param bend
		 *            bend to set
		 */
		public void setPitchBend(int bend) {
			sendMessage(ShortMessage.PITCH_BEND, 0, bend);
		}

		/**
		 * Set the volume of this sound.
		 * 
		 * @param volume
		 *            the volume to set
		 */
		public void setVolume(int volume) {
			sendMessage(ShortMessage.CONTROL_CHANGE, CONTROL_VOLUME, volume);
		}

		/**
		 * Set the cutoff of this sound. <br>
		 * This default implementation uses the brightness control since the
		 * MIDI specification does not define a standard way to set the cutoff.
		 * 
		 * @param cutoff
		 *            the cutoff to set
		 */
		public void setCutoff(int cutoff) {
			sendMessage(ShortMessage.CONTROL_CHANGE, CONTROL_BRIGHTNESS, cutoff);
		}

		protected void noteOnImpl(int pitch, int velocity) {
			sendMessage(ShortMessage.NOTE_ON, pitch, velocity);
		}

		protected void noteOffImpl(int pitch) {
			sendMessage(ShortMessage.NOTE_OFF, pitch, UNUSED_DATA);
		}

		/**
		 * Set the modulation of this sound. <br>
		 * General MIDI does not support setting of the modulation frequency so
		 * this parameter is ignored.
		 * 
		 * @param amplitude
		 *            the amplitude of modulation
		 * @param frequency
		 *            the frequency of modulation
		 */
		public void setModulation(int amplitude, int frequency) {

			sendMessage(ShortMessage.CONTROL_CHANGE, CONTROL_MODULATION,
					amplitude);
		}
	}
}