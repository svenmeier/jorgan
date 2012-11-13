package jorgan.fluidsynth.windows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.io.BackendStream;
import jorgan.util.ClassUtils;
import jorgan.util.NativeUtils;
import bias.Configuration;

public class BackendManager {

	public static final String BACKEND_PATH = "jorgan.fluidsynth.backend.path";

	private static final Logger logger = Logger.getLogger(BackendManager.class
			.getName());

	private static final Configuration config = Configuration.getRoot().get(
			BackendManager.class);

	private String backend;

	public BackendManager() {
		config.read(this);
	}

	public void setBackend(String backend) {
		this.backend = backend;
	}

	public String getBackend() {
		return backend;
	}

	public List<String> getBackends() {
		ArrayList<String> backends = new ArrayList<String>();

		backends.add(null);

		File baseDirectory = baseDirectory();
		if (baseDirectory.exists()) {
			for (File file : baseDirectory.listFiles()) {
				if (file.isDirectory()) {
					backends.add(file.getName());
				}
			}
		}

		return backends;
	}

	private File baseDirectory() {
		File directory;
		String path = System.getProperty(BACKEND_PATH);
		if (path == null) {
			directory = new File(ClassUtils.getDirectory(Fluidsynth.class),
					"fluidsynth");
		} else {
			directory = new File(path);
		}

		return directory;
	}

	private File directory(String backend) {
		return new File(baseDirectory(), backend);
	}

	private Backend read(String backend) throws IOException {
		return new BackendStream().read(new File(directory(backend),
				"backend.xml"));
	}

	public Backend getInstance(String backend) {
		try {
			return read(backend);
		} catch (IOException ex) {
			logger.log(Level.INFO,
					String.format("backend failure '%s'", backend), ex);

			return null;
		}
	}

	public void loadLibraries() throws UnsatisfiedLinkError {
		if (backend != null) {
			Backend instance = getInstance(backend);
			if (instance == null) {
				throw new UnsatisfiedLinkError(String.format(
						"unknown backend '%s'", backend));
			}

			for (String library : instance.getLibraries()) {
				NativeUtils.load(getFile(backend, library));
			}
		}
	}

	public File getFile(String backend, String file) {
		return new File(directory(backend), file);
	}
}