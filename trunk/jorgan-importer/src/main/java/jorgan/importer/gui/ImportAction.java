package jorgan.importer.gui;

import java.awt.event.ActionEvent;

import jorgan.gui.OrganFrame;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import bias.Configuration;

/**
 * The action that starts an import.
 */
public class ImportAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			ImportAction.class);

	private OrganSession session;

	private OrganFrame frame;

	public ImportAction(OrganSession session, OrganFrame frame) {
		this.session = session;
		this.frame = frame;

		config.read(this);
	}

	public void actionPerformed(ActionEvent ev) {
		ImportWizard.showInDialog(frame, session);
	}
}