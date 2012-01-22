package jorgan.cli;

import jorgan.cli.spi.OptionProvider;
import bias.store.CLIStore;
import bias.util.cli.Option;

public class DefaultOptionProvider implements OptionProvider {

	public void addOptions(CLIStore store) {
		Option headless = store.addSwitch("jorgan/cli/CLIProvider/headless",
				'l');
		headless.setLongName("headless");
		headless.setDescription("start without a graphical UI");

		Option encoding = store.addInput("jorgan/cli/CLI/encoding", 'e', false,
				String.class);
		encoding.setLongName("encoding");
		encoding.setDescription("character encoding");
	}
}
