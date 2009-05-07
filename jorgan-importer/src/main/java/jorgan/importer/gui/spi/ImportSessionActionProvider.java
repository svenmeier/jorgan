package jorgan.importer.gui.spi;


import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import jorgan.gui.spi.SessionActionProvider;
import jorgan.importer.gui.ImportAction;
import jorgan.session.OrganSession;

public class ImportSessionActionProvider implements SessionActionProvider {

	public List<Action> getActions(OrganSession session) {
		List<Action> actions = new ArrayList<Action>();

		actions.add(new ImportAction(session));

		return actions;
	}
}
