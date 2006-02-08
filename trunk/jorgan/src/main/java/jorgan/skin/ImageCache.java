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

    protected static MediaTracker tracker = new MediaTracker(new Component() {
    });

    private static Map images = new HashMap();

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
     * @return image
     */
    public static Image getImage(URL url) {

        Image img = (Image) images.get(url);
        if (img == null) {
            img = createImage(url);

            if (!loadImage(img)) {
                img = createImage(ImageCache.class
                        .getResource("img/missing.gif"));
                loadImage(img);
            }

            images.put(url, img);
        }

        return img;
    }

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
     * @return <code>true</code> if the image was correctly loaded
     */
    private static boolean loadImage(java.awt.Image image) {
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
}