package jorgan.lan;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import jorgan.lan.net.MessageReceiver;
import jorgan.midi.Loopback;

/**
 * A remote {@link MidiDevice} over LAN.
 */
public class ReceiveDevice extends Loopback implements IpMidi {

	private int index;

	private MessageReceiver port;

	public ReceiveDevice(int index, Info info) {
		super(info, false, true);

		this.index = index;
	}

	@Override
	public synchronized void open() throws MidiUnavailableException {
		super.open();

		try {
			port = new MessageReceiver(GROUP, PORT_BASE + index) {
				@Override
				protected void onReceived(ShortMessage message) {
					loopbackMessage(message, -1);
				}
			};
		} catch (Exception ex) {
			close();

			MidiUnavailableException exception = new MidiUnavailableException();
			exception.initCause(ex);
			throw exception;
		}
	}

	@Override
	public synchronized void close() {
		if (port != null) {
			port.close();
			port = null;
		}

		super.close();
	}
}