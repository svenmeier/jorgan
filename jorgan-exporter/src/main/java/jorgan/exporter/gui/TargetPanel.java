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
package jorgan.exporter.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bias.Configuration;
import bias.swing.MessageBox;
import jorgan.exporter.target.ClipboardTarget;
import jorgan.exporter.target.FileTarget;
import jorgan.exporter.target.Target;
import jorgan.session.History;
import jorgan.swing.FileSelector;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;

/**
 * A {@link Target} configuration for an {@link Export}.
 */
public class TargetPanel extends JPanel {

	private static Configuration config = Configuration.getRoot()
			.get(TargetPanel.class);

	private JRadioButton clipboardRadioButton;

	private JRadioButton fileRadioButton;

	private FileSelector fileSelector;

	public TargetPanel() {
		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("target").read(new JLabel()));

		ButtonGroup group = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				firePropertyChange("target", null, null);
			}
		};

		clipboardRadioButton = new JRadioButton();
		group.add(clipboardRadioButton);
		column.definition(config.get("clipboard").read(clipboardRadioButton));

		fileRadioButton = new JRadioButton();
		group.add(fileRadioButton);
		column.definition(config.get("file").read(fileRadioButton));

		fileSelector = new FileSelector(FileSelector.FILES_ONLY) {
			protected File toChooser(File file) {
				if (file == null) {
					file = new History().getRecentDirectory();
				}
				return file;
			}
		};
		fileSelector.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				firePropertyChange("file", null, null);
			}
		});
		fileSelector.setEnabled(false);
		fileRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				fileSelector.setEnabled(fileRadioButton.isSelected());
			}
		});
		column.definition(fileSelector).fillHorizontal();
	}

	public boolean hasTarget() {
		return clipboardRadioButton.isSelected()
				|| fileSelector.getSelectedFile() != null;
	}

	public Target getTarget() {
		if (clipboardRadioButton.isSelected()) {
			return new ClipboardTarget();
		} else {
			File file = fileSelector.getSelectedFile();
			if (file != null) {
				if (!file.exists() || confirmFileExists(file)) {
					return new FileTarget(file);
				}
			}
		}

		return null;
	}

	private boolean confirmFileExists(File file) {
		return config.get("fileExists")
				.read(new MessageBox(MessageBox.OPTIONS_OK_CANCEL))
				.show(this, file.getName()) == MessageBox.OPTION_OK;
	}
}