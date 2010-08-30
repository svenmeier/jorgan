package jorgan.midimapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.midimapper.io.MappingStream;
import jorgan.midimapper.mapping.Mapping;
import bias.Configuration;

public class MidiMapperProvider extends MidiDeviceProvider {

	private static final Logger logger = Logger
			.getLogger(MidiMapperProvider.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			MidiMapperProvider.class);

	private List<Mapping> mappings = new ArrayList<Mapping>();

	public MidiMapperProvider() {
		config.read(this);
	}

	public void setMappings(List<File> files) {
		for (File file : files) {
			try {
				mappings.add(new MappingStream().read(file));
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	public List<File> getMappings() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Info[] getDeviceInfo() {
		Info[] infos = new Info[mappings.size()];

		int i = 0;
		for (Mapping mapping : mappings) {
			infos[i] = new MapperInfo(mapping);
			i++;
		}

		return infos;
	}

	@Override
	public boolean isDeviceSupported(Info info) {
		return info instanceof MapperInfo;
	}

	@Override
	public MidiDevice getDevice(Info info) {
		if (info instanceof MapperInfo) {
			MapperInfo mapperInfo = (MapperInfo) info;

			return new MidiMapper(info, mapperInfo.getMapping());
		}
		return null;
	}

	private class MapperInfo extends Info {

		private Mapping mapping;

		protected MapperInfo(Mapping mapping) {
			super("Mapper " + mapping.getName(), "jOrgan",
					"Midi-Mapper of jOrgan", "1.0");

			this.mapping = mapping;
		}

		public Mapping getMapping() {
			return mapping;
		}
	}
}
