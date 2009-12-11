package jorgan.lan;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import bias.Configuration;

/**
 * The provider for the LAN device.
 */
public class LanDeviceProvider extends MidiDeviceProvider {

	private static Configuration config = Configuration.getRoot().get(
			LanDeviceProvider.class);

	private static final List<LanDevice> devices = new ArrayList<LanDevice>();

	private int count;

	public LanDeviceProvider() {
		config.read(this);
	}

	public void setCount(int count) {
		this.count = count;
		
		while (devices.size() < count) {
			devices.add(createDevice(devices.size()));
		}
	}

	private LanDevice createDevice(int index) {
		return new LanDevice(index, new Info("jOrgan LAN " + (index + 1),
				"jOrgan", "jOrgan Midi over LAN", "1.0") {
		});
	}

	@Override
	public MidiDevice.Info[] getDeviceInfo() {

		Info[] infos = new Info[count];

		for (int i = 0; i < count; i++) {
			infos[i] = devices.get(i).getDeviceInfo();
		}

		return infos;
	}

	@Override
	public MidiDevice getDevice(MidiDevice.Info info) {
		for (LanDevice device : devices) {
			if (device.getDeviceInfo() == info) {
				return device;
			}
		}

		return null;
	}
}