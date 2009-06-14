package jorgan.recorder.gui.dock;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.dock.OrganDockable;
import jorgan.gui.dock.spi.DockableProvider;

public class RecorderDockableProvider implements DockableProvider {

	public List<OrganDockable> getDockables() {
		List<OrganDockable> dockables = new ArrayList<OrganDockable>();

		dockables.add(new RecorderDockable());

		return dockables;
	}
}
