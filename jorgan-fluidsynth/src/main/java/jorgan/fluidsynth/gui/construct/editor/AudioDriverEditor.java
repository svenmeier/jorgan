package jorgan.fluidsynth.gui.construct.editor;

import java.beans.PropertyEditorSupport;
import java.util.List;

import jorgan.fluidsynth.Fluidsynth;

public class AudioDriverEditor extends PropertyEditorSupport {

	private static String[] tags = new String[1];

	static {
		List<String> drivers = Fluidsynth.getAudioDrivers();
		
		tags = new String[drivers.size() + 1];
		int i = 1; 
		for (String driver : drivers) {
			tags[i] = driver;
			i++;
		}			
	}

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