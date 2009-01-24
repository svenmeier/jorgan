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
	
	private static boolean supported;

	/**
	 * Load the native library "creativeJNI" from the path specified via the system
	 * property {@link #JORGAN_CREATIVE_LIBRARY_PATH} or the directory this
	 * class was loaded from.
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

		try {
			System.load(NativeUtils.getLibraryName(file, "creativeJNI"));

			supported = true;
		} catch (UnsatisfiedLinkError e) {
			if (e.getMessage().contains("unsupported JNI version 0xFFFFFFFF")) {
				// JNI signales sfman32.dll unavailable
				supported = false;
			} else {
				throw e;
			}
		}
	}

	public boolean isSupported() {
		return supported;
	}
	
	/**
	 * Get the index of a device.
	 * 
	 * @param device
	 *            the device to query
	 * @return index of device
	 * @throws IllegalArgumentException
	 *             if device is not known
	 */
	public int getDeviceIndex(String device) throws IllegalArgumentException {
		for (int d = getNumDevices() - 1; d >= 0; d--) {
			if (getDeviceName(d).equals(device)) {
				return d;
			}
		}

		throw new IllegalArgumentException("unkown device " + device);
	}

	/**
	 * Get the total number of available devices. The standard Soundblaster Live
	 * drivers are available as two devices, e.g. 'SB Live! Synth A [D800]' and
	 * 'SB Live! Synth B [D800]'.
	 * 
	 * @return number of devices
	 */
	public native int getNumDevices();

	/**
	 * Get the name of a device.
	 * 
	 * @param device
	 *            the device to query
	 * @return name of device
	 * @throws IllegalArgumentException
	 *             if device is not known
	 */
	public native String getDeviceName(int device)
			throws IllegalArgumentException;

	/**
	 * Test if a bank is used, i.e. if a SoundFont is loaded.
	 * 
	 * @param device
	 *            device to test
	 * @param bank
	 *            bank to test
	 * @return <code>true</code> if bank is used
	 * @throws IllegalArgumentException
	 *             if device is unknown or bank invalid
	 */
	public native boolean isBankUsed(int device, int bank)
			throws IllegalArgumentException;

	/**
	 * Get the description of a bank.
	 * 
	 * @param device
	 *            device to query
	 * @param bank
	 *            bank to get description for
	 * @return description of bank
	 * @throws IllegalArgumentException
	 *             if device is unknown or bank invalid
	 */
	public native String getBankDescriptor(int device, int bank)
			throws IllegalArgumentException;

	/**
	 * Get the fileName of a SoundFont loaded into the given bank.
	 * 
	 * @param device
	 *            device to query
	 * @param bank
	 *            bank to get fileName for
	 * @return pathName of SoundFont
	 * @throws IllegalArgumentException
	 *             if device is unknown or bank invalid
	 */
	public native String getBankFileName(int device, int bank)
			throws IllegalArgumentException;

	/**
	 * Load a SoundFont into the given bank.
	 * 
	 * @param device
	 *            device to load into
	 * @param bank
	 *            bank to load SoundFont into (forward/backward slashes ok)
	 * @param fileName
	 *            fileName of SoundFont to load
	 * @throws IllegalArgumentException
	 *             if device is unknown or bank invalid
	 * @throws IOException
	 *             if file was not loaded
	 */
	public native void loadBank(int device, int bank, String fileName)
			throws IllegalArgumentException, IOException;

	/**
	 * Clear a bank.
	 * 
	 * @param device
	 *            device to clear bank in
	 * @param bank
	 *            bank to clear
	 * @throws IllegalArgumentException
	 *             if device is unknown or bank invalid
	 * @throws IOException
	 *             if device is busy
	 */
	public native void clearBank(int device, int bank)
			throws IllegalArgumentException, IOException;

	/**
	 * Get the description of a preset.
	 * 
	 * @param device
	 *            device to query
	 * @param bank
	 *            bank of preset
	 * @param preset
	 *            preset
	 * @return description of preset
	 * @throws IllegalArgumentException
	 *             if device is unknown or bank invalid or preset invalid
	 */
	public native String getPresetDescriptor(int device, int bank, int preset)
			throws IllegalArgumentException;
}