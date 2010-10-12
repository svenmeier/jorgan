package jorgan.fluidsynth.gui.construct.editor;

import java.beans.PropertyEditorSupport;
import java.util.List;

import jorgan.disposition.Element;
import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.gui.construct.editor.ElementAwareEditor;

public class AudioDeviceEditor extends PropertyEditorSupport implements
		ElementAwareEditor {

	private FluidsynthSound sound;

	public void setElement(Element element) {

		if (element instanceof FluidsynthSound) {
			this.sound = (FluidsynthSound) element;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String[] getTags() {
		String[] tags = new String[1];

		String audioDriver = sound.getAudioDriver();
		if (audioDriver != null) {
			try {
				// FIXME if audioDriver is non-running JACK, the following call
				// will
				// lock-up the system !
				List<String> devices = Fluidsynth.getAudioDevices(audioDriver);

				tags = new String[devices.size() + 1];
				int i = 1;
				for (String device : devices) {
					tags[i] = device;
					i++;
				}
			} catch (NoClassDefFoundError error) {
			}
		}

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