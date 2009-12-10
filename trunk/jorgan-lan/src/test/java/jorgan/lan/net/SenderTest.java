package jorgan.lan.net;

import jorgan.lan.net.MessagePort;
import jorgan.midi.MessageUtils;
import junit.framework.TestCase;

/**
 * Test for {@link MessagePort}.
 */
public class SenderTest extends TestCase {

	public void test() throws Exception {
		
		MessagePort sender = new MessagePort(0);
		
		sender.send(MessageUtils.createMessage(144, 64, 100));
	}
}
