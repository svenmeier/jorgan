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

import jorgan.disposition.Element;
import jorgan.disposition.Sound;
import jorgan.disposition.event.AbstractChange;
import jorgan.disposition.event.OrganListener;
import jorgan.util.Null;

/**
 * Fluidsynth settings, see http://fluidsynth.sourceforge.net/api.
 */
public class FluidsynthSound extends Sound {

	public static final String TUNINGS = "tunings";

	private String soundfont;

	private int channels = 64;

	private int polyphony = 256;

	private int sampleRate = 44100;

	private Interpolate interpolate = Interpolate.ORDER_4TH;

	// audio.driver [alsa, oss, jack, dsound, sndman, coreaudio, portaudio]
	private String audioDriver;

	// audio.<driver>.device
	private String audioDevice;

	// audio.periods [2-64]
	private int audioBuffers = 8;

	// audio.period-size [64-8192]
	private int audioBufferSize = 512;

	// synth.cpu-cores
	private int cores = 1;

	private float gain = 0.5f;

	private float overflowPercussion = 0.10f;

	private float overflowSustained = 0.10f;

	private float overflowReleased = 0.50f;

	private float overflowAge = 0.55f;

	private float overflowVolume = 0.50f;

	private int bank;

	private List<Tuning> tunings = new ArrayList<Tuning>();

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Effect.class.isAssignableFrom(clazz);
	}

	public void setCores(int cores) {
		if (cores < 1) {
			throw new IllegalArgumentException(
					"cores must be greater or equal 1");
		}
		if (cores > 256) {
			throw new IllegalArgumentException("cores must be less than 256");
		}

		if (this.cores != cores) {
			int oldCores = this.cores;

			this.cores = cores;

			fireChange(new PropertyChange(oldCores, this.cores));
		}
	}

	public int getCores() {
		return cores;
	}

	public void setGain(float gain) {
		if (gain > 1.0f) {
			gain = 1.0f;
		}
		if (gain < 0.0f) {
			gain = 0.0f;
		}

		if (this.gain != gain) {
			float oldGain = this.gain;

			this.gain = gain;

			fireChange(new PropertyChange(oldGain, gain));
		}
	}

	public float getGain() {
		return gain;
	}

	public void setOverflowPercussion(float overflow) {
		if (overflow > 1.0f) {
			overflow = 1.0f;
		}
		if (overflow < 0.0f) {
			overflow = 0.0f;
		}

		if (this.overflowPercussion != overflow) {
			float oldOverflow = this.overflowPercussion;

			this.overflowPercussion = overflow;

			fireChange(new PropertyChange(oldOverflow, overflow));
		}
	}

	public float getOverflowPercussion() {
		return overflowPercussion;
	}

	public void setOverflowSustained(float overflow) {
		if (overflow > 1.0f) {
			overflow = 1.0f;
		}
		if (overflow < 0.0f) {
			overflow = 0.0f;
		}

		if (this.overflowSustained != overflow) {
			float oldOverflow = this.overflowSustained;

			this.overflowSustained = overflow;

			fireChange(new PropertyChange(oldOverflow, overflow));
		}
	}

	public float getOverflowSustained() {
		return overflowSustained;
	}

	public void setOverflowReleased(float overflow) {
		if (overflow > 1.0f) {
			overflow = 1.0f;
		}
		if (overflow < 0.0f) {
			overflow = 0.0f;
		}

		if (this.overflowReleased != overflow) {
			float oldOverflow = this.overflowReleased;

			this.overflowReleased = overflow;

			fireChange(new PropertyChange(oldOverflow, overflow));
		}
	}

	public float getOverflowReleased() {
		return overflowReleased;
	}

	public void setOverflowVolume(float overflow) {
		if (overflow > 1.0f) {
			overflow = 1.0f;
		}
		if (overflow < 0.0f) {
			overflow = 0.0f;
		}

		if (this.overflowVolume != overflow) {
			float oldOverflow = this.overflowVolume;

			this.overflowVolume = overflow;

			fireChange(new PropertyChange(oldOverflow, overflow));
		}
	}

	public float getOverflowVolume() {
		return overflowVolume;
	}

	public void setOverflowAge(float overflow) {
		if (overflow > 1.0f) {
			overflow = 1.0f;
		}
		if (overflow < 0.0f) {
			overflow = 0.0f;
		}

		if (this.overflowAge != overflow) {
			float oldOverflow = this.overflowAge;

			this.overflowAge = overflow;

			fireChange(new PropertyChange(oldOverflow, overflow));
		}
	}

	public float getOverflowAge() {
		return overflowAge;
	}

	public void setInterpolate(Interpolate interpolate) {
		if (this.interpolate != interpolate) {
			Interpolate oldInterpolate = this.interpolate;

			this.interpolate = interpolate;

			fireChange(new PropertyChange(oldInterpolate, interpolate));
		}
	}

	public Interpolate getInterpolate() {
		return interpolate;
	}

	public void setAudioDriver(String audioDriver) {
		if (!Null.safeEquals(this.audioDriver, audioDriver)) {
			setAudioDevice(null);

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
		return polyphony;
	}

	public void setPolyphony(int polyphony) {
		if (polyphony < 16) {
			throw new IllegalArgumentException(
					"polyphony must be greater or equal 16");
		}
		if (polyphony > 65535) {
			throw new IllegalArgumentException(
					"polyphony must be less or equal 65535");
		}

		if (this.polyphony != polyphony) {
			int oldPolyphony = this.polyphony;

			this.polyphony = polyphony;

			fireChange(new PropertyChange(oldPolyphony, this.polyphony));
		}
	}

	public int getBank() {
		return bank;
	}

	public void setBank(int bank) {
		if (bank != this.bank) {
			int oldBank = this.bank;

			this.bank = bank;

			fireChange(new PropertyChange(oldBank, this.bank));
		}
	}

	public int getTuningCount() {
		return tunings.size();
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
		addTuning(tuning, tunings.size());
	}

	public void addTuning(final Tuning tuning, final int index) {
		if (this.tunings.size() < index) {
			throw new IllegalArgumentException("index '" + index + "'");
		}

		tunings.add(index, tuning);

		fireChange(new AbstractChange() {
			public void notify(OrganListener listener) {
				listener.indexedPropertyAdded(FluidsynthSound.this, TUNINGS,
						tuning);
			}

			public void undo() {
				removeTuning(tuning);
			}

			public void redo() {
				addTuning(tuning, index);
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
				listener.indexedPropertyRemoved(FluidsynthSound.this, TUNINGS,
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

		return Null.safeEquals(this.audioDriver, sound.audioDriver)
				&& Null.safeEquals(this.audioDevice, sound.audioDevice)
				&& this.audioBuffers == sound.getAudioBuffers()
				&& this.audioBufferSize == sound.getAudioBufferSize()
				&& this.sampleRate == sound.getSampleRate()
				&& this.polyphony == sound.getPolyphony()
				&& this.channels == sound.getChannels()
				&& Null.safeEquals(this.soundfont, sound.getSoundfont())
				&& this.bank == sound.getBank() && this.gain == sound.gain
				&& this.cores == sound.cores
				&& this.interpolate == sound.interpolate
				&& this.overflowAge == sound.overflowAge
				&& this.overflowPercussion == sound.overflowPercussion
				&& this.overflowReleased == sound.overflowReleased
				&& this.overflowSustained == sound.overflowSustained
				&& this.overflowVolume == sound.overflowVolume;
	}

	public static enum Interpolate {
		NONE(0), LINEAR(1), ORDER_4TH(4), ORDER_7TH(7);

		private int number;

		private Interpolate(int number) {
			this.number = number;
		}

		public int number() {
			return number;
		}
	}
}