package jorgan.gui;

import jorgan.UI;
import jorgan.spi.UIProvider;

public class GUIProvider implements UIProvider {

	@Override
	public UI getUI() {
		return new GUI();
	}
	
	@Override
	public int getOrder() {
		return 50;
	}
}
