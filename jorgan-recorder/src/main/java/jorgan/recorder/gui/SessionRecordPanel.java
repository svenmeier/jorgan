package jorgan.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import jorgan.recorder.SessionRecorder;
import jorgan.recorder.midi.Recorder;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.swing.StandardDialog;
import bias.Configuration;

/**
 * A dialog for recording.
 */
public class SessionRecordPanel extends JPanel {

	static Configuration config = Configuration.getRoot().get(
			SessionRecordPanel.class);
	private SessionRecorder sessionRecorder;

	public SessionRecordPanel(SessionRecorder recorder) {
		super(new BorderLayout());

		config.read(this);
		
		this.sessionRecorder = recorder;

		add(new RecorderPanel(recorder.getRecorder()) {
			@Override
			protected TrackPanel createTrackPanel(Recorder recorder, final int track) {
				return new TrackPanel(recorder, track) {
					@Override
					protected String getTitle() {
						return SessionRecordPanel.this.getTitle(track);
					}
				};
			}
		}, BorderLayout.CENTER);
	}

	private String getTitle(int track) {
		return sessionRecorder.getTitle(track);
	}

	public static void showInDialog(Component owner, OrganSession session) {
		final StandardDialog dialog = StandardDialog.create(owner);
		dialog.setModal(false);
		
		config.get("dialog").read(dialog);

		session.addListener(new SessionListener() {
			public void constructingChanged(boolean constructing) {
			}
			public void destroyed() {
				dialog.setVisible(false);
			}
		});

		final SessionRecorder recorder = new SessionRecorder(session);
		SessionRecordPanel panel = new SessionRecordPanel(recorder);
		dialog.setBody(panel);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				recorder.dispose();
			}
		});

		dialog.setVisible(true);
	}
}