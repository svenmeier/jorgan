package jorgan.importer;

import java.util.ArrayList;
import java.util.List;

import jorgan.spi.ConfigurationProvider;
import bias.Store;
import bias.store.DefaultingStore;
import bias.store.PropertiesStore;

public class ImporterConfigurationProvider implements ConfigurationProvider {

	public List<Store> getStores(Store preferencesStore) {
		ArrayList<Store> stores = new ArrayList<Store>();

		stores.add(new DefaultingStore(preferencesStore, new PropertiesStore(
				getClass(), "preferences.properties")));

		return stores;
	}

}
