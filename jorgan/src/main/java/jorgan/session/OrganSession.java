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
package jorgan.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jorgan.disposition.Console;
import jorgan.disposition.Organ;
import jorgan.session.spi.SessionRegistry;

/**
 * A session of interaction with an {@link Organ}.
 */
public class OrganSession {

	/**
	 * The file the current organ is associated with.
	 */
	private File file;

	private Organ organ;

	private List<SessionListener> listeners = new ArrayList<SessionListener>();

	private boolean constructing = false;

	private Map<Class<? extends Object>, Object> ts = new HashMap<Class<? extends Object>, Object>();

	public OrganSession() {
		this(createDefaultOrgan());
	}

	public OrganSession(Organ organ) {
		this(organ, null);
	}

	public OrganSession(Organ organ, File file) {
		if (organ == null) {
			throw new IllegalArgumentException("organ must not be null");
		}
		this.organ = organ;
		this.file = file;
	}

	public void setFile(File file) {
		if (this.file != null && file == null) {
			throw new IllegalArgumentException("file cannot be set to null");
		}

		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public boolean isConstructing() {
		return constructing;
	}

	public void setConstructing(boolean constructing) {
		if (constructing != this.constructing) {
			this.constructing = constructing;

			for (SessionListener listener : listeners) {
				listener.constructingChanged(constructing);
			}
		}
	}

	public void addListener(SessionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SessionListener listener) {
		listeners.remove(listener);
	}

	public Organ getOrgan() {
		return organ;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		T t = (T) ts.get(clazz);
		if (t == null) {
			t = (T) SessionRegistry.create(this, clazz);
			if (t == null) {
				throw new IllegalArgumentException();
			} else {
				ts.put(clazz, t);
			}
		}
		return t;
	}

	public File resolve(String name) throws IOException {
		File file = new File(name);

		if (!file.isAbsolute()) {
			if (getFile() == null) {
				file = null;
			} else {
				file = new File(getFile().getParentFile(), name);
			}
		}

		if (file != null) {
			file = file.getCanonicalFile();
		}

		if (file == null || !file.exists()) {
			throw new FileNotFoundException();
		}

		return file;
	}

	private static Organ createDefaultOrgan() {
		Organ organ = new Organ();

		organ.addElement(new Console());

		return organ;
	}

	public void destroy() {
		for (SessionListener listener : listeners) {
			listener.destroyed();
		}
	}
}