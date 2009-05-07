package jorgan.customizer.gui.spi;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import jorgan.customizer.gui.CustomizeAction;
import jorgan.gui.spi.SessionActionProvider;
import jorgan.session.OrganSession;

public class CustomizerSessionActionProvider implements SessionActionProvider {

	public List<Action> getActions(OrganSession session) {
		List<Action> actions = new ArrayList<Action>();

		actions.add(new CustomizeAction(session));

		return actions;
	}
}
