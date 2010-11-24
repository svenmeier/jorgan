package jorgan.lcd;

import jorgan.lcd.LCDProc.Screen;
import jorgan.lcd.LCDProc.Screen.BarWidget;
import jorgan.lcd.LCDProc.Screen.NumWidget;
import jorgan.lcd.LCDProc.Screen.StringWidget;
import junit.framework.TestCase;

public class LCDProcTest extends TestCase {

	public void test() throws Exception {
		LCDProc proc = new LCDProc();

		Screen screen1 = proc.addScreen();

		StringWidget string = screen1.addString(4, 2);
		string.value("jOrgan");

		Screen screen2 = proc.addScreen();
		NumWidget num = screen2.addNum(1);
		num.value(8);

		Screen screen3 = proc.addScreen();
		BarWidget bar = screen3.addBar(4, 3, 8, true);
		bar.value(1.0d);

		while (true) {
			num.value((int) (Math.random() * 10));

			bar.value(Math.random());

			Thread.sleep(1000);
		}
	}
}