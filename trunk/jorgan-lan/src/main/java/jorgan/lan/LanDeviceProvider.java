package jorgan.lan;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.lan.net.MessagePort;

/**
 * The provider for the LAN device.
 */
public class LanDeviceProvider extends MidiDeviceProvider {

	private static final LanDevice[] devices = new LanDevice[MessagePort.PORT_COUNT];
	{
		for (int i = 0; i < devices.length; i++) {
			devices[i] = new LanDevice(i, new Info("jOrgan LAN " + i, "jOrgan",
					"Midi over LAN", "1.0") {
			});
		}
	}

	@Override
	public MidiDevice.Info[] getDeviceInfo() {

		Info[] infos = new Info[devices.length];

		for (int i = 0; i < infos.length; i++) {
			infos[i] = devices[i].getDeviceInfo();
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