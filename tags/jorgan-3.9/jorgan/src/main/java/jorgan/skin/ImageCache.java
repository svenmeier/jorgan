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

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache of images.
 */
public class ImageCache {

	// shared Component for media tracking
	private static Component component = new Component() {
	};

	private static Map<URL, Image> images = new HashMap<URL, Image>();

	// private static Map scaled = new HashMap();

	/**
	 * Flush all cached images.
	 */
	public static void flush() {
		images.clear();

		// scaled.clear();
	}

	/**
	 * Get an image for the given URL.
	 * 
	 * @param url
	 *            url to get image for
	 * @return image
	 */
	public static Image getImage(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("url must not be null");
		}

		Image img = images.get(url);
		if (img == null) {
			img = createImage(url);

			loadImage(img);

			images.put(url, img);
		}

		return img;
	}

	// public static void drawImage(Graphics2D g, Image image, int dx1, int dy1,
	// int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Component
	// component) {
	// AffineTransform original = g.getTransform();
	//        
	// double scaleX = original.getScaleX();
	// double scaleY = original.getScaleY();
	//        
	// if (scaleX != 0.0d || scaleY != 0.0d) {
	// image = scaleImage(image, scaleX, scaleY, component);
	//            
	// g.setTransform(new AffineTransform(1.0d, original.getShearY(),
	// original.getShearX(), 1.0d, original.getTranslateX(),
	// original.getTranslateY()));
	// }
	//        
	// g.drawImage(image, (int)(dx1 * scaleX), (int)(dy1 * scaleY),
	// (int)Math.round(dx2 * scaleX), (int)Math.round(dy2 * scaleY), (int)(sx1 *
	// scaleX), (int)(sy1 * scaleY), (int)(sx2 * scaleX), (int)(sy2 * scaleY),
	// null);
	//        
	// if (scaleX != 0.0d || scaleY != 0.0d) {
	// g.setTransform(original);
	// }
	// }

	// private static Image scaleImage(Image image, double scaleX, double
	// scaleY, Component component) {
	// String key = scaleX + ":" + scaleY + ":" +
	// System.identityHashCode(image);
	//        
	// Image scaledInstance = (Image)scaled.get(key);
	// if (scaledInstance == null) {
	// scaledInstance = image.getScaledInstance((int)(image.getWidth(null) *
	// scaleX), (int)(image.getHeight(null) * scaleY), Image.SCALE_SMOOTH);
	//
	// loadImage(scaledInstance, component);
	//            
	// scaled.put(key, scaledInstance);
	// }
	//        
	// return scaledInstance;
	// }

	/**
	 * Create an image for the given URL.
	 * 
	 * @param url
	 *            url to create image from
	 * @return created image
	 */
	private static Image createImage(URL url) {
		return Toolkit.getDefaultToolkit().createImage(url);
	}

	/**
	 * Loads the image, returning only when the image is loaded.
	 * 
	 * @param image
	 *            the image
	 */
	private static void loadImage(Image image) {

		MediaTracker tracker = new MediaTracker(component);
		tracker.addImage(image, -1);
		try {
			tracker.waitForAll();
		} catch (InterruptedException e) {
			throw new Error("unexpected interruption");
		}
		tracker.removeImage(image);
	}
}