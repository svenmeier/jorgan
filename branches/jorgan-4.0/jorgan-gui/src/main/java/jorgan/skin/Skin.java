/*
 * jOrgan - Java Virtual Pipe Organ
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A skin.
 */
public class Skin implements Resolver {

	private String name = "";

	private ArrayList<Style> styles = new ArrayList<Style>();

	private transient Resolver resolver;

	private transient Map<String, URL> urls;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			name = "";
		}
		this.name = name;
	}

	public List<Style> createStyles() {
		List<Style> clones = new ArrayList<Style>();

		for (int s = 0; s < styles.size(); s++) {
			Style style = styles.get(s);

			Style clone = (Style) style.clone();
			initResolver(clone);

			clones.add(clone);
		}

		return clones;
	}

	public void setResolver(Resolver resolver) {
		this.resolver = resolver;

		this.urls = new HashMap<String, URL>();
	}

	public Skin getSkin() {
		return this;
	}

	public Style createStyle(String styleName) {

		for (int s = 0; s < styles.size(); s++) {
			Style style = styles.get(s);
			if (style.getName().equals(styleName)) {
				Style clone = (Style) style.clone();

				initResolver(clone);

				return clone;
			}
		}
		return null;
	}

	private void initResolver(Layer layer) {
		layer.setResolver(this);

		if (layer instanceof CompositeLayer) {
			CompositeLayer compositeLayer = (CompositeLayer) layer;

			for (Layer child : compositeLayer.getChildren()) {
				initResolver(child);
			}
		}
	}

	public URL resolve(String name) {
		URL url = urls.get(name);
		if (url == null) {
			url = resolveImpl(name);
			urls.put(name, url);
		}
		return url;
	}

	private URL resolveImpl(String name) {
		String locale = "_" + Locale.getDefault().toString();

		int suffix = name.lastIndexOf('.');
		if (suffix == -1) {
			suffix = name.length();
		}

		while (true) {
			String localized = name.substring(0, suffix) + locale
					+ name.substring(suffix);

			URL url = resolver.resolve(localized);
			if (url != null) {
				return url;
			}

			if (locale.length() == 0) {
				return null;
			} else {
				locale = locale.substring(0, locale.lastIndexOf('_'));
			}
		}
	}
}