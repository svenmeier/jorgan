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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jorgan.disposition.Sound;
import jorgan.disposition.event.AbstractChange;
import jorgan.disposition.event.OrganListener;
import jorgan.util.Null;

/**
 */
public class FluidsynthSound extends Sound {

	public static final String TUNINGS = "tunings";

	private String soundfont;

	private int channels = 32;

	private int polyphony = 256;

	private int sampleRate = 44100;

	// audio.driver [alsa, oss, jack, dsound, sndman, coreaudio, portaudio]
	private String audioDriver;

	// audio.<driver>.device
	private String audioDevice;

	// audio.periods [2-64]
	private int audioBuffers = 8;

	// audio.period-size [64-8192]
	private int audioBufferSize = 512;

	private double gain = 0.5d;

	private Reverb reverb;

	private Chorus chorus;

	private List<Tuning> tunings = new ArrayList<Tuning>();

	public void setGain(double gain) {
		this.gain = FluidsynthSound.limit(gain);

		fireChange(new FastPropertyChange("gain", false));
	}

	public double getGain() {
		return gain;
	}

	public void setAudioDriver(String audioDriver) {
		if (!Null.safeEquals(this.audioDriver, audioDriver)) {
			String oldAudioDriver = this.audioDriver;

			if ("".equals(audioDriver)) {
				audioDriver = null;
			}
			this.audioDriver = audioDriver;

			fireChange(new PropertyChange(oldAudioDriver, this.audioDriver));
		}
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		if (this.sampleRate != sampleRate) {
			int oldSampleRate = this.sampleRate;

			this.sampleRate = sampleRate;

			fireChange(new PropertyChange(oldSampleRate, this.sampleRate));
		}
	}

	public String getAudioDriver() {
		return audioDriver;
	}

	public String getSoundfont() {
		return soundfont;
	}

	public void setSoundfont(String soundfont) {
		soundfont = cleanPath(soundfont);

		if (!Null.safeEquals(this.soundfont, soundfont)) {
			String oldSoundfont = this.soundfont;

			this.soundfont = soundfont;

			fireChange(new PropertyChange(oldSoundfont, this.soundfont));
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
			throw new IllegalArgumentException(
					"channels must be less or equal 256");
		}
		if (channels % 16 != 0) {
			throw new IllegalArgumentException(
					"channels must be multiple of 16");
		}

		if (this.channels != channels) {
			int oldChannels = this.channels;

			this.channels = channels;

			fireChange(new PropertyChange(oldChannels, this.channels));
		}
	}

	public int getPolyphony() {
		if (polyphony == 0) {
			// backwards compatibiliy for dispositions without polyphony
			polyphony = 256;
		}
		return polyphony;
	}

	public void setPolyphony(int polyphony) {
		if (polyphony < 16) {
			throw new IllegalArgumentException(
					"polyphony must be greater or equal 16");
		}
		if (polyphony > 4096) {
			throw new IllegalArgumentException(
					"polyphony must be less or equal 4096");
		}

		if (this.polyphony != polyphony) {
			int oldPolyphony = this.polyphony;

			this.polyphony = polyphony;

			fireChange(new PropertyChange(oldPolyphony, this.polyphony));
		}
	}

	public Chorus getChorus() {
		return chorus;
	}

	public void setChorus(Chorus chorus) {
		this.chorus = chorus;

		fireChange(new FastPropertyChange("chorus", false));
	}

	public Reverb getReverb() {
		return reverb;
	}

	public void setReverb(Reverb reverb) {
		this.reverb = reverb;

		fireChange(new FastPropertyChange("reverb", false));
	}

	public List<Tuning> getTunings() {
		return Collections.unmodifiableList(tunings);
	}

	public void changeTuning(final Tuning tuning, final String name,
			final double[] derivations) {
		if (!tunings.contains(tuning)) {
			throw new IllegalArgumentException("unkown tuning");
		}

		final String oldName = tuning.getName();
		final double[] oldDerviations = tuning.getDerivations();

		tuning.change(name, derivations);

		fireChange(new AbstractChange() {
			public void notify(OrganListener listener) {
				listener.indexedPropertyChanged(FluidsynthSound.this, TUNINGS,
						tuning);
			}

			public void undo() {
				tuning.change(oldName, oldDerviations);
			}

			public void redo() {
				changeTuning(tuning, name, derivations);
			}
		});
	}

	public void addTuning(final Tuning tuning) {
		if (tunings == null) {
			tunings = new ArrayList<Tuning>();
		}

		tunings.add(tuning);

		fireChange(new AbstractChange() {
			public void notify(OrganListener listener) {
				listener.indexedPropertyChanged(FluidsynthSound.this, TUNINGS,
						tuning);
			}

			public void undo() {
				removeTuning(tuning);
			}

			public void redo() {
				addTuning(tuning);
			}
		});
	}

	public void removeTuning(final Tuning tuning) {
		if (!tunings.contains(tuning)) {
			throw new IllegalArgumentException("unkown tuning");
		}

		tunings.remove(tuning);

		fireChange(new AbstractChange() {
			public void notify(OrganListener listener) {
				listener.indexedPropertyChanged(FluidsynthSound.this, TUNINGS,
						tuning);
			}

			public void undo() {
				addTuning(tuning);
			}

			public void redo() {
				removeTuning(tuning);
			}
		});
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
			throw new IllegalArgumentException(
					"audioBuffers must be less than 16");
		}

		if (this.audioBuffers != audioBuffers) {
			int oldAudioBuffers = this.audioBuffers;

			this.audioBuffers = audioBuffers;

			fireChange(new PropertyChange(oldAudioBuffers, this.audioBuffers));
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
			throw new IllegalArgumentException(
					"audioBufferSize must be less than 8192");
		}

		if (this.audioBufferSize != audioBufferSize) {
			int oldAudioBufferSize = this.audioBufferSize;

			this.audioBufferSize = audioBufferSize;

			fireChange(new PropertyChange(oldAudioBufferSize,
					this.audioBufferSize));
		}
	}

	public String getAudioDevice() {
		return audioDevice;
	}

	public void setAudioDevice(String audioDevice) {
		if (!Null.safeEquals(this.audioDevice, audioDevice)) {
			String oldAudioDevice = this.audioDevice;

			if ("".equals(audioDevice)) {
				audioDevice = null;
			}
			this.audioDevice = audioDevice;

			fireChange(new PropertyChange(oldAudioDevice, this.audioDevice));
		}
	}

	public boolean equals(FluidsynthSound sound) {

		return Null.safeEquals(getAudioDriver(), sound.getAudioDriver())
				&& Null.safeEquals(getAudioDevice(), sound.getAudioDevice())
				&& getAudioBuffers() == sound.getAudioBuffers()
				&& getAudioBufferSize() == sound.getAudioBufferSize()
				&& getSampleRate() == sound.getSampleRate()
				&& getPolyphony() == sound.getPolyphony()
				&& getChannels() == sound.getChannels()
				&& Null.safeEquals(getSoundfont(), sound.getSoundfont());
	}
}