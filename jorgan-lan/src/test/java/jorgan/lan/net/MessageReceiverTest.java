package jorgan.lan.net;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import jorgan.lan.SendDevice;
import junit.framework.TestCase;

/**
 * Test for {@link MessageReceiver}.
 */
public class MessageReceiverTest extends TestCase {

	private boolean hasWarning;

	private boolean hasError;

	private boolean hasReceived;

	public void test() throws Exception {

		MessageReceiver receiver = new MessageReceiver(SendDevice.GROUP,
				SendDevice.PORT_BASE) {

			@Override
			protected void onReceived(ShortMessage message) {
				hasReceived = true;
			}

			@Override
			protected void onWarning(InvalidMidiDataException ex) {
				hasWarning = true;
			}

			@Override
			protected void onError(IOException ex) {
				hasError = true;
			}
		};

		Thread.sleep(500);

		receiver.close();

		Thread.sleep(500);

		assertFalse(hasReceived);
		assertFalse(hasWarning);
		assertFalse(hasError);
	}
}
