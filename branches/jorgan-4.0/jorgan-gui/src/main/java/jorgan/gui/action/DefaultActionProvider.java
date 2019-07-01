package jorgan.gui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;

import jorgan.gui.OrganFrame;
import jorgan.gui.action.spi.ActionProvider;
import jorgan.session.OrganSession;

public class DefaultActionProvider implements ActionProvider {

	public List<Action> getMenuActions(OrganSession session, OrganFrame frame) {

		return Collections.<Action> singletonList(new FullScreenAction(session,
				frame));
	}

	@Override
	public List<Action> getToolbarActions(OrganSession session, OrganFrame frame) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new BackAction(session));
		actions.add(new ForwardAction(session));
		actions.add(new UndoAction(session));
		actions.add(new RedoAction(session));
		actions.add(new SearchAction(session, frame));
		return actions;
	}
}
