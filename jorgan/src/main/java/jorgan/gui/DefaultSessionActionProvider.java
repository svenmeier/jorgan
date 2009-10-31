package jorgan.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import jorgan.gui.spi.SessionActionProvider;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;

public class DefaultSessionActionProvider implements SessionActionProvider {

	public List<Action> getActions(OrganSession session, OrganFrame frame) {

		final FullScreenAction action = new FullScreenAction(session, frame);

		session.addListener(new SessionListener() {

			public void constructingChanged(boolean constructing) {
			}

			public void destroyed() {
				action.destroy();
			}

			public void modified() {
			}

			public void saved(File file) throws IOException {
			}
		});

		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		return actions;
	}
}
