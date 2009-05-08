package jorgan.recorder.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import jorgan.recorder.SessionRecorder;
import jorgan.recorder.midi.Recorder;
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
			protected TrackPanel createTrackPanel(Recorder recorder,
					final int track) {
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
}