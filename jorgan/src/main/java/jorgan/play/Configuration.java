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
package jorgan.play;

import java.util.prefs.*;

import jorgan.config.prefs.*;

/**
 * Configuration of the play package.
 */
public class Configuration extends PreferencesConfiguration {

  private static final boolean WARN_KEYBOARD_WITHOUT_DEVICE    = true;
  private static final boolean WARN_CONSOLE_WITHOUT_DEVICE     = true;
  private static final boolean WARN_SOUNDSOURCE_WITHOUT_DEVICE = true;
  private static final boolean WARN_STOP_WITHOUT_MESSAGE       = false;
  private static final boolean WARN_COUPLER_WITHOUT_MESSAGE    = false;
  private static final boolean WARN_TREMULANT_WITHOUT_MESSAGE  = false;
  private static final boolean WARN_SWELL_WITHOUT_MESSAGE      = false;
  private static final boolean WARN_VARIATION_WITHOUT_MESSAGE  = false;
  private static final boolean WARN_PISTON_WITHOUT_MESSAGE     = false;

  private static final boolean RELEASE_DEVICES_WHEN_DEACTIVATED = false;

  private static Configuration sharedInstance = new Configuration();

  private boolean warnKeyboardWithoutDevice;
  private boolean warnConsoleWithoutDevice;
  private boolean warnSoundSourceWithoutDevice;
  private boolean warnStopWithoutMessage;
  private boolean warnCouplerWithoutMessage;
  private boolean warnTremulantWithoutMessage;
  private boolean warnSwellWithoutMessage;
  private boolean warnVariationWithoutMessage;
  private boolean warnPistonWithoutMessage;

  private boolean releaseDevicesWhenDeactivated;

  protected void restore(Preferences prefs) {
    warnKeyboardWithoutDevice    = prefs.getBoolean("warnKeyboardWithoutDevice"   , WARN_KEYBOARD_WITHOUT_DEVICE);
    warnConsoleWithoutDevice     = prefs.getBoolean("warnConsoleWithoutDevice"    , WARN_CONSOLE_WITHOUT_DEVICE);
    warnSoundSourceWithoutDevice = prefs.getBoolean("warnSoundSourceWithoutDevice", WARN_SOUNDSOURCE_WITHOUT_DEVICE);
    warnStopWithoutMessage       = prefs.getBoolean("warnStopWithoutMessage"      , WARN_STOP_WITHOUT_MESSAGE);;
    warnCouplerWithoutMessage    = prefs.getBoolean("warnCouplerWithoutMessage"   , WARN_COUPLER_WITHOUT_MESSAGE);;
    warnTremulantWithoutMessage  = prefs.getBoolean("warnTremulantWithoutMessage" , WARN_TREMULANT_WITHOUT_MESSAGE);;
    warnSwellWithoutMessage      = prefs.getBoolean("warnSwellWithoutMessage"     , WARN_SWELL_WITHOUT_MESSAGE);;
    warnVariationWithoutMessage  = prefs.getBoolean("warnVariationWithoutMessage" , WARN_VARIATION_WITHOUT_MESSAGE);;
    warnPistonWithoutMessage     = prefs.getBoolean("warnPistonWithoutMessage"    , WARN_PISTON_WITHOUT_MESSAGE);;

    releaseDevicesWhenDeactivated = prefs.getBoolean("releaseDevicesWhenDeactivated", RELEASE_DEVICES_WHEN_DEACTIVATED);
  }

