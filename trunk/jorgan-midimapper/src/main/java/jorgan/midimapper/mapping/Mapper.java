package jorgan.midimapper.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;

import jorgan.midi.mpl.Context;

public class Mapper {

	private transient int match = -1;

	private transient Context context;

	private List<Message> from = new ArrayList<Message>();

	private List<Message> to = new ArrayList<Message>();

	public void match(MidiMessage message, Callback callback) {
		if (from.get(match + 1).match(message)) {
			match++;
		} else {
			match = -1;
		}

		if (match == from.size() - 1) {
			match = -1;

			// TODO invoke callback
		}
	}
}
