package jorgan.midimerger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jorgan.App;
import jorgan.midimerger.io.MergingStream;
import jorgan.midimerger.merging.Merging;
import jorgan.spi.ConfigurationProvider;
import bias.Store;
import bias.store.FileSetStore;

public class MidiMergerConfigurationProvider implements ConfigurationProvider {

	public List<Store> getStores() {
		ArrayList<Store> stores = new ArrayList<Store>();

		stores.add(new MergingsStore());

		return stores;
	}

	private final class MergingsStore extends FileSetStore<Merging> implements
			FileFilter {

		private static final String SUFFIX = ".merger";

		private MergingStream stream = new MergingStream();

		private MergingsStore() {
			super("jorgan/midimerger/MidiMergerProvider/mergings");
		}

		@Override
		protected File getFile(Merging merging) {
			return new File(App.getHome(), merging.getName() + SUFFIX);
		}

		@Override
		protected File[] getFiles() {
			return App.getHome().listFiles(this);
		}

		@Override
		public boolean accept(File file) {
			return file.getName().endsWith(SUFFIX);
		}

		@Override
		protected Object read(InputStream input) throws IOException {
			return stream.read(input);
		}

		@Override
		protected void write(Merging merging, OutputStream output)
				throws IOException {
			stream.write(merging, output);
		}
	}
}
