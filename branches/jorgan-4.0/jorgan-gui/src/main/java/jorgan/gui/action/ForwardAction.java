package jorgan.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import spin.Spin;
import bias.Configuration;

/**
 * The action that steps back to the previous element.
 */
public class ForwardAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			ForwardAction.class);

	private OrganSession session;

	public ForwardAction(OrganSession session) {
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
		session.lookup(ElementSelection.class).addListener(
				(SelectionListener) Spin.over(new SelectionListener() {
					@Override
					public void selectionChanged() {
						update();
					}
				}));

		config.read(this);

		setEnabled(false);
	}

	protected void update() {
		final ElementSelection selection = session
				.lookup(ElementSelection.class);

		setEnabled(session.isConstructing() && selection.canForward());
	}

	public void actionPerformed(ActionEvent ev) {
		session.lookup(ElementSelection.class).forward();
	}
}
