package jorgan.spi;

import bias.store.CLIStore;

public interface OptionProvider {

	public void addOptions(CLIStore store);
}
