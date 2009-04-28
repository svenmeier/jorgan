package jorgan.gui;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import jorgan.gui.imports.ImportWizard;
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

	public ImportAction(OrganSession session) {
		this.session = session;

		config.read(this);
	}

	public void actionPerformed(ActionEvent ev) {
		ImportWizard.showInDialog(((JComponent) ev.getSource()), session);
	}
}