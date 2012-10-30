package jorgan.fluidsynth.windows;

import java.io.File;
import java.util.Collections;
import java.util.List;

import jorgan.util.NativeUtils;

public class Backend {

	private String name = "";

	private String description = "";

	private String version = "";

	private String maintainer = "";

	private List<String> libraries = Collections.emptyList();

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getVersion() {
		return version;
	}

	public String getMaintainer() {
		return maintainer;
	}

	public List<String> getLibraries() {
		return libraries;
	}

	public void load(File directory) throws UnsatisfiedLinkError {

		for (String library : libraries) {
			NativeUtils.load(new File(directory, library));
		}
	}
}
