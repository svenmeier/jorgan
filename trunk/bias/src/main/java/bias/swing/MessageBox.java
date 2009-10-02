/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.swing;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * A {@link JOptionPane} wrapper that is easily configurable.
 */
public class MessageBox {

	public static final int OPTIONS_YES_NO = JOptionPane.YES_NO_OPTION;

	public static final int OPTIONS_YES_NO_CANCEL = JOptionPane.YES_NO_CANCEL_OPTION;

	public static final int OPTIONS_OK = JOptionPane.DEFAULT_OPTION;

	public static final int OPTIONS_OK_CANCEL = JOptionPane.OK_CANCEL_OPTION;

	public static final int OPTION_YES = JOptionPane.YES_OPTION;

	public static final int OPTION_NO = JOptionPane.NO_OPTION;

	public static final int OPTION_CANCEL = JOptionPane.CANCEL_OPTION;

	public static final int OPTION_OK = JOptionPane.OK_OPTION;

	public static final int OPTION_CLOSED = JOptionPane.CLOSED_OPTION;

	private Type type = Type.PLAIN;

	private int optionsType;

	private Object[] options;

	private String title;

	private String pattern;

	private Component[] components;

	private Icon icon;

	private JDialog dialog;

	/**
	 * Constructor.
	 * 
	 * @param optionsType
	 *            type of options
	 * @see #OPTIONS_OK_CANCEL
	 * @see #OPTIONS_OK
	 * @see #OPTIONS_YES_NO_CANCEL
	 * @see #OPTIONS_YES_NO
	 */
	public MessageBox(int optionsType) {
		this.optionsType = optionsType;
	}

	/**
	 * Set the type.
	 * 
	 * @param type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public void setComponents(Component... components) {
		this.components = components;
	}
	
	/**
	 * Set the options.
	 * 
	 * @param options
	 */
	public void setOptions(Object[] options) {
		this.options = options;
	}

	/**
	 * Show this message box for the given parent.
	 * 
	 * @param parent
	 *            parent component
	 * @return selected option
	 */
	public int show(Component parent, Object... arguments) {
		JOptionPane optionPane = new JOptionPane();

		optionPane.setMessageType(type.getMessageType());
		String message = MessageFormat.format(pattern, arguments);
		if (components == null) {
			optionPane.setMessage(message);
		} else {
			Object[] messages = new Object[components.length + 1];
			messages[0] = message;
			System.arraycopy(components, 0, messages, 1, components.length);
			optionPane.setMessage(messages);
		}
		optionPane.setIcon(icon);
		if (options == null) {
			optionPane.setOptionType(optionsType);
		} else {
			optionPane.setOptionType(JOptionPane.DEFAULT_OPTION);
			optionPane.setOptions(options);
			optionPane.setInitialValue(options[0]);
		}

		dialog = optionPane.createDialog(parent, title);
		dialog.setVisible(true);
		dialog.dispose();
		dialog = null;

		Object value = optionPane.getValue();
		if (value != null && value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		return OPTION_CLOSED;
	}

	public void hide() {
		dialog.setVisible(false);
	}

	public static enum Type {
		ERROR(JOptionPane.ERROR_MESSAGE), INFORMATION(
				JOptionPane.INFORMATION_MESSAGE), WARNING(
				JOptionPane.WARNING_MESSAGE), QUESTION(
				JOptionPane.QUESTION_MESSAGE), PLAIN(JOptionPane.PLAIN_MESSAGE);

		private int messageType;

		private Type(int messageType) {
			this.messageType = messageType;
		}

		public int getMessageType() {
			return messageType;
		}
	}
}
