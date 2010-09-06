/**
 * 
 */
package jorgan.midimerger.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.App;
import jorgan.midimerger.merging.Merging;

public class MergingFilenameFilter implements FilenameFilter {

	private static final String SUFFIX = ".merging";

	private static final Logger logger = Logger
			.getLogger(MergingFilenameFilter.class.getName());

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(SUFFIX);
	}

	public static List<Merging> getMergings() {
		List<Merging> mergings = new ArrayList<Merging>();

		for (File file : App.getHome().listFiles(new MergingFilenameFilter())) {
			try {
				mergings.add(new MergingStream().read(file));
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}

		return mergings;
	}
}