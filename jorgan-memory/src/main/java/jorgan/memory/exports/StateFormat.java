package jorgan.memory.exports;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class StateFormat extends Format {

	private Format valueFormat = NumberFormat.getPercentInstance();

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {

		if (obj instanceof Float) {
			valueFormat.format(obj, toAppendTo, pos);
		} else if (obj instanceof Boolean) {
			toAppendTo.append((Boolean) obj ? "+" : "-");
		} else {
			throw new IllegalArgumentException();
		}

		return toAppendTo;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		throw new UnsupportedOperationException();
	}
}