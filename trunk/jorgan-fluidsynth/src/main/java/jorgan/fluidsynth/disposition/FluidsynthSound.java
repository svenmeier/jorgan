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
	
	// audio.driver [alsa, oss, jack, dsound, sndman, coreaudio, portaudio]
	private String audioDriver;

	// audio.<driver>.device
	private String audioDevice;
	
	// audio.periods [2-64]
	private int audioBuffers = 16;

	// audio.period-size [64-8192]
	private int audioBufferSize = 64;
	
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

	public int getAudioBuffers() {
		return audioBuffers;
	}

	public void setAudioBuffers(int audioBuffers) {
		if (audioBuffers < 2) {
			throw new IllegalArgumentException(
					"audioBuffers must be greater or equal 2");
		}
		if (audioBuffers > 16) {
			throw new IllegalArgumentException("audioBuffers must be less than 16");
		}

		if (this.audioBuffers != audioBuffers) {
			this.audioBuffers = audioBuffers;
			
			fireChange(new PropertyChange());
		}
	}

	public int getAudioBufferSize() {
		return audioBufferSize;
	}

	public void setAudioBufferSize(int audioBufferSize) {
		if (audioBufferSize < 64) {
			throw new IllegalArgumentException(
					"audioBufferSize must be greater or equal 64");
		}
		if (audioBufferSize > 8192) {
			throw new IllegalArgumentException("audioBufferSize must be less than 8192");
		}

		if (this.audioBufferSize != audioBufferSize) {
			this.audioBufferSize = audioBufferSize;

			fireChange(new PropertyChange());
		}
	}

	public String getAudioDevice() {
		return audioDevice;
	}

	public void setAudioDevice(String audioDevice) {
		if (!Null.safeEquals(this.audioDevice, audioDevice)) {
			if ("".equals(audioDevice)) {
				audioDevice = null;
			}
			
			this.audioDevice = audioDevice;
			
			fireChange(new PropertyChange());
		}
	}	
}