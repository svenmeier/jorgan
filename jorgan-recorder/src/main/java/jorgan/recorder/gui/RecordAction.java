package jorgan.recorder.gui;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import bias.Configuration;

/**
 * The action that starts recording.
 */
public class RecordAction extends BaseAction {

	static Configuration config = Configuration.getRoot().get(
			RecordAction.class);

	private OrganSession session;
	
	private SessionRecorderDialog dialog;

	public RecordAction(OrganSession session) {
		this.session = session;

		config.read(this);
		
		session.addListener(new SessionListener() {
			public void constructingChanged(boolean constructing) {
			}
			public void destroyed() {
				if (dialog != null) {
					dialog.setVisible(false);
					dialog.dispose();
					dialog = null;
				}
			}
		});
	}

	public void actionPerformed(ActionEvent ev) {
		if (dialog == null) {
			dialog = SessionRecorderDialog.showInDialog(((JComponent) ev.getSource()), session);
		} else {
			dialog.setVisible(true);
			dialog.toFront();
		}
	}
}