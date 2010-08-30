package jorgan.midimapper.mapping;

import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;

public class Message {

	private String[] datas = new String[0];

	private Command[] commands;

	public Message() {
	}

	public Message(String... datas) {
		this.datas = datas;
	}

	public int getLength() {
		return datas.length;
	}

	public float process(int index, float data, Context context)
			throws ProcessingException {
		if (commands == null) {
			commands = new Command[datas.length];
			for (int d = 0; d < datas.length; d++) {
				commands[d] = Command.create(datas[d]);
			}
		}

		return commands[index].process(data, context);
	}
}
