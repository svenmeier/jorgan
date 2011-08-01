package jorgan.recorder.gui.dock;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.dock.AbstractView;
import jorgan.gui.dock.spi.ViewProvider;

public class RecorderViewProvider implements ViewProvider {

	public List<AbstractView> getViews() {
		List<AbstractView> views = new ArrayList<AbstractView>();

		views.add(new RecorderView());

		return views;
	}
}
