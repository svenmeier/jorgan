/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import bias.ConfigurationException;
import bias.Store;

/**
 * A {@link Store} for a single key storing a {@link Set} of values in
 * {@link File}s.
 */
public abstract class FileSetStore<T> extends AbstractStore {

	private String key;

	public FileSetStore(String key) {
		this.key = key;
	}

	@Override
	protected Set<String> getKeysImpl(String path) {
		if (getPath(key).equals(path)) {
			Set<String> keys = new HashSet<String>();
			keys.add(key);
			return keys;
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	protected Set<T> getValueImpl(String key, Type type) {
		Set<T> objects = new HashSet<T>();

		for (File file : getFiles()) {
			objects.add(read(file));
		}

		return objects;
	}

	@SuppressWarnings("unchecked")
	protected T read(File file) {
		InputStream input = null;

		try {
			input = new FileInputStream(file);

			return (T) read(input);
		} catch (IOException ex) {
			throw new ConfigurationException(ex);
		} finally {
			try {
				input.close();
			} catch (IOException ignore) {
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValueImpl(String key, Type type, Object value) {
		for (File file : getFiles()) {
			file.delete();
		}

		Set<T> objects = (Set<T>) value;

		for (T object : objects) {
			write(object, getFile(object));
		}
	}

	protected void write(T object, File file) {
		OutputStream output = null;

		try {
			output = new FileOutputStream(file);

			write(object, output);
		} catch (IOException ex) {
			throw new ConfigurationException(ex);
		} finally {
			try {
				output.close();
			} catch (IOException ignore) {
			}
		}
	}

	protected abstract File[] getFiles();

	protected abstract File getFile(T object);

	protected abstract Object read(InputStream input) throws IOException;

	protected abstract void write(T object, OutputStream output)
			throws IOException;
}