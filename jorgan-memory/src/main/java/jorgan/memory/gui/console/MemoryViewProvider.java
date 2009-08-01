package jorgan.memory.gui.console;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;
import jorgan.gui.console.spi.ViewProvider;
import jorgan.memory.disposition.MemorySwitcher;
import jorgan.session.OrganSession;

public class MemoryViewProvider implements ViewProvider {

	public View<?> createView(OrganSession session, Displayable element) {
		View<? extends Displayable> view = null;

		if (element instanceof MemorySwitcher) {
			view = new MemorySwitcherView(session, (MemorySwitcher) element);
		}

		return view;
	}
}
