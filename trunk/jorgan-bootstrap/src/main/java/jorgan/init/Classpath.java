package jorgan.init;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import jorgan.util.ClassUtils;

public class Classpath {

	public Classpath() throws Exception {
		URL[] classpath = getClasspath();

		ClassLoader classloader = new URLClassLoader(classpath);
		Thread.currentThread().setContextClassLoader(classloader);
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
}
