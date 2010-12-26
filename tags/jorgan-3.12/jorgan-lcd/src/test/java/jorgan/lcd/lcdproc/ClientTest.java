package jorgan.lcd.lcdproc;

import jorgan.lcd.lcdproc.Client.Screen;
import jorgan.lcd.lcdproc.Client.Screen.HBarWidget;
import jorgan.lcd.lcdproc.Client.Screen.NumWidget;
import jorgan.lcd.lcdproc.Client.Screen.StringWidget;
import jorgan.lcd.lcdproc.Client.Screen.VBarWidget;
import junit.framework.TestCase;

/**
 * Test for {@link Client}.
 */
public class ClientTest extends TestCase {

	public void test() throws Exception {
		Client client = new Client();

		Screen screen1 = client.addScreen();
		StringWidget string = screen1.addString(4, 2);
		string.value("jOrgan");

		Screen screen2 = client.addScreen();
		NumWidget num = screen2.addNum(1);

		Screen screen3 = client.addScreen();
		HBarWidget hbar = screen3.addHBar(4, 3, 8);

		Screen screen4 = client.addScreen();
		VBarWidget vbar1 = screen4.addVBar(4, 4, 4);
		VBarWidget vbar2 = screen4.addVBar(6, 4, 4);
		VBarWidget vbar3 = screen4.addVBar(8, 4, 4);
		VBarWidget vbar4 = screen4.addVBar(10, 4, 4);

		while (true) {
			num.value((int) (Math.random() * 10));

			hbar.value(Math.random());

			vbar1.value(Math.random());
			vbar2.value(Math.random());
			vbar3.value(Math.random());
			vbar4.value(Math.random());

			Thread.sleep(1000);
		}
	}
}