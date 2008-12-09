package jorgan.fluidsynth.gui.construct.editor;

import java.beans.PropertyEditorSupport;

public class DriverEditor extends PropertyEditorSupport {

	private String[] tags = { null, "alsa", "oss", "jack", "dsound", "sndman",
			"coreaudio", "portaudio" };

	@Override
	public String[] getTags() {
		return tags;
	}

	@Override
	public String getAsText() {

		return (String) getValue();
	}

	@Override
	public void setAsText(String string) {

		setValue(string);
	}
}