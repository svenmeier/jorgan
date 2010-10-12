package jorgan.memory.gui.console;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;
import jorgan.gui.console.spi.ViewProvider;
import jorgan.memory.disposition.Memory;
import jorgan.session.OrganSession;

public class MemoryViewProvider implements ViewProvider {

	public View<?> createView(OrganSession session, Displayable element) {
		View<? extends Displayable> view = null;

		if (element instanceof Memory) {
			view = new MemoryView(session, (Memory) element);
		}

		return view;
	}
}
