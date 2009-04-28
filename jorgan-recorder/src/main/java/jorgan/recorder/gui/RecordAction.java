package jorgan.recorder.gui;

import java.awt.event.ActionEvent;

import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import bias.Configuration;

/**
 * The action that starts recording.
 */
public class RecordAction extends BaseAction {

	static Configuration config = Configuration.getRoot().get(
			RecordAction.class);

	private OrganSession session;

	public RecordAction(OrganSession session) {
		this.session = session;

		config.read(this);
	}

	public void actionPerformed(ActionEvent ev) {
	}
}