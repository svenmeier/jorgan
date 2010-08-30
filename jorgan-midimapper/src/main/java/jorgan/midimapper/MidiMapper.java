package jorgan.midimapper;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.Loopback;
import jorgan.midi.MessageUtils;
import jorgan.midi.MidiGate;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midimapper.mapping.Callback;
import jorgan.midimapper.mapping.Mapping;

public class MidiMapper extends Loopback {

	private MidiGate gate = new MidiGate();

	private Mapping mapping;

	private MidiDevice device;

	private Out out;

	public MidiMapper(Info info, Mapping mapping) {
		super(info, mapping.getDirection() == Direction.OUT, mapping
				.getDirection() == Direction.IN);

		this.mapping = mapping;
	}

	@Override
	public void open() throws MidiUnavailableException {
		super.open();

		gate.open();
	}

	@Override
	protected synchronized void openImpl() throws MidiUnavailableException {
		if (mapping.getDevice() != null) {
			device = DevicePool.instance().getMidiDevice(mapping.getDevice(),
					mapping.getDirection());
			device.open();

			if (mapping.getDirection() == Direction.IN) {
				new In();
			} else {
				out = new Out();
			}
		}
	}

	@Override
	public void close() {
		gate.close();

		super.close();
	}

	@Override
	protected synchronized void closeImpl() {
		if (device != null) {
			device.close();
		}

		super.closeImpl();
	}

	@Override
	protected void onLoopIn(MidiMessage message) {
		try {
			mapping.map(message.getMessage(), out);
		} catch (ProcessingException todo) {
			todo.printStackTrace();
		}
	}

	private class Out implements Callback {

		private Receiver receiver;

		public Out() throws MidiUnavailableException {
			receiver = device.getReceiver();
		}

		@Override
		public void onMapped(byte[] datas) {
			try {
				receiver.send(MessageUtils.createMessage(datas), -1);
			} catch (InvalidMidiDataException todo) {
				todo.printStackTrace();
			}
		}
	}

	private class In implements Receiver, Callback {

		public In() throws MidiUnavailableException {
			device.getTransmitter().setReceiver(gate.guard(this));
		}

		@Override
		public void send(MidiMessage message, long timeStamp) {
			try {
				mapping.map(message.getMessage(), this);
			} catch (ProcessingException todo) {
				todo.printStackTrace();
			}
		}

		@Override
		public void close() {
		}

		@Override
		public void onMapped(byte[] datas) {
			try {
				loopOut(MessageUtils.createMessage(datas));
			} catch (InvalidMidiDataException todo) {
				todo.printStackTrace();
			}
		}
	}
}
