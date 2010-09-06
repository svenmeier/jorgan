/**
 * 
 */
package jorgan.midimapper.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.App;
import jorgan.midimapper.mapping.Mapping;

public class MappingFilenameFilter implements FilenameFilter {

	private static final Logger logger = Logger
			.getLogger(MappingFilenameFilter.class.getName());

	static final String SUFFIX = ".mapping";

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(SUFFIX);
	}

	public static List<Mapping> getMappings() {
		List<Mapping> mappings = new ArrayList<Mapping>();

		for (File file : App.getHome().listFiles(new MappingFilenameFilter())) {
			try {
				mappings.add(new MappingStream().read(file));
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}

		return mappings;
	}

}