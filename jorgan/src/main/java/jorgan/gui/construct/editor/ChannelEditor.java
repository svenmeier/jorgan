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
package jorgan.gui.construct.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.sound.midi.ShortMessageRecorder;
import jorgan.util.I18N;

/**
 * A property editor for a channel property.
 */
public class ChannelEditor extends CustomEditor implements ElementAwareEditor,
		ActionListener {

	private static I18N i18n = I18N.get(ChannelEditor.class);

	private Keyboard keyboard;

	private JPanel panel = new JPanel();

	private JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1));

	private JButton button = new JButton("...");

	private JDialog dialog;

	private ShortMessageRecorder recorder;

	/**
	 * Constructor.
	 */
	public ChannelEditor() {
		panel.setLayout(new BorderLayout());

		button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(this);
		panel.add(button, BorderLayout.EAST);

		spinner.setBorder(null);
		panel.add(spinner, BorderLayout.CENTER);

		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
				.getEditor();
		editor.getTextField().setBorder(null);
	}

	public void setElement(Element element) {
		keyboard = (Keyboard) element;
	}

	protected String format(Object value) {
		if (value == null) {
			return "";
		} else {
			return "" + (((Integer) value).intValue() + 1);
		}
	}

	public Component getCustomEditor(Object value) {

		spinner.setValue(new Integer(((Integer) value).intValue() + 1));
		button.setEnabled(keyboard.getDevice() != null);

		return panel;
	}

	public void actionPerformed(ActionEvent ev) {
		try {
			recorder = new ChannelRecorder(keyboard.getDevice());
		} catch (MidiUnavailableException ex) {
			// cannot record
			return;
		}

		JOptionPane channelOptionPane = new JOptionPane(i18n
				.getString("channelOptionPane/message"),
				JOptionPane.INFORMATION_MESSAGE, -1, null, new Object[] { i18n
						.getString("channelOptionPane/cancel") });

		dialog = channelOptionPane.createDialog(panel, i18n
				.getString("channelOptionPane/title"));
		dialog.setVisible(true);
		dialog = null;

		recorder.close();
	}

	protected Object getEditedValue() {
		try {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
					.getEditor();
			editor.commitEdit();
		} catch (ParseException ex) {
			// invalid format so keep previous value
		}

		return new Integer(((Integer) spinner.getValue()).intValue() - 1);
	}

	/**
	 * Recorder of a channel.
	 */
	private class ChannelRecorder extends ShortMessageRecorder implements
			Runnable {

		private int channel;

		/**
		 * Constructor.
		 * 
		 * @param deviceName
		 *            name of device
		 * @throws MidiUnavailableException
		 */
		public ChannelRecorder(String deviceName)
				throws MidiUnavailableException {
			super(deviceName);
		}

		public boolean messageRecorded(ShortMessage message) {
			if (message.getCommand() == ShortMessage.NOTE_ON) {
				channel = message.getChannel();

				SwingUtilities.invokeLater(this);
				
				return false;
			}
			return true;
		}

		public void run() {

			spinner.setValue(new Integer(channel + 1));

			dialog.setVisible(false);
		}
	}
}
