package jorgan.lan;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import jorgan.lan.net.MessageSender;
import jorgan.midi.Loopback;

/**
 * A remote {@link MidiDevice} over LAN.
 */
public class SendDevice extends Loopback implements IpMidi {

	private int index;

	private MessageSender port;

	public SendDevice(int index, Info info) {
		super(info, true, false);

		this.index = index;
	}

	@Override
	public synchronized void open() throws MidiUnavailableException {
		super.open();

		try {
			port = new MessageSender(GROUP, PORT_BASE + index);

			probe();
		} catch (Exception ex) {
			close();

			MidiUnavailableException exception = new MidiUnavailableException();
			exception.initCause(ex);
			throw exception;
		}
	}

	private void probe() throws IOException {
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(ShortMessage.ACTIVE_SENSING);
		} catch (InvalidMidiDataException e) {
			throw new Error(e);
		}
		port.send(message);
	}

	@Override
	public synchronized void close() {
		if (port != null) {
			port.close();
			port = null;
		}

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