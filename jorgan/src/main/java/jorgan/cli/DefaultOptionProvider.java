package jorgan.cli;

import jorgan.cli.spi.OptionProvider;
import bias.store.CLIStore;
import bias.util.cli.Option;

public class DefaultOptionProvider implements OptionProvider {

	public void addOptions(CLIStore store) {
		Option headless = store.addSwitch("jorgan/App/headless", 'l');
		headless.setLongName("headless");
		headless.setDescription("start without a graphical UI");

		Option noConstruct = store.addSwitch(
				"jorgan/session/OrganSession/sealed", 's');
		noConstruct.setLongName("sealed");
		noConstruct.setDescription("sealed");
	}
}
