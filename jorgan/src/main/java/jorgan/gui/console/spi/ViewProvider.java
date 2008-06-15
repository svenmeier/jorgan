package jorgan.gui.console.spi;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;

public interface ViewProvider {

	public View<?> createView(Displayable element);
}
