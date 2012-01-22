package jorgan.cli;

import jorgan.UI;
import jorgan.spi.UIProvider;
import bias.Configuration;

public class CLIProvider implements UIProvider {

	private static Configuration configuration = Configuration.getRoot().get(
			CLIProvider.class);

	private boolean headless;

	public CLIProvider() {
		configuration.read(this);
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	@Override
	public UI getUI() {
		return new CLI();
	}

	@Override
	public int getOrder() {
		if (headless) {
			return 0;
		} else {
			return 100;
		}
	}
}
