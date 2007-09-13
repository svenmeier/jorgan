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
import javax.swing.AbstractSpinnerModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;

import jorgan.disposition.Element;
import jorgan.disposition.Key;
import jorgan.disposition.Keyboard;
import jorgan.sound.midi.ShortMessageRecorder;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Property editor for a key property.
 */
public class KeyEditor extends CustomEditor implements ElementAwareEditor,
		ActionListener {

	private static Configuration config = Configuration.getRoot().get(
			KeyEditor.class);

	private Keyboard keyboard;

	private KeyFormatter formatter = new KeyFormatter();

	private KeyModel model = new KeyModel();

	private JPanel panel = new JPanel();

	private JSpinner spinner = new JSpinner(model);

	private JButton button = new JButton("...");

	private MessageBox box;

	private ShortMessageRecorder recorder;

	/**
	 * Constructor.
	 */
	public KeyEditor() {
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
		editor.getTextField().setEditable(true);
		editor.getTextField().setFormatterFactory(
				new DefaultFormatterFactory(formatter));
	}

	public void setElement(Element element) {
		keyboard = (Keyboard) element;
	}

	@Override
	public Component getCustomEditor(Object value) {

		spinner.setValue(value);
		button.setEnabled(keyboard.getDevice() != null);

		return panel;
	}

	@Override
	protected Object getEditedValue() {
		try {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
					.getEditor();
			editor.commitEdit();
		} catch (ParseException ex) {
			// invalid format so keep previous value
		}

		return spinner.getValue();
	}

	public void actionPerformed(ActionEvent ev) {
		try {
			recorder = new KeyRecorder(keyboard.getDevice());
		} catch (MidiUnavailableException ex) {
			// cannot record
			return;
		}

		box = new MessageBox(MessageBox.OPTIONS_OK);
		config.get("recorder").read(box).show(panel);
		box.show(panel);
		box = null;

		recorder.close();
	}

	@Override
	protected String format(Object value) {
		return formatter.valueToString(value);
	}

	private class KeyModel extends AbstractSpinnerModel {

		private Key key;

		public Object getValue() {
			return key;
		}

		public void setValue(Object value) {

			if (value == null && key != null || value != null && key == null
					|| value != null && !value.equals(key)) {
				this.key = (Key) value;
				fireStateChanged();
			}
		}

		public Object getNextValue() {
			if (key == null) {
				return Key.C4;
			} else {
				return key.halftoneUp();
			}
		}

		public Object getPreviousValue() {
			if (key == null) {
				return Key.C4;
			} else {
				return key.halftoneDown();
			}
		}
	}

	private class KeyFormatter extends JFormattedTextField.AbstractFormatter {
		@Override
		public Object stringToValue(String text) throws ParseException {
			if ("".equals(text)) {
				return null;
			} else {
				try {
					return new Key(text);
				} catch (IllegalArgumentException ex) {
					throw new ParseException("no key with name '" + text + "'",
							0);
				}
			}
		}

		@Override
		public String valueToString(Object value) {
			if (value == null) {
				return "";
			} else {
				return ((Key) value).getName();
			}
		}
	}

	/**
	 * Recorder of a key.
	 */
	private class KeyRecorder extends ShortMessageRecorder implements Runnable {

		private int pitch;

		/**
		 * Constructor.
		 * 
		 * @param deviceName
		 *            name of device
		 * @throws MidiUnavailableException
		 */
		public KeyRecorder(String deviceName) throws MidiUnavailableException {
			super(deviceName);
		}

		@Override
		public boolean messageRecorded(ShortMessage message) {
			if (message.getCommand() == ShortMessage.NOTE_ON) {
				pitch = message.getData1();

				SwingUtilities.invokeLater(this);

				return false;
			}
			return true;
		}

		public void run() {
			Key key = new Key(pitch);

			spinner.setValue(key);

			box.hide();
		}
	}
}
