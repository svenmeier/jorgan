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
  private static final boolean WARN_CRESCENDO_WITHOUT_MESSAGE  = false;
  private static final boolean WARN_VARIATION_WITHOUT_MESSAGE  = false;
  private static final boolean WARN_COMBINATION_WITHOUT_MESSAGE= false;

  private static final boolean RELEASE_DEVICES_WHEN_DEACTIVATED = false;

  private static Configuration sharedInstance = new Configuration();

  private boolean warnKeyboardWithoutDevice;
  private boolean warnConsoleWithoutDevice;
  private boolean warnSoundSourceWithoutDevice;
  private boolean warnStopWithoutMessage;
  private boolean warnCouplerWithoutMessage;
  private boolean warnTremulantWithoutMessage;
  private boolean warnSwellWithoutMessage;
  private boolean warnCrescendoWithoutMessage;
  private boolean warnVariationWithoutMessage;
  private boolean warnCombinationWithoutMessage;

  private boolean releaseDevicesWhenDeactivated;

  protected void restore(Preferences prefs) {
    warnKeyboardWithoutDevice    = getBoolean(prefs, "warnKeyboardWithoutDevice"   , WARN_KEYBOARD_WITHOUT_DEVICE);
    warnConsoleWithoutDevice     = getBoolean(prefs, "warnConsoleWithoutDevice"    , WARN_CONSOLE_WITHOUT_DEVICE);
    warnSoundSourceWithoutDevice = getBoolean(prefs, "warnSoundSourceWithoutDevice", WARN_SOUNDSOURCE_WITHOUT_DEVICE);
    warnStopWithoutMessage       = getBoolean(prefs, "warnStopWithoutMessage"      , WARN_STOP_WITHOUT_MESSAGE);;
    warnCouplerWithoutMessage    = getBoolean(prefs, "warnCouplerWithoutMessage"   , WARN_COUPLER_WITHOUT_MESSAGE);;
    warnTremulantWithoutMessage  = getBoolean(prefs, "warnTremulantWithoutMessage" , WARN_TREMULANT_WITHOUT_MESSAGE);;
    warnSwellWithoutMessage      = getBoolean(prefs, "warnSwellWithoutMessage"     , WARN_SWELL_WITHOUT_MESSAGE);;
    warnCrescendoWithoutMessage  = getBoolean(prefs, "warnCrescendoWithoutMessage" , WARN_CRESCENDO_WITHOUT_MESSAGE);;
    warnVariationWithoutMessage  = getBoolean(prefs, "warnVariationWithoutMessage" , WARN_VARIATION_WITHOUT_MESSAGE);;
    warnCombinationWithoutMessage= getBoolean(prefs, "warnCombinationWithoutMessage"    , WARN_COMBINATION_WITHOUT_MESSAGE);;

    releaseDevicesWhenDeactivated = getBoolean(prefs, "releaseDevicesWhenDeactivated", RELEASE_DEVICES_WHEN_DEACTIVATED);
  }

  protected void backup(Preferences prefs) {
    putBoolean(prefs, "warnConsoleWithoutDevice"     , warnConsoleWithoutDevice);
    putBoolean(prefs, "warnKeyboadWithoutDevice"     , warnKeyboardWithoutDevice);
    putBoolean(prefs, "warnSoundSourceWithoutDevice" , warnSoundSourceWithoutDevice);
    putBoolean(prefs, "warnStopWithoutMessage"       , warnStopWithoutMessage);;
    putBoolean(prefs, "warnCouplerWithoutMessage"    , warnCouplerWithoutMessage);;
    putBoolean(prefs, "warnTremulantWithoutMessage"  , warnTremulantWithoutMessage);;
    putBoolean(prefs, "warnSwellWithoutMessage"      , warnSwellWithoutMessage);;
    putBoolean(prefs, "warnCrescendoWithoutMessage"  , warnCrescendoWithoutMessage);;
    putBoolean(prefs, "warnVariationWithoutMessage"  , warnVariationWithoutMessage);;
    putBoolean(prefs, "warnCombinationWithoutMessage", warnCombinationWithoutMessage);;

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

  public boolean getWarnCrescendoWithoutMessage() {
    return warnCrescendoWithoutMessage;
  }

  public boolean getWarnVariationWithoutMessage() {
    return warnVariationWithoutMessage;
  }

  public boolean getWarnCombinationWithoutMessage() {
    return warnCombinationWithoutMessage;
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

  public void setWarnCrescendoWithoutMessage(boolean warnCrescendoWithoutMessage) {
    this.warnCrescendoWithoutMessage = warnCrescendoWithoutMessage;
      
    fireConfigurationChanged();
  }

  public void setWarnVariationWithoutMessage(boolean warnVariationWithoutMessage) {
    this.warnVariationWithoutMessage = warnVariationWithoutMessage;

    fireConfigurationChanged();
  }

  public void setWarnCombinationWithoutMessage(boolean warnCombinationWithoutMessage) {
    this.warnCombinationWithoutMessage = warnCombinationWithoutMessage;
    
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