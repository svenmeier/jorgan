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
package jorgan.util;

import java.io.*; 
import java.net.*;

/**
 * Collection of utility methods supporting the installation of an
 * application.
 */
public class Installation {

  /**
   * Test is the current running program is installed.
   * <br>
   * This method assumes that an installed java program is contained
   * in a jar file.
   * 
   * @return    <code>true</code> if installed
   */
  public static boolean isInstalled(Class clazz) {
    try {
      URL classUrl = getClassURL(clazz);
    
      return "jar".equals(classUrl.getProtocol());
    } catch (Exception ex) {
      // fall through
    }
    return false;   
  }

  /**
   * Get the directory where the current running program is installed,
   * i.e. the location of the jar file the given class is contained in. 
   * <br>
   * If it is not installed (see {@link #isInstalled()}) the <em>current
   * user working directory</em> (as denoted by the system property
   * <code>user.dir<code>) is returned instead.
   * 
   * @param  clazz  class to check for installation
   * @return        the installation directory
   * @see
   */
  public static File getInstallDirectory(Class clazz) {
    if (isInstalled(clazz)) {
      try {
        JarURLConnection jarCon = (JarURLConnection)getClassURL(clazz).openConnection();

        URL jarUrl = jarCon.getJarFileURL();
    
        File jarFile = new File(URLDecoder.decode(jarUrl.getPath(), "UTF-8"));
    
        return jarFile.getParentFile();
      } catch (Exception ex) {
        // fall through
      }
    }
    
    return new File(System.getProperty("user.dir"));
  }
  
  /**
   * Get URL of the given class.
   * 
   * @param  clazz  class to get URL for
   * @return the URL this class was loaded from
   */
  private static URL getClassURL(Class clazz) {
    return clazz.getResource("/" + clazz.getName().replace('.', '/'));
  }
}