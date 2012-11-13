package jorgan.fluidsynth.windows;

import java.util.ArrayList;
import java.util.List;

public class Backend {

	private String name = "";

	private String description = "";

	private String version = "";

	private String maintainer = "";

	private List<Link> links = new ArrayList<Link>();

	private List<String> libraries = new ArrayList<String>();

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

	public List<Link> getLinks() {
		return links;
	}
}
