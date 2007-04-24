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
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.sound.midi.ShortMessageRecorder;
import jorgan.util.I18N;

/**
 * PropertyEditor for a message property.
 */
public class MessageEditor extends CustomEditor implements ElementAwareEditor,
		ActionListener {

	private static I18N i18n = I18N.get(MessageEditor.class);

	private String device;

	private JPanel panel = new JPanel();

	private JTextField textField = new JTextField();

	private JButton button = new JButton("...");

	private JDialog dialog;

	/**
	 * Constructor.
	 */
	public MessageEditor() {
		panel.setLayout(new BorderLayout());

		button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(this);
		panel.add(button, BorderLayout.EAST);

		textField.setBorder(null);
		panel.add(textField, BorderLayout.CENTER);
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

				int status = Integer.parseInt(token0);
				int data1 = parseData(token1);
				int data2 = parseData(token2);

				message = new Message(status, data1, data2);
			}
		} catch (Exception ex) {
			// invalid format results in null message
		}

		return message;
	}

	private int parseData(String token) {
		if ("*".equals(token)) {
			return Message.WILDCARD_ANY;
		} else if ("+".equals(token)) {
			return Message.WILDCARD_POSITIVE;
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

		JOptionPane messageOptionPane = new JOptionPane(i18n
				.getString("messageOptionPane/message"),
				JOptionPane.INFORMATION_MESSAGE, -1, null, new Object[] { i18n
						.getString("messageOptionPane/cancel") });

		dialog = messageOptionPane.createDialog(panel.getTopLevelAncestor(),
				i18n.getString("messageOptionPane/title"));
		dialog.setVisible(true);
		dialog.dispose();
		dialog = null;

		recorder.close();
	}

	private String formatData(int data) {
		if (data == Message.WILDCARD_ANY) {
			return "*";
		} else if (data == Message.WILDCARD_POSITIVE) {
			return "+";
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

			buffer.append(message.getStatus());

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

			dialog.setVisible(false);
		}
	}
}
