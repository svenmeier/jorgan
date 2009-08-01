package jorgan.gui.console;

import jorgan.disposition.ConsoleSwitcher;
import jorgan.disposition.Continuous;
import jorgan.disposition.Displayable;
import jorgan.disposition.Engageable;
import jorgan.disposition.Regulator;
import jorgan.disposition.Switch;
import jorgan.gui.console.spi.ViewProvider;
import jorgan.session.OrganSession;

public class DefaultViewProvider implements ViewProvider {

	public View<?> createView(OrganSession session, Displayable element) {
		View<? extends Displayable> view = null;

		if (element instanceof ConsoleSwitcher) {
			view = new ConsoleSwitcherView((ConsoleSwitcher) element);
		} else if (element instanceof Switch) {
			view = new SwitchView((Switch) element);
		} else if (element instanceof Engageable) {
			view = new EngageableView<Engageable>((Engageable) element);
		} else if (element instanceof Regulator) {
			view = new RegulatorView((Regulator) element);
		} else if (element instanceof Continuous) {
			view = new ContinuousView<Continuous>((Continuous) element);
		}

		return view;
	}
}
