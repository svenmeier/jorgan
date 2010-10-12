package jorgan.customizer.gui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;

import jorgan.gui.OrganFrame;
import jorgan.gui.action.spi.ActionProvider;
import jorgan.session.OrganSession;

public class CustomizerActionProvider implements ActionProvider {

	public List<Action> getMenuActions(OrganSession session, OrganFrame frame) {
		List<Action> actions = new ArrayList<Action>();

		actions.add(new CustomizeAction(session, frame));

		return actions;
	}

	@Override
	public List<Action> getToolbarActions(OrganSession session, OrganFrame frame) {
		return Collections.emptyList();
	}
}
