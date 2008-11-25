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
package jorgan.fluidsynth.disposition;

import jorgan.disposition.Sound;
import jorgan.util.Null;

public class FluidsynthSound extends Sound {

	private String soundfont;

	private int channels = 32;
	
	private String audioDriver;
	
	private double gain = 0.5d;

	private Reverb reverb;

	private Chorus chorus;

	public void setGain(double gain) {
		this.gain = FluidsynthSound.limit(gain);

		fireChange(new SimplePropertyChange());
	}
	
	public double getGain() {
		return gain;
	}
	
	public void setAudioDriver(String audioDriver) {
		if (!Null.safeEquals(this.audioDriver, audioDriver)) {
			if ("".equals(audioDriver)) {
				audioDriver = null;
			}
			this.audioDriver = audioDriver;
			
			fireChange(new PropertyChange());
		}
	}
	
	public String getAudioDriver() {
		return audioDriver;
	}
	
	public String getSoundfont() {
		return soundfont;
	}

	public void setSoundfont(String soundfont) {
		if (!Null.safeEquals(this.soundfont, soundfont)) {
			this.soundfont = soundfont;

			fireChange(new PropertyChange());
		}
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		if (channels < 16) {
			throw new IllegalArgumentException(
					"channels must be greater or equal 16");
		}
		if (channels > 256) {
			throw new IllegalArgumentException("channels must be less than 256");
		}
		if (this.channels != channels) {
			this.channels = channels;

			fireChange(new PropertyChange());
		}
	}

	public Chorus getChorus() {
		return chorus;
	}

	public void setChorus(Chorus chorus) {
		this.chorus = chorus;
		
		fireChange(new SimplePropertyChange());
	}

	public Reverb getReverb() {
		return reverb;
	}

	public void setReverb(Reverb reverb) {
		this.reverb = reverb;
		
		fireChange(new SimplePropertyChange());
	}
	
	static double limit(double value) {
		if (value > 1.0d) {
			value = 1.0d;
		}
		if (value < 0.0d) {
			value = 0.0d;
		}
		
		return value;
	}	
}