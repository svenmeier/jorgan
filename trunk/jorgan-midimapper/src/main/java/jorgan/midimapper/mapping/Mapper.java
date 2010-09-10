package jorgan.midimapper.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;

public class Mapper {

	private transient int match = 0;

	private transient Context context = new MapperContext();

	private List<Message> from = new ArrayList<Message>();

	private List<Message> to = new ArrayList<Message>();

	public Mapper() {
		from.add(new Message("get status", "get data1", "get data2"));
		to.add(new Message("set status", "set data1", "set data2"));
	}

	public void setFrom(List<Message> from) {
		if (from.isEmpty()) {
			throw new IllegalArgumentException("must not be empty");
		}

		this.from.clear();
		this.from.addAll(from);

		match = 0;
	}

	public List<Message> getFrom() {
		return Collections.unmodifiableList(from);
	}

	public void setTo(List<Message> to) {
		if (to.isEmpty()) {
			throw new IllegalArgumentException("must not be empty");
		}

		this.to.clear();
		this.to.addAll(to);

		match = 0;
	}

	public List<Message> getTo() {
		return Collections.unmodifiableList(to);
	}

	public void map(byte[] datas, Callback callback) throws ProcessingException {

		if (!from.isEmpty()) {
			if (from(from.get(match), datas)) {
				match++;
			} else {
				match = 0;
			}

			if (match == from.size()) {
				match = 0;

				to(callback);
			}
		}
	}

	private boolean from(Message message, byte[] datas)
			throws ProcessingException {
		if (context == null) {
			context = new MapperContext();
		}

		if (datas.length == message.getLength()) {
			for (int b = 0; b < datas.length; b++) {
				if (Float.isNaN(message.process(b, ((int) datas[b]) & 0xff,
						context))) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private void to(Callback callback) throws ProcessingException {
		if (context == null) {
			context = new MapperContext();
		}

		for (Message message : this.to) {
			byte[] datas = new byte[message.getLength()];

			for (int b = 0; b < datas.length; b++) {
				datas[b] = (byte) message.process(b, 0.0f, context);
			}

			callback.onMapped(datas);
		}
	}

	private class MapperContext implements Context {

		private Map<String, Float> map = new HashMap<String, Float>();

		@Override
		public float get(String name) {
			return map.get(name);
		}

		@Override
		public void set(String name, float value) {
			map.put(name, value);
		}
	}
}
