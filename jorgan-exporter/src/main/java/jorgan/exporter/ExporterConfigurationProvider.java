package jorgan.exporter;


import java.util.ArrayList;
import java.util.List;

import jorgan.spi.ConfigurationProvider;
import bias.Store;
import bias.store.DefaultingStore;
import bias.store.PreferencesStore;
import bias.store.PropertiesStore;

public class ExporterConfigurationProvider implements ConfigurationProvider {

	public List<Store> getStores() {
		ArrayList<Store> stores = new ArrayList<Store>();

		stores.add(new DefaultingStore(PreferencesStore.user(),
				new PropertiesStore(getClass(), "preferences.properties")));

		return stores;
	}

}
