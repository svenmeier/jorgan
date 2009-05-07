package jorgan.customizer.gui;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import bias.Configuration;

/**
 * The action that starts the devices configuration wizard.
 */
public class CustomizeAction extends BaseAction {

	static Configuration config = Configuration.getRoot().get(
			CustomizeAction.class);

	private OrganSession session;

	public CustomizeAction(OrganSession session) {
		this.session = session;

		config.read(this);
	}

	public void actionPerformed(ActionEvent ev) {
		CustomizeWizard.showInDialog((JComponent) ev.getSource(), session);
	}
}