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
package jorgan.config.prefs;

import java.io.*;
import java.util.prefs.*;

/**
 * Preferences implementation used for resetting of configurations.
 * <br>
 * Getters Always returns the default value, all other methods throw
 * a runtime exception.
 */
public class ResetPreferences extends Preferences {
  
  private UnsupportedOperationException abuseDetected()  {
    return new UnsupportedOperationException("Preferences only useable for reset");
  }

  public String toString() {
    throw abuseDetected();
  }
  
  public void clear() throws BackingStoreException {
    throw abuseDetected();
  }
  
  public void flush() throws BackingStoreException {
    throw abuseDetected();
  }

  public void removeNode() throws BackingStoreException {
    throw abuseDetected();
  }

  public void sync() throws BackingStoreException {
    throw abuseDetected();
  }

  public boolean isUserNode() {    
    throw abuseDetected();
  }

  public void exportNode(OutputStream os) {
    throw abuseDetected();
  }

  public void exportSubtree(OutputStream os) {
    throw abuseDetected();
  }

  public String absolutePath() {
    throw abuseDetected();
  }

  public String name() {
    throw abuseDetected();
  }

  public String[] childrenNames() throws BackingStoreException {
    throw abuseDetected();
  }

  public String[] keys() throws BackingStoreException {
    throw abuseDetected();
  }

  public void remove(String key) {
    throw abuseDetected();
  }

  public boolean nodeExists(String pathName) throws BackingStoreException {
    throw abuseDetected();
  }

  public double getDouble(String key, double def) {
    return def;
  }

  public void putDouble(String key, double value) {
    throw abuseDetected();
  }

  public float getFloat(String key, float def) {
    return def;
  }

  public void putFloat(String key, float value) {
    throw abuseDetected();
  }

  public int getInt(String key, int def) {
    return def;
  }

  public void putInt(String key, int value) {
    throw abuseDetected();
  }

  public long getLong(String key, long def) {
    return def;
  }

  public void putLong(String key, long value) {
    throw abuseDetected();
  }

  public void putBoolean(String key, boolean value) {
    throw abuseDetected();
  }

  public boolean getBoolean(String key, boolean def) {
    return def;
  }

  public void putByteArray(String key, byte[] value) {
    throw abuseDetected();
  }

  public byte[] getByteArray(String key, byte[] def) {
    return def;
  }

  public void addNodeChangeListener(NodeChangeListener ncl) {
    throw abuseDetected();
  }

  public void removeNodeChangeListener(NodeChangeListener ncl) {
    throw abuseDetected();
  }

  public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
    throw abuseDetected();
  }

  public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
    throw abuseDetected();
  }

  public Preferences parent() {
    throw abuseDetected();
  }

  public void put(String key, String value) {
    throw abuseDetected();
  }

  public Preferences node(String pathName) {
    throw abuseDetected();
  }

  public String get(String key, String def) {
    return def;
  }
}
