package jorgan.gui.action.spi;

import java.util.List;

import javax.swing.Action;

import jorgan.gui.OrganFrame;
import jorgan.session.OrganSession;

public interface ActionProvider {

	public List<Action> getMenuActions(OrganSession session, OrganFrame frame);

	/**
	 * TODO split into toolbar and edit actions
	 */
	public List<Action> getToolbarActions(OrganSession session, OrganFrame frame);
}
