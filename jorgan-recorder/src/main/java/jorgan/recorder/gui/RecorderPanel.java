package jorgan.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.sound.midi.MidiMessage;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import jorgan.recorder.swing.LabelPanel;
import jorgan.swing.BaseAction;
import jorgan.swing.StandardDialog;
import bias.Configuration;

public class RecorderPanel extends JPanel {

	private static final Configuration config = Configuration.getRoot().get(
			RecorderPanel.class);

	private Recorder recorder;

	private PlayAction playAction = new PlayAction();

	private FirstAction firstAction = new FirstAction();

	private LastAction lastAction = new LastAction();

	private PreviousAction previousAction = new PreviousAction();

	private NextAction nextAction = new NextAction();

	private RecordAction recordAction = new RecordAction();

	private LabelPanel labelPanel;

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	private Timer timer = new Timer(500, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			updateLabel();
		}
	});

	public RecorderPanel() {
		super(new BorderLayout());

		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		this.recorder = new Recorder(1);
		recorder.addListener(new RecorderListener() {
			public void played(int track, long millis, MidiMessage message) {
			}

			public void recorded(int track, long millis, MidiMessage message) {
			}

			public void playing() {
				update();
			}

			public void recording() {
				update();
			}

			public void stopped() {
				update();
			}
			
			private void update() {
				playAction.update();
				recordAction.update();
			}
		});

		labelPanel = new LabelPanel() {

			@Override
			protected String getText() {
				return format.format(new Date(recorder.getTime()));
			}
		};
		labelPanel.setForeground(Color.white);
		labelPanel.setBackground(Color.black);
		add(labelPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
		add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.add(new JButton(firstAction));
		buttonPanel.add(new JButton(previousAction));
		buttonPanel.add(new JButton(playAction));
		buttonPanel.add(new JButton(nextAction));
		buttonPanel.add(new JButton(lastAction));
		buttonPanel.add(new JButton(recordAction));

		addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				if (isDisplayable()) {
					timer.start();
				} else {
					timer.stop();
				}
			}
		});
	}

	private void updateLabel() {
		labelPanel.repaint();
	}

	private class FirstAction extends BaseAction {
		public FirstAction() {
			config.get("first").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			recorder.first();
			updateLabel();
		}
	}

	private class LastAction extends BaseAction {
		public LastAction() {
			config.get("last").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			recorder.last();
			updateLabel();
		}
	}

	private class PreviousAction extends BaseAction {
		public PreviousAction() {
			config.get("previous").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			recorder.previous();
			updateLabel();
		}
	}

	private class NextAction extends BaseAction {
		public NextAction() {
			config.get("next").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			recorder.next();
			updateLabel();
		}
	}

	private class PlayAction extends BaseAction {
		public PlayAction() {
			config.get("play").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (recorder.isStopped()) {
				recorder.play();
			} else {
				recorder.stop();
			}
		}

		protected void update() {
			if (recorder.isPlaying()) {
				config.get("stop").read(this);				
			} else {
				config.get("play").read(this);				
			}
		}
	}

	private class RecordAction extends BaseAction {
		public RecordAction() {
			config.get("record").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (recorder.isRecording()) {
				recorder.stop();
			} else {
				recorder.record();
			}
		}

		protected void update() {
			if (recorder.isRecording()) {
				config.get("stop").read(this);				
			} else {
				config.get("record").read(this);				
			}
		}
	}

	/**
	 * Show in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 */
	public void showInDialog(JFrame owner) {
		StandardDialog dialog = new StandardDialog(owner, false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.setBody(this);

		dialog.autoPosition();
		dialog.setVisible(true);
	}

	public static void main(String[] args) {
		new RecorderPanel().showInDialog(null);
	}
}
