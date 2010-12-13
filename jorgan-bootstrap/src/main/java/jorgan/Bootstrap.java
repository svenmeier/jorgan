package jorgan;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jorgan.util.ClassUtils;
import jorgan.util.IOUtils;

/**
 * Collection of utility methods supporting the boostrapping of an application.
 */
public class Bootstrap extends ThreadGroup implements Runnable {

	private static Logger logger = Logger.getLogger(Bootstrap.class.getName());

	private String[] args;

	private Bootstrap(String[] args) {
		super("bootstrap");

		this.args = args;
	}

	public void run() {
		try {
			initLogging();			

			URL[] classpath = getClasspath();

			ClassLoader classloader = new URLClassLoader(classpath);
			Thread.currentThread().setContextClassLoader(classloader);

			Class<?> clazz = classloader.loadClass("jorgan.App");

			Method method = clazz.getMethod("main",
					new Class[] { String[].class });
			method.invoke(null, new Object[] { args });
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "bootstrapping failed", t);
		}
	}

	/**
	 * Bootstrap with dynamically constructed classpath.
	 * 
	 * @param args
	 *            arguments
	 */
	public static void main(final String[] args) {

		Bootstrap bootstrap = new Bootstrap(args);
		new Thread(bootstrap, bootstrap).start();
	}

	private URL[] getClasspath() throws MalformedURLException {

		List<URL> urls = new ArrayList<URL>();

		File directory = ClassUtils.getDirectory(getClass());
		File file = new File(directory, "lib");
		if (file.exists()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				urls.add(files[i].toURI().toURL());
			}
		}

		return urls.toArray(new URL[urls.size()]);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (e instanceof ThreadDeath) {
			return;
		}
		logger.log(Level.WARNING, "uncaught exception", e);
	}

	private void initLogging() throws IOException {
		File home = new File(System.getProperty("user.home"), ".jorgan");
		if (!home.exists()) {
			home.mkdirs();
		}

		File logging = new File(home, "logging.properties");
		if (!logging.exists()) {
			InputStream input = getClass().getResourceAsStream(
					"logging.properties");

			OutputStream output = null;
			try {
				output = new FileOutputStream(logging);

				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
			}
		}

		FileInputStream input = null;
		try {
			input = new FileInputStream(logging);
			LogManager.getLogManager().readConfiguration(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}