  protected void backup(Preferences prefs) {
    prefs.putBoolean("warnConsoleWithoutDevice"    , warnConsoleWithoutDevice);
    prefs.putBoolean("warnKeyboadWithoutDevice"    , warnKeyboardWithoutDevice);
    prefs.putBoolean("warnSoundSourceWithoutDevice", warnSoundSourceWithoutDevice);
    prefs.putBoolean("warnStopWithoutMessage"      , warnStopWithoutMessage);;
    prefs.putBoolean("warnCouplerWithoutMessage"   , warnCouplerWithoutMessage);;
    prefs.putBoolean("warnTremulantWithoutMessage" , warnTremulantWithoutMessage);;
    prefs.putBoolean("warnSwellWithoutMessage"     , warnSwellWithoutMessage);;
    prefs.putBoolean("warnVariationWithoutMessage" , warnVariationWithoutMessage);;
    prefs.putBoolean("warnPistonWithoutMessage"    , warnPistonWithoutMessage);;

    prefs.putBoolean("releaseDevicesWhenDeactivated", releaseDevicesWhenDeactivated);
  }

  public boolean getWarnConsoleWithoutDevice() {
    return warnConsoleWithoutDevice;
  }

  public boolean getWarnKeyboardWithoutDevice() {
    return warnKeyboardWithoutDevice;
  }

  public boolean getWarnSoundSourceWithoutDevice() {
    return warnSoundSourceWithoutDevice;
  }

  public boolean getWarnStopWithoutMessage() {
    return warnStopWithoutMessage;
  }

  public boolean getWarnCouplerWithoutMessage() {
    return warnCouplerWithoutMessage;
  }

  public boolean getWarnTremulantWithoutMessage() {
    return warnTremulantWithoutMessage;
  }

  public boolean getWarnSwellWithoutMessage() {
    return warnSwellWithoutMessage;
  }

  public boolean getWarnVariationWithoutMessage() {
    return warnVariationWithoutMessage;
  }

  public boolean getWarnPistonWithoutMessage() {
    return warnPistonWithoutMessage;
  }

  public boolean getReleaseDevicesWhenDeactivated() {
    return releaseDevicesWhenDeactivated;
  }

  public void setWarnConsoleWithoutDevice(boolean warnConsoleWithoutDevice) {
    this.warnConsoleWithoutDevice = warnConsoleWithoutDevice;
    
    fireConfigurationChanged();
  }

  public void setWarnKeyboardWithoutDevice(boolean warnKeyboardWithoutDevice) {
    this.warnKeyboardWithoutDevice = warnKeyboardWithoutDevice;
    
    fireConfigurationChanged();
  }

  public void setWarnSoundSourceWithoutDevice(boolean warnSoundSourceWithoutDevice) {
    this.warnSoundSourceWithoutDevice = warnSoundSourceWithoutDevice;
    
    fireConfigurationChanged();
  }

  public void setWarnStopWithoutMessage(boolean warnStopWithoutMessage) {
    this.warnStopWithoutMessage = warnStopWithoutMessage;
    
    fireConfigurationChanged();
  }

  public void setWarnCouplerWithoutMessage(boolean warnCouplerWithoutMessage) {
    this.warnCouplerWithoutMessage = warnCouplerWithoutMessage;
    
    fireConfigurationChanged();
  }

  public void setWarnTremulantWithoutMessage(boolean warnTremulantWithoutMessage) {
    this.warnTremulantWithoutMessage = warnTremulantWithoutMessage;
    
    fireConfigurationChanged();
  }

  public void setWarnSwellWithoutMessage(boolean warnSwellWithoutMessage) {
    this.warnSwellWithoutMessage = warnSwellWithoutMessage;
    
    fireConfigurationChanged();
  }

  public void setWarnVariationWithoutMessage(boolean warnVariationWithoutMessage) {
    this.warnVariationWithoutMessage = warnVariationWithoutMessage;

    fireConfigurationChanged();
  }

  public void setWarnPistonWithoutMessage(boolean warnPistonWithoutMessage) {
    this.warnPistonWithoutMessage = warnPistonWithoutMessage;
    
    fireConfigurationChanged();
  }

  public void setReleaseDevicesWhenDeactivated(boolean releaseDevicesWhenDeactivated) {
    this.releaseDevicesWhenDeactivated = releaseDevicesWhenDeactivated;
    
    fireConfigurationChanged();
  }

  /**
   * Get the shared configuration.
   *
   * @return configuration
   */
  public static Configuration instance() {
    return sharedInstance;
  }
}