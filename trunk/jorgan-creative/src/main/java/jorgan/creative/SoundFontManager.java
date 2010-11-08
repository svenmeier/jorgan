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
package jorgan.creative;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import jorgan.util.ClassUtils;
import jorgan.util.NativeUtils;

/**
 * Java Wrapper for a Creative SoundBlaster SoundFont Manager.
 */
public class SoundFontManager {

	public static final String JORGAN_CREATIVE_LIBRARY_PATH = "jorgan.creative.library.path";

	private String deviceName;

	private ByteBuffer context;

	/**
	 * @throws IOException
	 *             if device is not a Creative device
	 */
	public SoundFontManager(String deviceName) throws IOException {
		this.deviceName = deviceName;

		context = init(deviceName);
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void clear(int bank) throws IOException {
		clear(context, bank);
	}

	public boolean isLoaded(int bank) {
		return isLoaded(context, bank);
	}

	public void load(int bank, File file) throws IOException {
		String fileName;
		try {
			fileName = file.getCanonicalPath();
		} catch (IOException e) {
			throw new Error(e);
		}

		load(context, bank, fileName);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if bank is invalid
	 */
	public String getDescriptor(int bank) throws IllegalArgumentException {
		return getDescriptor(context, bank);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if bank or preset are invalid
	 */
	public String getPresetDescriptor(int bank, int preset)
			throws IllegalArgumentException {
		return getPresetDescriptor(context, bank, preset);
	}

	public void destroy() {
		destroy(context);
		context = null;
	}

	private static native ByteBuffer init(String deviceName);

	private static native void destroy(ByteBuffer context);

	private static native void clear(ByteBuffer context, int bank)
			throws IOException;

	private static native boolean isLoaded(ByteBuffer context, int bank);

	private static native void load(ByteBuffer context, int bank,
			String fileName) throws IOException;

	private static native String getDescriptor(ByteBuffer context, int bank)
			throws IllegalArgumentException;

	private static native String getPresetDescriptor(ByteBuffer context,
			int bank, int preset) throws IllegalArgumentException;

	/**
	 * Load the native library "creativeJNI" from the path specified via the
	 * system property {@link #JORGAN_CREATIVE_LIBRARY_PATH} or the directory
	 * this class was loaded from.
	 * 
	 * @see jorgan.util.ClassUtils
	 */
	static {
		File file;
		String path = System.getProperty(JORGAN_CREATIVE_LIBRARY_PATH);
		if (path == null) {
			file = ClassUtils.getDirectory(SoundFontManager.class);
		} else {
			file = new File(path);
		}

		System.load(NativeUtils.mapLibraryName(file, "creativeJNI"));
	}

	/**
	 * Test.
	 */
	public static void test() {
	}
}