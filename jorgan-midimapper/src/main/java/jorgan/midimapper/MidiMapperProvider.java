package jorgan.midimapper;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.midi.Direction;
import jorgan.midimapper.mapping.Mapping;

public class MidiMapperProvider extends MidiDeviceProvider {

	/**
	 * TODO where to get mappings from?
	 */
	private List<Mapping> mappings = new ArrayList<Mapping>();

	public MidiMapperProvider() {
		mappings.add(new Mapping("Mapped In", Direction.IN, "jOrgan Keyboard"));
		mappings.add(new Mapping("Mapped Out", Direction.OUT,
				"Java Sound Synthesizer"));
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
