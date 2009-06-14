package jorgan.customizer.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import jorgan.gui.OrganFrame;
import jorgan.gui.spi.SessionActionProvider;
import jorgan.session.OrganSession;

public class CustomizerSessionActionProvider implements SessionActionProvider {

	public List<Action> getActions(OrganSession session, OrganFrame frame) {
		List<Action> actions = new ArrayList<Action>();

		actions.add(new CustomizeAction(session, frame));

		return actions;
	}
}
