package jorgan.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class PadFormat extends Format {

	private char pad;

	private int length;

	public PadFormat(int length) {
		this(length, ' ');
	}

	public PadFormat(int length, char pad) {
		this.length = length;
		this.pad = pad;
	}

	@Override
	public StringBuffer format(Object object, StringBuffer buffer,
			FieldPosition pos) {

		String string = object.toString();

		int length = string.length();
		while (length < this.length) {
			buffer.append(pad);
			length++;
		}

		buffer.append(string);

		return buffer;
	}

	@Override
	public Object parseObject(String string, ParsePosition pos) {
		throw new UnsupportedOperationException();
	}
}
