package jorgan.recorder.gui.console;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;
import jorgan.gui.console.spi.ViewProvider;
import jorgan.recorder.disposition.Recorder;
import jorgan.session.OrganSession;

public class RecorderViewProvider implements ViewProvider {

	public View<?> createView(OrganSession session, Displayable element) {
		View<? extends Displayable> view = null;

		if (element instanceof Recorder) {
			view = new RecorderView(session, (Recorder) element);
		}

		return view;
	}
}
