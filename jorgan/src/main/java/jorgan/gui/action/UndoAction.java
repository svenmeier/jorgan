package jorgan.gui.action;

import java.awt.event.ActionEvent;

import jorgan.gui.undo.UndoListener;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
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

		final UndoManager undoManager = this.session.lookup(UndoManager.class);
		undoManager.addListener((UndoListener) Spin.over(new UndoListener() {
			@Override
			public void done() {
				setEnabled(undoManager.canUndo());
			}
		}));

		config.read(this);

		setEnabled(false);
	}

	public void actionPerformed(ActionEvent ev) {
		session.lookup(UndoManager.class).undo();
	}
}
