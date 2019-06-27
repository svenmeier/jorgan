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
package jorgan.fluidsynth;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.ShortMessage;

import jorgan.fluidsynth.windows.BackendManager;
import jorgan.util.ClassUtils;
import jorgan.util.NativeUtils;

public class Fluidsynth {

	private static final Logger logger = Logger.getLogger(Fluidsynth.class
			.getName());

	public static final String LIBRARY_PATH = "jorgan.fluidsynth.library.path";

	private static final int NAME_MAX_LENGTH = 32;

	private ByteBuffer context;

	public Fluidsynth() throws IllegalStateException, IOException {
		this("", 16, null);
	}

	public Fluidsynth(String name, int channels, String audioDriver)
			throws IllegalStateException, IOException {
		this(name, 1, channels, 256, 44100.0f, audioDriver, null, 8, 512, 0.5f,
				0.5f, 0.5f, 0.5f, 0.5f);
	}

	public Fluidsynth(String name, int cores, int channels, int polyphony,
			float sampleRate, String audioDriver, String audioDevice,
			int buffers, int bufferSize, float overflowAge,
			float overflowPercussion, float overflowReleased,
			float overflowSustained, float overflowVolume) throws IOException {

		name = name.substring(0, Math.min(name.length(), NAME_MAX_LENGTH));

		context = init(name, cores, channels, polyphony, sampleRate,
				audioDriver, audioDevice, buffers, bufferSize, overflowAge,
				overflowPercussion, overflowReleased, overflowSustained,
				overflowVolume);
	}

	public void soundFontLoad(File soundfont, int bank) throws IOException {
		soundFontLoad(context, soundfont.getAbsolutePath(), bank);
	}

	public void setGain(float gain) {
		setGain(context, gain);
	}

	public void setInterpolate(int number) {
		setInterpolate(context, number);
	}

	public void setReverbOn(boolean b) {
		setReverbOn(context, b);
	}

	public void setReverb(double roomsize, double damping, double width,
			double level) {
		setReverb(context, roomsize, damping, width, level);
	}

	public void setChorusOn(boolean b) {
		setChorusOn(context, b);
	}

	public void setChorus(int nr, double level, double speed, double depth_ms,
			int type) {
		setChorus(context, nr, level, speed, depth_ms, type);
	}

	public void setTuning(int tuningBank, int tuningProgram, String name,
			double[] derivations) {
		if (derivations == null || derivations.length != 12) {
			throw new IllegalArgumentException();
		}
		setTuning(context, tuningBank, tuningProgram, name, derivations);
	}

	public void send(int channel, int command, int data1, int data2) {
		switch (command) {
		case ShortMessage.NOTE_ON:
			noteOn(context, channel, data1, data2);
			break;
		case ShortMessage.NOTE_OFF:
			noteOff(context, channel, data1);
			break;
		case ShortMessage.PROGRAM_CHANGE:
			programChange(context, channel, data1);
			break;
		case ShortMessage.CONTROL_CHANGE:
			controlChange(context, channel, data1, data2);
			break;
		case ShortMessage.PITCH_BEND:
			pitchBend(context, channel, (data2 * 128) + data1);
			break;
		}
	}

	public void destroy() {
		destroy(context);
		context = null;
	}

	private static native ByteBuffer init(String name, int cores, int channels,
			int polyphony, float sampleRate, String audioDriver,
			String audioDevice, int buffers, int bufferSize, float overflowAge,
			float overflowPercussion, float overflowReleased,
			float overflowSustained, float overflowVolume) throws IOException;

	private static native void destroy(ByteBuffer context);

	private native void soundFontLoad(ByteBuffer context, String filename,
			int bank) throws IOException;

	private static native void noteOn(ByteBuffer context, int channel, int key,
			int velocity);

	private static native void noteOff(ByteBuffer context, int channel, int key);

	private static native void controlChange(ByteBuffer context, int channel,
			int controller, int value);

	private static native void pitchBend(ByteBuffer context, int channel,
			int bend);

	private static native void programChange(ByteBuffer context, int channel,
			int program);

	private static native void setGain(ByteBuffer context, float gain);

	private static native void setInterpolate(ByteBuffer context, int number);

	private static native void setReverbOn(ByteBuffer context, boolean b);

	private static native void setReverb(ByteBuffer context, double roomsize,
			double damping, double width, double level);

	private static native void setChorusOn(ByteBuffer context, boolean b);

	private static native void setChorus(ByteBuffer context, int nr,
			double level, double speed, double depth_ms, int type);

	private static native void setTuning(ByteBuffer context, int tuningBank,
			int tuningProgram, String name, double[] derivations);

	/**
	 * Get the available {@link #getAudioDriver()}s.
	 * 
	 * @return possible options for audio drivers
	 */
	public native static List<String> getAudioDrivers();

	/**
	 * Get the available {@link #getAudioDevice()}s.
	 * 
	 * @param audioDriver
	 *            the audio driver to get possible devices for
	 * @return possible options for audio devices
	 */
	public native static List<String> getAudioDevices(String audioDriver);

	/**
	 * Load the native library "fluidsynth" from the path specified via the
	 * system property {@link #JORGAN_FLUIDSYNTH_LIBRARY_PATH} or the directory
	 * this class was loaded from.
	 * 
	 * @see jorgan.util.ClassUtils
	 */
	static {
		File directory;
		String path = System.getProperty(LIBRARY_PATH);
		if (path == null) {
			directory = ClassUtils.getDirectory(Fluidsynth.class);
		} else {
			directory = new File(path);
		}

		try {
			if (NativeUtils.isWindows()) {
				new BackendManager().loadLibraries();
			}

			NativeUtils.load(directory, "fluidsynthJNI");
		} catch (UnsatisfiedLinkError error) {
			logger.log(Level.INFO, "native failure", error);
			throw new NoClassDefFoundError();
		}
	}
}
