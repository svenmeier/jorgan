package jorgan.fluidsynth.gui.construct.editor;

import java.beans.PropertyEditorSupport;

import jorgan.fluidsynth.disposition.FluidsynthSound.Interpolate;

public class EnumEditor extends PropertyEditorSupport {
	public EnumEditor() {
	}

	@Override
	public String[] getTags() {
		Interpolate[] values = Interpolate.values();

		String[] tags = new String[values.length];
		for (int t = 0; t < tags.length; t++) {
			tags[t] = values[t].name();
		}

		return tags;
	}

	@Override
	public String getAsText() {
		return ((Interpolate) getValue()).name();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		super.setValue(Interpolate.valueOf(text));
	}
}