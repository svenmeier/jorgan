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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 */
public class FileField extends JPanel {

	private JTextField textField;

	private JButton button;

	private JFileChooser fileChooser;

	public FileField() {
		setLayout(new BorderLayout());

		button = new JButton("...");
		button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
					fileChooser
							.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fileChooser.setMultiSelectionEnabled(false);
				}

				if (!"".equals(textField.getText())) {
					fileChooser.setSelectedFile(new File(textField.getText()));
				}

				if (fileChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
					textField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		add(button, BorderLayout.EAST);

		textField = createTextField();
		add(textField, BorderLayout.CENTER);
	}

	protected JTextField createTextField() {
		return new JTextField();
	}

	public void setFile(File file) {
		if (file == null) {
			textField.setText("");
		} else {
			textField.setText(file.getPath());
		}
	}

	public File getFile() {
		String text = textField.getText();
		if ("".equals(text.trim())) {
			return null;
		}
		return new File(text);
	}
}
