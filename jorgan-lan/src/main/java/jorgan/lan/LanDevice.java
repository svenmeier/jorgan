package jorgan.lan;

import java.io.IOException;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import jorgan.lan.net.MessagePort;
import jorgan.midi.Loopback;

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
		} catch (IOException ex) {
			close();

			MidiUnavailableException exception = new MidiUnavailableException();
			ex.initCause(ex);
			throw exception;
		}
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
				throw new IllegalStateException(e);
			}
		}
	}
}