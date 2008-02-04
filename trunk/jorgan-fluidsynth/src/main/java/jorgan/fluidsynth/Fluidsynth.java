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

import javax.sound.midi.ShortMessage;

import jorgan.util.ClassUtils;

/**
 * Java Wrapper for a Fluidsynth.
 */
public class Fluidsynth {

	public static final String JORGAN_FLUIDSYNTH_LIBRARY_PATH = "jorgan.fluidsynth.library.path";

	private static final String LIBRARY = "fluidsynthJNI";

	public Fluidsynth() {
		create();
	}

	public void dispose() {
		destroy();
	}
	
	private native void create();

	private native void destroy();

	public void soundFontLoad(File file) throws IOException {
		soundFontLoad(file.getAbsolutePath());
	}

	public native void soundFontLoad(String filename) throws IOException;

	public native void noteOn(int channel, int key, int velocity);

	public native void noteOff(int channel, int key);

	public native void controlChange(int channel, int controller, int value);

	public native void pitchBend(int channel, int bend);

	public native void programChange(int channel, int program);
	
	/**
	 * Load the native library "fluidsynth" from the path specified via the system
	 * property {@link #JORGAN_FLUIDSYNTH_LIBRARY_PATH} or the directory this
	 * class was loaded from. Fall back to standard VM library loading which
	 * tries to resolve to a .dll/.so on <code>java.library.path</code> or a
	 * system directory.
	 * 
	 * @see jorgan.util.ClassUtils
	 */
	static {
		try {
			File file;
			String path = System.getProperty(JORGAN_FLUIDSYNTH_LIBRARY_PATH);
			if (path == null) {
				file = ClassUtils.getDirectory(Fluidsynth.class);
			} else {
				file = new File(path);
			}

			String library = new File(file, System.mapLibraryName(LIBRARY))
					.getCanonicalPath();
			System.load(library);
		} catch (Throwable t) {
			System.loadLibrary(LIBRARY);
		}
	}

	public void send(ShortMessage shortMessage) {
		int channel = shortMessage.getChannel();

		switch (shortMessage.getCommand()) {
		case ShortMessage.NOTE_ON:
			noteOn(channel, shortMessage.getData1(), shortMessage
					.getData2());
			break;
		case ShortMessage.NOTE_OFF:
			noteOff(channel, shortMessage.getData1());
			break;
		case ShortMessage.PROGRAM_CHANGE:
			programChange(channel, shortMessage.getData1());
			break;
		case ShortMessage.CONTROL_CHANGE:
			controlChange(channel, shortMessage.getData1(),
					shortMessage.getData2());
			break;
		case ShortMessage.PITCH_BEND:
			pitchBend(channel, shortMessage.getData1());
			break;
		}
	}
}
