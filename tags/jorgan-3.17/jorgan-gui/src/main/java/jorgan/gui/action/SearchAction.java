package jorgan.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import jorgan.gui.OrganFrame;
import jorgan.gui.search.SearchDialog;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import bias.Configuration;

public class SearchAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			SearchAction.class);

	private OrganSession session;

	private OrganFrame frame;

	SearchAction(OrganSession session, OrganFrame frame) {
		this.session = session;
		this.frame = frame;

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
				setEnabled(constructing);
			}
		});

		config.read(this);

		setEnabled(session.isConstructing());
	}

	public void actionPerformed(ActionEvent ev) {
		SearchDialog.show(session, frame);
	}
}
