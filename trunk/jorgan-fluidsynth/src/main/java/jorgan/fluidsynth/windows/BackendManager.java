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

	private File directory() {
		File directory;
		String path = System.getProperty(BACKEND_PATH);
		if (path == null) {
			directory = new File(ClassUtils.getDirectory(Fluidsynth.class),
					"fluidsynth");
		} else {
			directory = new File(path);
		}

		return new File(directory, backend);
	}

	public Backend getCurrentBackend() {
		if (backend != null) {
			try {
				return new BackendStream().read(new File(directory(),
						"backend.xml"));
			} catch (IOException ex) {
				logger.log(Level.INFO, "fluidsynth can not read backend", ex);
			}
		}

		return null;
	}

	public List<Backend> getAudios() {
		return new ArrayList<Backend>();
	}

	public static void loadLibraries() throws UnsatisfiedLinkError {
		BackendManager manager = new BackendManager();

		Backend current = manager.getCurrentBackend();
		if (current != null) {
			current.load(manager.directory());
		}
	}
}