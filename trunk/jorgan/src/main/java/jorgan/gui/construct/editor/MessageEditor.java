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
import java.util.StringTokenizer;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.sound.midi.ShortMessageRecorder;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * PropertyEditor for a message property.
 */
public class MessageEditor extends CustomEditor implements ElementAwareEditor,
		ActionListener {

	private static Configuration config = Configuration.getRoot().get(
			MessageEditor.class);

	private String device;

	private JPanel panel = new JPanel();

	private JTextField textField = new JTextField();

	private JButton button = new JButton("...");

	private MessageBox box;
	
	private boolean hex = false;

	/**
	 * Constructor.
	 */
	public MessageEditor() {
		config.read(this);
		
		panel.setLayout(new BorderLayout());

		button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(this);
		panel.add(button, BorderLayout.EAST);

		textField.setBorder(null);
		panel.add(textField, BorderLayout.CENTER);
	}

	public void setHex(boolean hex) {
		this.hex = hex;
	}
	
	public boolean getHex() {
		return hex;
	}
	
	public void setElement(Element element) {

		device = null;

		for (Console console : element.getReferrer(Console.class)) {
			device = console.getDevice();
			if (device != null) {
				break;
			}
		}
	}

	public Component getCustomEditor(Object value) {

		textField.setText(format(value));
		button.setEnabled(device != null);

		return panel;
	}

	protected Object getEditedValue() {

		Message message = null;

		try {
			StringTokenizer tokens = new StringTokenizer(textField.getText(),
					",");

			if (tokens.countTokens() == 0) {
				message = null;
			} else {
				String token0 = tokens.nextToken().trim();
				String token1 = tokens.nextToken().trim();
				String token2 = tokens.nextToken().trim();

				int status = parseData(token0, false);
				int data1 = parseData(token1, true);
				int data2 = parseData(token2, true);

				message = new Message(status, data1, data2);
			}
		} catch (Exception ex) {
			// invalid format results in null message
		}

		return message;
	}

	private int parseData(String token, boolean wildcards) {
		if (wildcards) {
			if ("*".equals(token)) {
				return Message.WILDCARD_ANY;
			}
			if ("+".equals(token)) {
				return Message.WILDCARD_POSITIVE;
			}
		}
		if (hex) {
			return Integer.parseInt(token, 16);
		} else {
			return Integer.parseInt(token);
		}
	}

	public void actionPerformed(ActionEvent ev) {
		ShortMessageRecorder recorder;
		try {
			recorder = new MessageRecorder(device);
		} catch (MidiUnavailableException ex) {
			// cannot record
			return;
		}

		box = new MessageBox(MessageBox.OPTION_CANCEL);
		config.get("recorder").read(box).show(panel);
		box.show(panel);
		box = null;

		recorder.close();
	}

	private String formatData(int data) {
		if (data == Message.WILDCARD_ANY) {
			return "*";
		}
		if (data == Message.WILDCARD_POSITIVE) {
			return "+";
		}
		
		if (hex) {
			String string = Integer.toString(data, 16).toUpperCase();
			if (string.length() == 1) {
				string = "0" + string;
			}
			return string;
		} else {
			return Integer.toString(data);
		}
	}

	protected String format(Object value) {
		Message message = (Message) value;
		if (message == null) {
			return "";
		} else {
			StringBuffer buffer = new StringBuffer();

			buffer.append(formatData(message.getStatus()));

			buffer.append(", ");

			buffer.append(formatData(message.getData1()));

			buffer.append(", ");

			buffer.append(formatData(message.getData2()));

			return buffer.toString();
		}
	}

	/**
	 * Recorder of a message.
	 */
	private class MessageRecorder extends ShortMessageRecorder implements
			Runnable {

		private int status;

		private int data1;

		private int data2;

		private MessageRecorder(String deviceName)
				throws MidiUnavailableException {
			super(deviceName);
		}

		public boolean messageRecorded(ShortMessage message) {
			status = message.getCommand() | message.getChannel();
			data1 = message.getData1();
			data2 = message.getData2();

			SwingUtilities.invokeLater(this);

			return false;
		}

		public void run() {
			Message message = new Message(status, data1, data2);

			textField.setText(format(message));

			box.hide();
		}
	}
}
