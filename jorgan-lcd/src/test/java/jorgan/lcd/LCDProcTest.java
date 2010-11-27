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

		Screen screen3 = proc.addScreen();
		BarWidget bar = screen3.addBar(4, 3, 8, true);

		Screen screen4 = proc.addScreen();
		BarWidget bar1 = screen4.addBar(4, 4, 4, false);
		BarWidget bar2 = screen4.addBar(6, 4, 4, false);
		BarWidget bar3 = screen4.addBar(8, 4, 4, false);
		BarWidget bar4 = screen4.addBar(10, 4, 4, false);

		while (true) {
			num.value((int) (Math.random() * 10));

			bar.value(Math.random());

			bar1.value(Math.random());
			bar2.value(Math.random());
			bar3.value(Math.random());
			bar4.value(Math.random());

			Thread.sleep(1000);
		}
	}
}