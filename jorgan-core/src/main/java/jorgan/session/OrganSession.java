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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import bias.Configuration;
import jorgan.Version;
import jorgan.disposition.Element.FastPropertyChange;
import jorgan.disposition.Organ;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganObserver;
import jorgan.disposition.spi.ElementRegistry;
import jorgan.io.DispositionStream;
import jorgan.io.disposition.Backup;
import jorgan.session.spi.SessionRegistry;
import jorgan.util.ShutdownHook;

/**
 * A session of interaction with an {@link Organ}.
 */
public class OrganSession {

	private static Logger logger = Logger.getLogger(OrganSession.class.getName());

	private static Configuration config = Configuration.getRoot().get(OrganSession.class);

	/**
	 * The file the current organ is associated with.
	 */
	private File file;

	private Organ organ;

	private List<SessionListener> listeners = new ArrayList<SessionListener>();

	private boolean modified = false;

	private boolean constructing = false;

	private int backupCount;

	private Map<Class<? extends Object>, Object> ts = new HashMap<Class<? extends Object>, Object>();

	private ShutdownHook shutdownHook;

	public OrganSession(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file must not be null");
		}
		this.file = file.getAbsoluteFile();

		if (file.exists()) {
			organ = new DispositionStream().read(file);
		} else {
			file.createNewFile();

			organ = createOrgan();
			markModified();
		}

		organ.addOrganObserver(new OrganObserver() {
			public void onChange(Change change) {
				if (change instanceof FastPropertyChange) {
					if (((FastPropertyChange) change).isDerived()) {
						// don't mark modified for derived changes
						return;
					}
				}

				markModified();
			}
		});

		SessionRegistry.init(this);

		config.read(this);

		new History().addRecentFile(file);
	}

	public int getBackupCount() {
		return backupCount;
	}

	public void setBackupCount(int count) {
		this.backupCount = count;
	}

	public void setSaveOnShutdown(boolean save) {
		if (save) {
			if (shutdownHook == null) {
				shutdownHook = new ShutdownHook(new Runnable() {
					public void run() {
						if (modified) {
							logger.log(Level.INFO, "save on shutdown");

							try {
								save();
							} catch (IOException ex) {
								logger.log(Level.WARNING, "unable to save on shutdown", ex);
							}
						}
					}
				});
			}
		} else {
			if (shutdownHook != null) {
				shutdownHook.release();
				shutdownHook = null;
			}
		}
	}

	public boolean isModified() {
		return modified;
	}

	public void markModified() {
		if (!modified) {
			modified = true;

			for (SessionListener listener : listeners) {
				listener.modified();
			}
		}
	}

	public void save() throws IOException {
		// TODO do this as SessionListener#beforeSave(file);
		new Backup(file).write(backupCount);

		organ.setVersion(new Version().get());

		new DispositionStream().write(organ, file);

		modified = false;

		new History().addRecentFile(file);

		for (SessionListener listener : listeners) {
			listener.saved(file);
		}
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
		if (!listeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
	}

	public Organ getOrgan() {
		return organ;
	}

	/**
	 * @throws IllegalArgumentException if clazz can not be looked up
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<T> clazz) {
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

	public String deresolve(File file) {
		if (file.isAbsolute()) {
			String directory = this.file.getParentFile().getAbsolutePath().replace('\\', '/');
			if (!directory.endsWith("/")) {
				directory += "/";
			}

			String path = file.getPath().replace('\\', '/');
			if (path.startsWith(directory)) {
				return "./" + path.substring(directory.length());
			}
		}

		return file.getPath();
	}

	public File resolve(String name) {
		File file = new File(name);

		if (!file.isAbsolute()) {
			file = new File(getFile().getParentFile(), name);
		}

		return file;
	}

	private Organ createOrgan() {
		Organ organ = new Organ();
		organ.setVersion(new Version().get());

		ElementRegistry.init(organ);

		return organ;
	}

	public void destroy() {
		for (SessionListener listener : listeners) {
			listener.destroyed();
		}

		if (shutdownHook != null) {
			shutdownHook.release();
		}
	}
}