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
package jorgan.swing;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * A cache of images.
 */
public class ImageCache {

	private static Map<String, Reference<BufferedImage>> images = new HashMap<String, Reference<BufferedImage>>();

	private static BufferedImage get(String key) {
		Reference<BufferedImage> reference = images.get(key);
		if (reference != null) {
			return reference.get();
		}
		return null;
	}

	private static void put(String key, BufferedImage image) {
		Reference<BufferedImage> reference = new SoftReference<BufferedImage>(
				image);
		images.put(key, reference);
	}

	/**
	 * Flush all cached images.
	 */
	public static void flush() {
		images.clear();
	}

	/**
	 * Get an image for the given URL.
	 * 
	 * @param url
	 *            url to get image for
	 * @param scale
	 *            scale
	 * @return image
	 */
	public static BufferedImage getImage(URL url, float scale)
			throws IOException {
		String key = "" + scale + ":" + url.toString();

		BufferedImage image = get(key);
		if (image == null) {
			image = deriveImage(getImage(url), scale);
			put(key, image);
		}
		return image;
	}

	private static BufferedImage getImage(URL url) throws IOException {
		String key = url.toString();
		BufferedImage image = get(key);
		if (image == null) {
			image = ImageIO.read(url);
			put(key, image);
		}
		return image;
	}

	private static BufferedImage deriveImage(BufferedImage image, float scale) {
		int scaledWidth = (int) (image.getWidth() * scale);
		int scaledHeight = (int) (image.getHeight() * scale);

		BufferedImage scaled = createImage(scaledWidth, scaledHeight);

		Graphics2D g2 = scaled.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
		g2.dispose();

		return scaled;
	}

	private static BufferedImage createImage(int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration configuration = gs.getDefaultConfiguration();

		return configuration.createCompatibleImage(width, height,
				Transparency.TRANSLUCENT);
	}
}