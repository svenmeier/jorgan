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

import jorgan.util.ClassUtils;
import jorgan.util.NativeUtils;

/**
 * Java Wrapper for a Creative SoundBlaster SoundFont Manager.
 */
public class SoundFontManager {

	public static final String JORGAN_CREATIVE_LIBRARY_PATH = "jorgan.creative.library.path";

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

		System.load(NativeUtils.getLibraryName(file, "creativeJNI"));
	}

	private String deviceName;

	private int bank;

	public SoundFontManager(String deviceName, int bank) {
		this.deviceName = deviceName;
		this.bank = bank;
	}

	public boolean isLoaded() {
		return isBankUsed(getIndex(), bank);
	}

	public void load(File file) throws IllegalArgumentException, IOException {
		String fileName;
		try {
			fileName = file.getCanonicalPath();
		} catch (IOException e) {
			throw new Error(e);
		}

		loadBank(getIndex(), bank, fileName);
	}

	public String getDescriptor() {
		return getBankDescriptor(getIndex(), bank);
	}

	public String getPresetDescriptor(int preset)
			throws IllegalArgumentException {
		return getPresetDescriptor(getIndex(), bank, preset);
	}

	public void clear() throws IllegalArgumentException, IOException {
		clearBank(getIndex(), bank);
	}

	private int getIndex() {
		for (int i = 0; i < getNumDevices(); i++) {
			if (getDeviceName(i).equals(deviceName)) {
				return i;
			}
		}
		throw new IllegalStateException();
	}

	private static native int getNumDevices();

	private static native String getDeviceName(int device)
			throws IllegalArgumentException;

	private static native boolean isBankUsed(int device, int bank)
			throws IllegalArgumentException;

	private static native String getBankDescriptor(int device, int bank)
			throws IllegalArgumentException;

	private static native void loadBank(int device, int bank, String fileName)
			throws IllegalArgumentException, IOException;

	private static native void clearBank(int device, int bank)
			throws IllegalArgumentException, IOException;

	private static native String getPresetDescriptor(int device, int bank,
			int preset) throws IllegalArgumentException;

	public static String[] getDeviceNames() {
		String[] deviceNames = new String[getNumDevices()];

		for (int i = 0; i < deviceNames.length; i++) {
			deviceNames[i] = getDeviceName(i);
		}

		return deviceNames;
	}
}