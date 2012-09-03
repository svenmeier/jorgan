package jorgan.exporter.exports;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;

public abstract class NamingWriter {

	private boolean useDescriptionName;

	protected String getName(Element element) {
		if (useDescriptionName) {
			return Elements.getDescriptionName(element);
		} else {
			return Elements.getDisplayName(element);
		}
	}

	public void setUseDescriptionName(boolean useDescriptionName) {
		this.useDescriptionName = useDescriptionName;
	}

}
