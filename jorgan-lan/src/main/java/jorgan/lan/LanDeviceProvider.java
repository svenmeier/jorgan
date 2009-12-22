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

	private static final List<SendDevice> senders = new ArrayList<SendDevice>();

	private static final List<ReceiveDevice> receivers = new ArrayList<ReceiveDevice>();

	private int senderCount;

	private int receiverCount;

	public LanDeviceProvider() {
		config.read(this);
	}

	public void setSenderCount(int count) {
		this.senderCount = count;

		ensureSenders(count);
	}

	public void setReceiverCount(int count) {
		this.receiverCount = count;

		ensureReceivers(count);
	}

	@Override
	public MidiDevice.Info[] getDeviceInfo() {

		ArrayList<Info> infos = new ArrayList<Info>();

		for (int i = 0; i < senderCount; i++) {
			infos.add(senders.get(i).getDeviceInfo());
		}

		for (int i = 0; i < receiverCount; i++) {
			infos.add(receivers.get(i).getDeviceInfo());
		}

		return infos.toArray(new Info[infos.size()]);
	}

	@Override
	public MidiDevice getDevice(MidiDevice.Info info) {
		for (SendDevice sender : senders) {
			if (sender.getDeviceInfo() == info) {
				return sender;
			}
		}

		for (ReceiveDevice receiver : receivers) {
			if (receiver.getDeviceInfo() == info) {
				return receiver;
			}
		}

		return null;
	}

	private static synchronized void ensureSenders(int count) {
		while (senders.size() < count) {
			senders.add(createSender(senders.size()));
		}
	}

	private static synchronized void ensureReceivers(int count) {
		while (receivers.size() < count) {
			receivers.add(createReceiver(receivers.size()));
		}
	}

	private static SendDevice createSender(int index) {
		return new SendDevice(index, new Info("jOrgan LAN " + (index + 1),
				"jOrgan", "jOrgan Midi over LAN", "1.0") {
		});
	}

	private static ReceiveDevice createReceiver(int index) {
		return new ReceiveDevice(index, new Info("jOrgan LAN " + (index + 1),
				"jOrgan", "jOrgan Midi over LAN", "1.0") {
		});
	}
}