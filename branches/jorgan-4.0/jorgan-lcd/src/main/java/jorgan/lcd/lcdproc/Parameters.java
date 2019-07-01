package jorgan.lcd.lcdproc;

import java.util.ArrayList;
import java.util.List;

public class Parameters {

	private List<Object> objects = new ArrayList<Object>();

	public Parameters() {
	}

	public Parameters(Object... objects) {
		for (Object object : objects) {
			this.objects.add(object);
		}
	}

	public Parameters append(Object... objects) {
		for (Object object : objects) {
			this.objects.add(object);
		}

		return this;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Object object : objects) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(object);
		}

		return builder.toString();
	}

}