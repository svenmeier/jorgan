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
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.util.ClassUtils;

/**
 * Java Wrapper for a Creative SoundBlaster SoundFont Manager.
 */
public class SoundFontManager {

	private static final Logger logger = Logger
			.getLogger(SoundFontManager.class.getName());

	public static final String JORGAN_CREATIVE_LIBRARY_PATH = "jorgan.creative.library.path";

	private static final String LIBRARY = "creativeJNI";

	/**
	 * Load the native library "creative" from the path specified via the system
	 * property {@link #JORGAN_CREATIVE_LIBRARY_PATH} or the directory this
	 * class was loaded from. Fall back to standard VM library loading which
	 * tries to resolve to a .dll/.so on <code>java.library.path</code> or a
	 * system directory.
	 * 
	 * @see jorgan.util.ClassUtils
	 */
	static {
		try {
			File file;
			String path = System.getProperty(JORGAN_CREATIVE_LIBRARY_PATH);
			if (path == null) {
				file = ClassUtils.getDirectory(SoundFontManager.class);
			} else {
				file = new File(path);
			}

			String library = new File(file, System.mapLibraryName(LIBRARY))
					.getCanonicalPath();
			System.load(library);
		} catch (Throwable t) {
			logger.log(Level.WARNING, "falling back to System.loadLibary()", t);
			
			System.loadLibrary(LIBRARY);
		}
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
	 */
	public native String getDeviceName(int device);

	/**
	 * Open a device. An application calls this function when it wishes to
	 * acquire a specific SoundFont device.
	 * 
	 * @param device
	 *            number of device to open
	 */
	public native void open(int device) throws IOException;

	/**
	 * Close a device. An application calls this function when it wishes to
	 * release control of an acquired SoundFont device.
	 * 
	 * @param device
	 *            the device to close
	 */
	public native void close(int device) throws IOException;

	/**
	 * Test if a bank is used, i.e. if a SoundFont is loaded.
	 * 
	 * @param device
	 *            device to test
	 * @param bank
	 *            bank to test
	 * @return <code>true</code> if bank is used
	 */
	public native boolean isBankUsed(int device, int bank);

	/**
	 * Get the description of a bank.
	 * 
	 * @param device
	 *            device to query
	 * @param bank
	 *            bank to get description for
	 * @return description of bank
	 */
	public native String getBankDescriptor(int device, int bank);

	/**
	 * Get the fileName of a SoundFont loaded into the given bank.
	 * 
	 * @param device
	 *            device to query
	 * @param bank
	 *            bank to get fileName for
	 * @return pathName of SoundFont
	 */
	public native String getBankFileName(int device, int bank);

	/**
	 * Load a SoundFont into the given bank.
	 * 
	 * @param device
	 *            device to load into
	 * @param bank
	 *            bank to load SoundFont into (forward/backward slashes ok)
	 * @param fileName
	 *            fileName of SoundFont to load
	 */
	public native void loadBank(int device, int bank, String fileName)
			throws IOException;

	/**
	 * Clear a bank.
	 * 
	 * @param device
	 *            device to clear bank in
	 * @param bank
	 *            bank to clear
	 */
	public native void clearBank(int device, int bank) throws IOException;

	/**
	 * Get the description of a preset in given program and bank.
	 * 
	 * @param device
	 *            device to query
	 * @param bank
	 *            bank of preset
	 * @param program
	 *            program of preset
	 * @return description of preset in the given bank and program
	 */
	public native String getPresetDescriptor(int device, int bank, int program);
}