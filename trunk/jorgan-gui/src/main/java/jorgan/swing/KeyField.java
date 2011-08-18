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
package jorgan.swing;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jorgan.disposition.Shortcut;

/**
 */
public class KeyField extends JPanel {

	private Listener listener = new Listener();

	private JTextField textField = new JTextField();

	private JButton button = new JButton("\u2190");

	public KeyField() {
		setLayout(new BorderLayout());

		button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(listener);
		add(button, BorderLayout.EAST);

		textField.setEditable(false);
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.addKeyEventDispatcher(listener);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
						.removeKeyEventDispatcher(listener);
			}
		});
		add(textField, BorderLayout.CENTER);
	}

	@Override
	public void removeNotify() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(listener);

		super.removeNotify();
	}

	private class Listener implements KeyEventDispatcher, ActionListener {

		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_RELEASED) {
				Shortcut shortcut = Shortcut.createShortCut(e);
				if (shortcut != null) {
					textField.setText(shortcut.toString());

					return true;
				}
			}

			return false;
		}

		public void actionPerformed(ActionEvent arg0) {
			textField.setText("");
		}
	};
}
