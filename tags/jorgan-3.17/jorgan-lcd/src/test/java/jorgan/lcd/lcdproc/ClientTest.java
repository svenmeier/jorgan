package jorgan.lcd.lcdproc;

import jorgan.lcd.lcdproc.IconWidget.Icon;
import junit.framework.TestCase;

/**
 * Test for {@link Client}.
 */
public class ClientTest extends TestCase {

	public void test() throws Exception {
		Client client = new Client();

		Screen screen1 = client.addScreen();
		StringWidget string = new StringWidget(screen1, 4, 2);
		string.value("Hi jOrgan");

		Screen screen2 = client.addScreen();
		NumWidget num = new NumWidget(screen2, 1);

		Screen screen3 = client.addScreen();
		HBarWidget hbar = new HBarWidget(screen3, 4, 3, 8);

		Screen screen4 = client.addScreen();
		VBarWidget vbar1 = new VBarWidget(screen4, 4, 4, 4);
		VBarWidget vbar2 = new VBarWidget(screen4, 6, 4, 4);
		VBarWidget vbar3 = new VBarWidget(screen4, 8, 4, 4);
		VBarWidget vbar4 = new VBarWidget(screen4, 10, 4, 4);

		Screen screen5 = client.addScreen();
		IconWidget icon = new IconWidget(screen5, 2, 2);

		while (true) {
			num.value((int) (Math.random() * 10));

			hbar.value(Math.random());

			vbar1.value(Math.random());
			vbar2.value(Math.random());
			vbar3.value(Math.random());
			vbar4.value(Math.random());

			icon.value(Math.random() > 0.5d ? Icon.CHECKBOX_OFF
					: Icon.CHECKBOX_ON);

			Thread.sleep(1000);
		}
	}
}