package jorgan.midimapper.mapping;

import javax.sound.midi.MidiMessage;

public interface Callback {

	public void onMapped(MidiMessage message);
}
