package jorgan.gui.console.spi;

import jorgan.disposition.Continuous;
import jorgan.disposition.Displayable;
import jorgan.disposition.Engageable;
import jorgan.disposition.IndexedContinuous;
import jorgan.disposition.Memory;
import jorgan.disposition.Switch;
import jorgan.gui.console.ContinuousView;
import jorgan.gui.console.EngageableView;
import jorgan.gui.console.IndexedContinuousView;
import jorgan.gui.console.MemoryView;
import jorgan.gui.console.SwitchView;
import jorgan.gui.console.View;

public class DefaultViewProvider implements ViewProvider {

	public View<?> createView(Displayable element) {
		View<? extends Displayable> view = null;

		if (element instanceof Switch) {
			view = new SwitchView((Switch) element);
		} else if (element instanceof Engageable) {
			view = new EngageableView<Engageable>((Engageable) element);
		} else if (element instanceof Memory) {
			view = new MemoryView((Memory) element);
		} else if (element instanceof IndexedContinuous) {
			view = new IndexedContinuousView<IndexedContinuous>(
					(IndexedContinuous) element);
		} else if (element instanceof Continuous) {
			view = new ContinuousView<Continuous>((Continuous) element);
		}

		return view;
	}
}
