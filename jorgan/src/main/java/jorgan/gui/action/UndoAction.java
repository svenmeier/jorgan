package jorgan.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import jorgan.gui.undo.UndoListener;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import spin.Spin;
import bias.Configuration;

/**
 * The action that steps back to the previous element.
 */
public class UndoAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			UndoAction.class);

	private OrganSession session;

	UndoAction(OrganSession session) {
		this.session = session;

		session.addListener(new SessionListener() {
			@Override
			public void saved(File file) throws IOException {
			}

			@Override
			public void modified() {
			}

			@Override
			public void destroyed() {
			}

			@Override
			public void constructingChanged(boolean constructing) {
				update();
			}
		});
		UndoManager undoManager = this.session.lookup(UndoManager.class);
		undoManager.addListener((UndoListener) Spin.over(new UndoListener() {
			@Override
			public void done() {
				update();
			}
		}));

		config.read(this);

		update();
	}

	public void actionPerformed(ActionEvent ev) {
		session.lookup(UndoManager.class).undo();
	}

	private void update() {
		setEnabled(session.isConstructing()
				&& this.session.lookup(UndoManager.class).canUndo());
	}
}
