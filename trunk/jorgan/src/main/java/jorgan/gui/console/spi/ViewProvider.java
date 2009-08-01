package jorgan.gui.console.spi;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;
import jorgan.session.OrganSession;

public interface ViewProvider {

	public View<?> createView(OrganSession session, Displayable element);
}
