package jorgan.recorder.gui.spi;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import jorgan.gui.spi.SessionActionProvider;
import jorgan.recorder.gui.RecordAction;
import jorgan.session.OrganSession;

public class RecorderSessionActionProvider implements SessionActionProvider {

	public List<Action> getActions(OrganSession session) {
		List<Action> actions = new ArrayList<Action>();

		actions.add(new RecordAction(session));

		return actions;
	}
}
