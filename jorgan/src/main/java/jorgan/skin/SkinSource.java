package jorgan.skin;

import java.net.URL;

/**
 * The source of a skin.
 */
public interface SkinSource {

    /**
     * Get the URL for the given name.
     * 
     * @param name
     *            name to get URL for
     * @return URL
     */
    public URL getURL(String name);
}