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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.accessibility.AccessibleContext;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import jorgan.swing.button.ToolbarButton;

/**
 * Selector of a file.
 */
public class FileSelector extends JPanel {

	public static final int FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;

	public static final int FILES_ONLY = JFileChooser.FILES_ONLY;

	public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;

	private int mode;

	private JTextField textField;

	private JButton button;

	private JFileChooser chooser;

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	private FileFilter filter;

	/**
	 * Create a new selector.
	 */
	public FileSelector(int mode) {
		super(new BorderLayout());

		this.mode = mode;

		button = new ToolbarButton("...");
		button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				showFileChooser();
			}
		});
		add(button, BorderLayout.EAST);

		textField = createTextField();
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				fireStateChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				fireStateChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				fireStateChanged();
			}
		});
		add(textField, BorderLayout.CENTER);

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.requestFocusInWindow();
			}
		});
	}

	protected JTextField createTextField() {
		return new JTextField();
	}

	@Override
	public void setEnabled(boolean enabled) {
		textField.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	/**
	 * Add a listener to changes.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener to changes.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeChangeListener(ChangeListener listener) {
		if (!listeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
	}

	private void showFileChooser() {
		if (chooser == null) {
			chooser = new JFileChooser();
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setFileSelectionMode(mode);
			chooser.setMultiSelectionEnabled(false);
		}
		File file = getSelectedFile();
		if (file != null) {
			file = file.getAbsoluteFile();
		}
		chooser.setSelectedFile(file);
		chooser.setFileFilter(filter);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			textField.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	private void fireStateChanged() {
		for (int l = 0; l < listeners.size(); l++) {
			ChangeListener listener = listeners.get(l);
			listener.stateChanged(new ChangeEvent(this));
		}
	}

	/**
	 * Set the selected file.
	 * 
	 * @param file
	 *            the file to select
	 */
	public void setSelectedFile(File file) {
		if (file == null) {
			textField.setText("");
		} else {
			textField.setText(file.getPath());
		}
	}

	/**
	 * Get the selected file.
	 * 
	 * @return the selected file
	 */
	public File getSelectedFile() {
		String text = textField.getText();
		if ("".equals(text)) {
			return null;
		} else {
			return new File(text);
		}
	}

	@Override
	public AccessibleContext getAccessibleContext() {
		return textField.getAccessibleContext();
	}
}