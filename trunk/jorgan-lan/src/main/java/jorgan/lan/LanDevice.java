package jorgan.lan;

import java.io.IOException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import jorgan.lan.net.MessagePort;
import jorgan.midi.Loopback;
import jorgan.midi.MessageUtils;

/**
 * A remote {@link MidiDevice} over LAN.
 */
public class LanDevice extends Loopback {

	private int index;

	private MessagePort port;

	public LanDevice(int index, Info info) {
		super(info, true, false);

		this.index = index;
	}

	@Override
	public synchronized void open() throws MidiUnavailableException {
		super.open();

		try {
			port = new MessagePort(index);

			probe();
		} catch (Exception ex) {
			close();

			MidiUnavailableException exception = new MidiUnavailableException();
			exception.initCause(ex);
			throw exception;
		}
	}

	private void probe() throws IOException {
		port.send(MessageUtils.newMessage(ShortMessage.ACTIVE_SENSING, 0, 0));
	}

	@Override
	public synchronized void close() {
		port.close();
		port = null;

		super.close();
	}

	@Override
	public synchronized void loopbackMessage(MidiMessage message, long timestamp) {
		if (message instanceof ShortMessage) {
			try {
				port.send((ShortMessage) message);
			} catch (IOException e) {
				// nothing we can do about it, receivers are expected to work
				// flawlessly, #probe() must have worked
			}
		}
	}
}