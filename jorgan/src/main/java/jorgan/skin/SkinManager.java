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
package jorgan.skin;

import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;
import javax.swing.*;

import jorgan.io.SkinReader;
import jorgan.util.*;

/**
 * Manager of skins.
 */
public class SkinManager implements ISkinManager {
    
  private static final Logger logger = Logger.getLogger(SkinManager.class.getName());

  /**
   * The name of the system property to specify the path to load skins from.
   * <br>
   * If this system property is not set, skins will be loaded in a "skins"
   * folder relative to the installation directory.
   */
  private static final String SKINS_PATH_PROPERTY = "jorgan.skins.path";
  
  private static final String SKIN_FILE  = "skin.xml"; 
  private static final String ZIP_SUFFIX = ".zip"; 
     
  private static SkinManager instance = new SkinManager();
  
  protected MediaTracker tracker = new MediaTracker(new JLabel());

  private Map images = new HashMap();

  private Map skinSourcesByName = new HashMap(); 
  
  private Map skinSourcesBySkin = new HashMap(); 

  private SkinManager() {
    
    initializeSkíns();
  }

  public static SkinManager instance() {
    return instance;
  }
  
  public void initializeSkíns() {

    File skinsDir = new File(System.getProperty(SKINS_PATH_PROPERTY, Bootstrap.getDirectory() + "/skins"));
    if (skinsDir.exists()) {
      String[] entries = skinsDir.list();
      for (int e = 0; e < entries.length; e++) {
        String entry = entries[e];
       
        File skinFile = new File(skinsDir, entry);
        try {
          SkinSource skinSource = null;
      
          if (skinFile.isDirectory()) {
            skinSource = new SkinDirectory(skinFile);
          } else if (skinFile.getName().endsWith(ZIP_SUFFIX)) {
            skinSource = new SkinZip(skinFile);
          }

          if (skinSource != null) {
            skinSourcesBySkin.put(skinSource.getSkin(), skinSource);
            skinSourcesByName.put(skinSource.getSkinName(), skinSource);
          }
        } catch (IOException ex) {
          logger.log(Level.FINE, "failed to load skin '" + entry + "'", ex);
        }
      }
    }
  }
    
  /**
   * Get the names of the available skins.
   * 
   * @return    skins
   */
  public String[] getSkinNames() {
    
    String[] names = new String[1 + skinSourcesByName.size()];

    Iterator iterator = skinSourcesByName.values().iterator();
    for (int n = 1; n < names.length; n++) {
      SkinSource skinSource = (SkinSource)iterator.next();
      names[n] = skinSource.getSkinName(); 
    }
    
    return names;
  }
  
  /**
   * Get the skin for the given name.
   * 
   * @param identifier name to get skin for
   * @return           skin or <code>null</code>
   */
  public Skin getSkin(String name) {
    if (name == null) {
      return null;
    }
    
    SkinSource skinSource = (SkinSource)skinSourcesByName.get(name);

    if (skinSource != null) {
      return skinSource.getSkin();
    } else {
      return null;  
    }
  }
  
  /**
   * Get the image for the given state.
   * 
   * @param image   image to get image for
   * @return        image
   */
  public java.awt.Image getImage(Skin skin, Image image) {
    if (image == null) {
      throw new IllegalArgumentException("image must not be null");
    }
    
    String fileName = image.getFile();

    SkinSource skinSource = (SkinSource)skinSourcesBySkin.get(skin);
    
    URL imgURL = skinSource.getURL(fileName);
    
    java.awt.Image img = (java.awt.Image)images.get(imgURL);
    if (img == null) {
      img = createImage(imgURL);
      
      if (!loadImage(img)) {
        img = createImage(getClass().getResource("img/missing.gif"));     
        loadImage(img); 
      }    
      
      images.put(imgURL, img);
    }

    return img;
  }

  /**
   * Create an image for the given URL.
   * 
   * @param url   url to create image from
   * @return      created image
   */
  private java.awt.Image createImage(URL url) {
    return Toolkit.getDefaultToolkit().createImage(url);
  }
  
  /**
   * Loads the image, returning only when the image is loaded.
   * 
   * @param image the image
   * @return      <code>true</code> if the image was correctly loaded
   */
  protected boolean loadImage(java.awt.Image image) {
    tracker.addImage(image, -1);
    try {
      tracker.waitForAll();
    } catch (InterruptedException e) {
      throw new Error("unexpected interruption");
    }
    boolean error = tracker.isErrorAny();
    tracker.removeImage(image);

    return !error;
  }

  
  /**
   * The source of a skin.
   */
  private abstract class SkinSource {

    protected Skin skin;

    /**
     * Read the skin from the given inputStream.
     * 
     * @param in            inputStream to read skin from 
     * @throws IOException
     */
    protected void readSkin(InputStream in) throws IOException {
      skin = (Skin)new SkinReader(in).read();
    }

    /**
     * Get the skin of this source.
     * 
     * @return    the skin
     */
    public Skin getSkin() {
      return skin;
    }

    /**
     * Get the name of the contained skin.
     * 
     * @return    the name of the skin
     */
    public abstract String getSkinName();

    /**
     * Get the URL for the given name.
     * 
     * @param name    name to get URL for
     * @return        URL
     */
    public URL getURL(String name) {
      try {
        return getURLImpl(name);
      } catch (MalformedURLException ex) {
        throw new Error("unexpected malformed url", ex);
      }
    }

    /**
     * Implementation method for the creation of the URL.
     */
    protected abstract URL getURLImpl(String name) throws MalformedURLException;
  }

  /**
   * A source of a skin contained in a directory.
   */
  private class SkinDirectory extends SkinSource {

    private File directory;

    public SkinDirectory(File directory) throws IOException {
      this.directory = directory;

      File skinFile = new File(directory, SKIN_FILE);
      readSkin(new FileInputStream(skinFile));
    }

    public String getSkinName() {
      return directory.getName();
    }

    protected URL getURLImpl(String name) throws MalformedURLException{
      return new File(directory, name).toURL();
    }
  }

  /**
   * A source of a skin contained in a zipFile.
   */
  private class SkinZip extends SkinSource {

    private File   file;
    private String name;
    
    public SkinZip(File file) throws IOException {
      this.file = file;

      ZipFile zipFile = new ZipFile(file);

      ZipEntry zipEntry = zipFile.getEntry(SKIN_FILE);
      if (zipEntry == null) {
          throw new IOException("missing " + SKIN_FILE);
      }
      readSkin(zipFile.getInputStream(zipEntry));
      
      name = file.getName();
      if (name.endsWith(ZIP_SUFFIX)) {
        name = name.substring(0, name.length() - ZIP_SUFFIX.length());
      }
    }

    public String getSkinName() {
      return name;
    }

    protected URL getURLImpl(String name) throws MalformedURLException {
      return new URL("jar:" + file.toURL() + "!/" + name);
    }
  }
}