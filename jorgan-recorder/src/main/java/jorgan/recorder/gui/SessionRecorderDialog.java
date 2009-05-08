/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.recorder.gui;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import jorgan.recorder.SessionRecorder;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.StandardDialog;
import bias.Configuration;

public class SessionRecorderDialog extends StandardDialog {

	private static Configuration config = Configuration.getRoot().get(
			SessionRecorderDialog.class);

	private SessionRecorder sessionRecorder;

	private NewAction newAction = new NewAction();

	private OpenAction openAction = new OpenAction();

	private SaveAction saveAction = new SaveAction();

	public SessionRecorderDialog(JDialog owner, OrganSession session) {
		super(owner, false);

		init(session);
	}

	public SessionRecorderDialog(JFrame owner, OrganSession session) {
		super(owner, false);

		init(session);
	}

	private void init(OrganSession session) {
		sessionRecorder = new SessionRecorder(session);

		setBody(new SessionRecordPanel(sessionRecorder));
	}

	@Override
	public void dispose() {
		super.dispose();

		if (sessionRecorder != null) {
			sessionRecorder.dispose();
			sessionRecorder = null;
		}
	}

	@Override
	public void onCancel() {
		if (sessionRecorder != null) {
			sessionRecorder.stop();
		}
		
		super.onCancel();
	}

	private class NewAction extends BaseAction {
		public NewAction() {
			config.get("new").read(this);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class OpenAction extends BaseAction {
		public OpenAction() {
			config.get("open").read(this);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class SaveAction extends BaseAction {
		public SaveAction() {
			config.get("save").read(this);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	public static SessionRecorderDialog showInDialog(Component owner,
			OrganSession session) {

		Window window = getWindow(owner);

		final SessionRecorderDialog dialog;

		if (window instanceof JFrame) {
			dialog = new SessionRecorderDialog((JFrame) window, session);
		} else if (window instanceof JDialog) {
			dialog = new SessionRecorderDialog((JDialog) window, session);
		} else {
			throw new Error("unable to get window ancestor");
		}

		config.read(dialog);
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				config.write(dialog);
			}
		});

		return dialog;
	}
}
