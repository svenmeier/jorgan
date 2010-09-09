package jorgan.midimapper;

import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.midimapper.mapping.Mapping;
import bias.Configuration;

public class MidiMapperProvider extends MidiDeviceProvider {

	private static final String PREFIX = "jOrgan ";

	private static Configuration config = Configuration.getRoot().get(
			MidiMapperProvider.class);

	private Set<Mapping> mappings = new HashSet<Mapping>();

	public MidiMapperProvider() {
		config.read(this);
	}

	public void setMappings(Set<Mapping> mappings) {
		this.mappings = mappings;
	}

	public Set<Mapping> getMappings() {
		return mappings;
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
			super(PREFIX + mapping.getName(), "jOrgan",
					"Midi-Mapper of jOrgan", "1.0");

			this.mapping = mapping;
		}

		public Mapping getMapping() {
			return mapping;
		}
	}

	public boolean isMapper(String device) {
		for (Mapping mapping : mappings) {
			if ((PREFIX + mapping.getName()).equals(device)) {
				return true;
			}
		}
		return false;
	}
}
