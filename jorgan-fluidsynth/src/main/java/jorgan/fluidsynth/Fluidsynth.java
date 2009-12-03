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

import javax.sound.midi.ShortMessage;

import jorgan.util.ClassUtils;
import jorgan.util.NativeUtils;

/**
 * Java Wrapper for a Fluidsynth.
 */
public class Fluidsynth {

	public static final String JORGAN_FLUIDSYNTH_LIBRARY_PATH = "jorgan.fluidsynth.library.path";

	private ByteBuffer context;

	private String name;

	private int channels;

	private int polyphony;

	private String audioDriver;

	private String audioDevice;

	private int buffers;

	private int bufferSize;

	private File soundfont;

	public Fluidsynth() throws IllegalStateException, IOException {
		this("", 16, null);
	}

	public Fluidsynth(String name, int channels, String audioDriver) throws IllegalStateException, IOException {
		this(name, channels, 256, 44100.0f, audioDriver, null, 16, 64);
	}

	public Fluidsynth(String name, int channels, int polyphony,
			float sampleRate, String audioDriver, String audioDevice,
			int buffers, int bufferSize) throws IllegalStateException,
			IOException {

		this.name = name;
		this.channels = channels;
		this.polyphony = polyphony;
		this.audioDriver = audioDriver;
		this.audioDevice = audioDevice;
		this.buffers = buffers;
		this.bufferSize = bufferSize;

		context = init(name, channels, polyphony, sampleRate, audioDriver, audioDevice,
				buffers, bufferSize);
	}

	public String getAudioDevice() {
		return audioDevice;
	}

	public String getAudioDriver() {
		return audioDriver;
	}

	public int getBuffers() {
		return buffers;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int getChannels() {
		return channels;
	}

	public int getPolyphony() {
		return polyphony;
	}

	public String getName() {
		return name;
	}

	public File getSoundfont() {
		return soundfont;
	}

	public void soundFontLoad(File soundfont) throws IOException {
		this.soundfont = soundfont;

		soundFontLoad(context, soundfont.getAbsolutePath());
	}

	public void setGain(float gain) {
		setGain(context, gain);
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

	private static native ByteBuffer init(String name, int channels,
			int polyphony, float sampleRate, String audioDriver, String audioDevice, int buffers,
			int bufferSize) throws IllegalStateException, IOException;

	private static native void destroy(ByteBuffer context);

	private native void soundFontLoad(ByteBuffer context, String filename)
			throws IOException;

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

	private static native void setReverbOn(ByteBuffer context, boolean b);

	private static native void setReverb(ByteBuffer context, double roomsize,
			double damping, double width, double level);

	private static native void setChorusOn(ByteBuffer context, boolean b);

	private static native void setChorus(ByteBuffer context, int nr,
			double level, double speed, double depth_ms, int type);

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
		File file;
		String path = System.getProperty(JORGAN_FLUIDSYNTH_LIBRARY_PATH);
		if (path == null) {
			file = ClassUtils.getDirectory(Fluidsynth.class);
		} else {
			file = new File(path);
		}

		try {
			// for Windows we load fluidsynth explicitely from extension
			System.load(NativeUtils.getLibraryName(file, "libfluidsynth-1"));
		} catch (UnsatisfiedLinkError error) {
			// should be on system library path, thus will be automagically
			// loadad with following JNI wrapper
		}

		System.load(NativeUtils.getLibraryName(file, "fluidsynthJNI"));
	}
}
