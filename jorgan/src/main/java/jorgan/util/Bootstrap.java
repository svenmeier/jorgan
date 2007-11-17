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
package jorgan.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of utility methods supporting the boostrapping of an application.
 */
public class Bootstrap extends ThreadGroup implements Runnable {

	private final static String MANIFEST = "META-INF/MANIFEST.MF";

	private final static String BOOTSTRAP_CLASSPATH = "Bootstrap-classpath";

	private final static String BOOTSTRAP_CLASS = "Bootstrap-class";

	private static boolean bootstrapped = false;

	private static Logger logger = Logger.getLogger(Bootstrap.class.getName());

	private String[] args;

	private Bootstrap(String[] args) {
		super("bootstrap");

		this.args = args;
	}

	public void run() {
		try {
			Manifest manifest = getManifest();

			URL[] classpath = getClasspath(manifest);

			ClassLoader classloader = new URLClassLoader(classpath);
			Thread.currentThread().setContextClassLoader(classloader);

			Class<?> clazz = classloader.loadClass(getClass(manifest));

			Method method = clazz.getMethod("main",
					new Class[] { String[].class });
			method.invoke(null, new Object[] { args });
		} catch (Throwable t) {
			logger.log(Level.WARNING, "bootstrapping failed", t);
		}
	}

	/**
	 * Test is the current running program is bootstrapped, i.e. it was started
	 * through the main method of this class.
	 * 
	 * @return <code>true</code> if bootstrapped
	 */
	public static boolean isBootstrapped() {
		return bootstrapped;
	}

	/**
	 * Get the directory where the current running program is bootstrapped from.
	 * <br>
	 * If it is not bootstrapped (see {@link #isBootstrapped()}) the
	 * <em>current
	 * user working directory</em> (as denoted by the system
	 * property <code>user.dir<code>) is returned instead.
	 * 
	 * @return        the bootstrap directory
	 * @see #isBootstrapped()
	 */
	public static File getDirectory() {
		return getDirectory(Bootstrap.class);
	}

	/**
	 * Get the directory where given clazz in the current running program is
	 * bootstrapped from. <br>
	 * If it is not bootstrapped (see {@link #isBootstrapped()}) the
	 * <em>current
	 * user working directory</em> (as denoted by the system
	 * property <code>user.dir<code>) is returned instead.
	 * 
	 * @param  clazz  class to get bootstrap directory for
	 * @return        the bootstrap directory
	 * @see #isBootstrapped()
	 */
	public static File getDirectory(Class clazz) {
		if (isBootstrapped()) {
			try {
				// jar:file:/C:/Program
				// Files/FooMatic/./lib/foo.jar!/foo/Bar.class
				JarURLConnection jarCon = (JarURLConnection) getClassURL(clazz)
						.openConnection();

				// file:/C:/Program Files/FooMatic/./lib/foo.jar
				URL jarUrl = jarCon.getJarFileURL();

				// /C:/Program Files/FooMatic/./lib/foo.jar
				File jarFile = new File(URLDecoder.decode(jarUrl.getPath(),
						"UTF-8"));

				// /C:/Program Files/FooMatic/./lib
				return jarFile.getParentFile();
			} catch (Exception ex) {
				logger.log(Level.WARNING, "detecting bootstrap directory failed",
						ex);
			}
		}

		return new File(System.getProperty("user.dir"));
	}

	/**
	 * Get URL of the given class.
	 * 
	 * @param clazz
	 *            class to get URL for
	 * @return the URL this class was loaded from
	 */
	private static URL getClassURL(Class clazz) {
		String resourceName = "/" + clazz.getName().replace('.', '/')
				+ ".class";
		return clazz.getResource(resourceName);
	}

	/**
	 * Bootstrap with dynamically constructed classpath.
	 * 
	 * @param args	arguments
	 */
	public static void main(final String[] args) {

		bootstrapped = true;

		Bootstrap bootstrap = new Bootstrap(args);
		new Thread(bootstrap, bootstrap).start();
	}

	/**
	 * Get bootstrap class from manifest file information
	 */
	private static String getClass(Manifest mf) {
		String clazz = mf.getMainAttributes().getValue(BOOTSTRAP_CLASS);
		if (clazz == null || clazz.length() == 0) {
			throw new Error("No " + BOOTSTRAP_CLASS + " defined in " + MANIFEST);
		}

		return clazz;
	}

	/**
	 * Assemble classpath from manifest file information (optional).
	 */
	private static URL[] getClasspath(Manifest manifest)
			throws MalformedURLException {

		String classpath = manifest.getMainAttributes().getValue(
				BOOTSTRAP_CLASSPATH);
		if (classpath == null) {
			classpath = "";
		}

		StringTokenizer tokens = new StringTokenizer(classpath, ",", false);
		List<URL> urls = new ArrayList<URL>();
		while (tokens.hasMoreTokens()) {
			File file = new File(getDirectory(), tokens.nextToken());
			if (file.exists()) {
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						urls.add(files[i].toURI().toURL());
					}
				} else {
					urls.add(file.toURI().toURL());
				}
			}
		}

		return urls.toArray(new URL[urls.size()]);
	}

	/**
	 * Get our manifest file. Normally all (parent) classloaders of a class do
	 * provide resources and the enumeration returned on lookup of manifest.mf
	 * will start with the topmost classloader's resources. We're inverting that
	 * order to make sure we're consulting the manifest file in the same jar as
	 * this class if available.
	 */
	private static Manifest getManifest() throws IOException {

		// find all manifest files
		Stack<URL> manifests = new Stack<URL>();
		for (Enumeration<URL> e = Bootstrap.class.getClassLoader().getResources(
				MANIFEST); e.hasMoreElements();) {
			manifests.add(e.nextElement());
		}

		// it has to have the runnable attribute
		while (!manifests.isEmpty()) {
			URL url = manifests.pop();
			InputStream in = url.openStream();
			Manifest manifest = new Manifest(in);
			in.close();
			// careful with key here since Attributes.Name are used internally
			// by Manifest file
			if (manifest.getMainAttributes().getValue(BOOTSTRAP_CLASS) != null) {
				return manifest;
			}
		}

		// not found
		throw new Error("No " + MANIFEST + " with " + BOOTSTRAP_CLASS
				+ " found");
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (e instanceof ThreadDeath) {
			return;
		}
		logger.log(Level.WARNING, "uncaught exception", e);
	}
}