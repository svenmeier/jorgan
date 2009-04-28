package jorgan.gui.spi;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import jorgan.gui.ImportAction;
import jorgan.session.OrganSession;

public class DefaultSessionActionProvider implements SessionActionProvider {

	public List<Action> getActions(OrganSession session) {
		List<Action> actions = new ArrayList<Action>();

		actions.add(new ImportAction(session));

		return actions;
	}
}
