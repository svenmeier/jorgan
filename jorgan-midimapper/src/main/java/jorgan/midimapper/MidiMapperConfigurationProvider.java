package jorgan.midimapper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jorgan.App;
import jorgan.midimapper.io.MappingStream;
import jorgan.midimapper.mapping.Mapping;
import jorgan.spi.ConfigurationProvider;
import bias.Store;
import bias.store.FileSetStore;

public class MidiMapperConfigurationProvider implements ConfigurationProvider {

	public List<Store> getStores() {
		ArrayList<Store> stores = new ArrayList<Store>();

		stores.add(new MappingsStore());

		return stores;
	}

	private final class MappingsStore extends FileSetStore<Mapping> implements
			FileFilter {

		private static final String SUFFIX = ".mapping";

		private MappingsStore() {
			super("jorgan/midimapper/MidiMapperProvider/mappings");
		}

		@Override
		protected File getFile(Mapping mapping) {
			return new File(App.getHome(), mapping.getName() + SUFFIX);
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
			return new MappingStream().read(input);
		}

		@Override
		protected void write(Mapping mapping, OutputStream output)
				throws IOException {
			new MappingStream().write(mapping, output);
		}
	}
}
