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
package jorgan.fluidsynth.gui.preferences;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.windows.Backend;
import jorgan.fluidsynth.windows.BackendManager;
import jorgan.fluidsynth.windows.Link;
import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.swing.combobox.BaseComboBoxModel;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link Fluidsynth} category.
 */
public class FluidsynthCategory extends JOrganCategory {

	private static final Logger logger = Logger
			.getLogger(FluidsynthCategory.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthCategory.class);

	private Model<String> backend = getModel(new Property(BackendManager.class,
			"backend"));

	private JComboBox backendComboBox;

	private JTextField nameTextField;

	private JTextField versionTextField;

	private JTextField maintainerTextField;

	private JTextArea descriptionTextArea;

	private Box linksBox;

	private BackendManager manager;

	public FluidsynthCategory() {
		config.read(this);

		manager = new BackendManager();
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.term(config.get("backend").read(new JLabel()));

		backendComboBox = new JComboBox();
		backendComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				updateInstance();
			}
		});
		column.definition(backendComboBox).fillHorizontal();

		column.group(config.get("details").read(new JLabel()));

		column.term(config.get("backend/name").read(new JLabel()));
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		column.definition(nameTextField).fillHorizontal();

		column.term(config.get("backend/version").read(new JLabel()));
		versionTextField = new JTextField();
		versionTextField.setEditable(false);
		column.definition(versionTextField).fillHorizontal();

		column.term(config.get("backend/maintainer").read(new JLabel()));
		maintainerTextField = new JTextField();
		maintainerTextField.setEditable(false);
		column.definition(maintainerTextField).fillHorizontal();

		column.term(config.get("backend/description").read(new JLabel()));
		descriptionTextArea = new JTextArea();
		descriptionTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(descriptionTextArea);
		scrollPane.setPreferredSize(new Dimension(0, 0));
		column.definition(scrollPane).fillBoth().growVertical();

		linksBox = new Box(BoxLayout.X_AXIS);
		column.definition(linksBox);

		return panel;
	}

	protected void updateInstance() {
		String backend = (String) backendComboBox.getSelectedItem();

		if (backend != null) {
			Backend instance = manager.getInstance(backend);
			if (instance != null) {
				nameTextField.setText(instance.getName());
				versionTextField.setText(instance.getVersion());
				maintainerTextField.setText(instance.getMaintainer());
				descriptionTextArea.setText(instance.getDescription());

				linksBox.removeAll();
				for (final Link link : instance.getLinks()) {
					createButton(backend, link);
				}
				linksBox.revalidate();

				return;
			}
		}

		nameTextField.setText("");
		versionTextField.setText("");
		maintainerTextField.setText("");
		descriptionTextArea.setText("");

		linksBox.removeAll();
		linksBox.revalidate();
	}

	private void createButton(String backend, final Link link) {
		try {
			JButton button = new JButton();
			button.setMargin(new Insets(0,0,0,0));

			String icon = link.getIcon();
			if (icon != null) {
				button.setIcon(new ImageIcon(manager.getFile(backend, icon)
						.getAbsolutePath()));

				button.setToolTipText(link.getText());
			} else {
				button.setText(link.getText());
			}

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						Desktop.getDesktop().browse(URI.create(link.getUrl()));
					} catch (Exception e) {
						logger.log(Level.WARNING, e.getMessage(), e);
					}
				}
			});
			linksBox.add(button);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	protected void read() {
		backendComboBox.setModel(new BaseComboBoxModel<String>(manager
				.getBackends()));

		backendComboBox.setSelectedItem(backend.getValue());
	}

	@Override
	protected void write() {
		backend.setValue((String) backendComboBox.getSelectedItem());
	}
}
