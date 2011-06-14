package jorgan.exporter.gui.action;

import java.awt.event.ActionEvent;

import jorgan.exporter.gui.ExportWizard;
import jorgan.gui.OrganFrame;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import bias.Configuration;

/**
 * The action that starts an export.
 */
public class ExportAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			ExportAction.class);

	private OrganSession session;

	private OrganFrame frame;

	public ExportAction(OrganSession session, OrganFrame frame) {
		this.session = session;
		this.frame = frame;

		config.read(this);
	}

	public void actionPerformed(ActionEvent ev) {
		ExportWizard.showInDialog(frame, session);
	}
}