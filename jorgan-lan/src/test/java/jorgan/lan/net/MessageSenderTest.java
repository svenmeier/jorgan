package jorgan.lan.net;

import jorgan.lan.SendDevice;
import jorgan.lan.net.MessageSender;
import jorgan.midi.MessageUtils;
import junit.framework.TestCase;

/**
 * Test for {@link MessageSender}.
 */
public class MessageSenderTest extends TestCase {

	public void test() throws Exception {
		
		MessageSender sender = new MessageSender(SendDevice.GROUP, SendDevice.PORT_BASE);
		
		sender.send(MessageUtils.createMessage(144, 64, 100));
	}
}
