package jorgan.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;

/**
 * A protection against dead-locks caused by both {@link Receiver}s and non-Midi
 * threads trying to aquire a shared lock.
 * <p>
 * {@link MidiSystem} synchronizes on the list of {@link Receiver}s while
 * delivering {@link MidiMessage}s (lock A). If a {@link Receiver}'s code
 * synchronizes on another lock B, the following szenario will lead to a
 * dead-lock:
 * <ul>
 * <li>the {@link MidiSystem} prepares a {@link MidiMessage} holding lock A,</li>
 * <li>another thread aquires lock B to close the {@link MidiDevice},</li>
 * <li>closing the {@link MidiDevice} is blocked by lock A</li>
 * <li>the {@link Receiver}'s code is blocked by lock B</li>
 * </ul>
 * This gate should be used to lock out {@link Receiver}s before aquiring a
 * shared lock.
 * 
 * @see #guard(Receiver)
 */
public class MidiGate {

	private boolean open = false;

	/**
	 * Open the gate, allowing transmitters to enter.
	 */
	public synchronized void open() {
		this.open = true;
	}

	/**
	 * Close the gate, waiting for all currently entered transmitters to leave
	 * first.
	 */
	public synchronized void close() {
		this.open = false;
	}

	/**
	 * Wrap the given receiver, guarding the gate.
	 * 
	 * @see #open()
	 * @see #close()
	 */
	public Receiver guard(Receiver receiver) {
		return new ReceiverWrapper(receiver) {
			@Override
			public void send(MidiMessage message, long timeStamp) {
				synchronized (MidiGate.this) {
					if (open) {
						super.send(message, timeStamp);
					}
				}
			}
		};
	}
}