package jorgan.customizer.builder;

import javax.sound.midi.ShortMessage;

import jorgan.disposition.Message;
import jorgan.midi.MessageUtils;

public class NotesBuilder {

	private int channel = -1;

	private int from = Integer.MAX_VALUE;

	private int to = 0;

	public boolean analyse(byte[] datas) {

		int status = datas[Message.STATUS] & 0xff;
		if (MessageUtils.isChannelStatus(status)) {
			int command = status & 0xf0;
			int channel = status & 0x0f;
			if (command == ShortMessage.NOTE_ON
					|| command == ShortMessage.NOTE_OFF
					|| command == ShortMessage.POLY_PRESSURE) {

				if (this.channel == -1) {
					this.channel = channel;
				}

				if (channel == this.channel) {
					from = Math.min(from, datas[Message.DATA1] & 0xff);
					to = Math.max(to, datas[Message.DATA1] & 0xff);
				}
			}
		}
		return true;
	}

	public int getChannel() {
		return channel;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
}