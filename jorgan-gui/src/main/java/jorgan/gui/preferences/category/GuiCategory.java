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
package jorgan.gui.preferences.category;

import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SpinnerNumberModel;

import bias.Configuration;
import bias.swing.Category;
import bias.util.MessageBuilder;
import bias.util.Property;
import jorgan.App;
import jorgan.gui.FullScreen;
import jorgan.gui.GUI;
import jorgan.gui.LAF;
import jorgan.session.History;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.combobox.BaseComboBoxModel;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.util.LocaleUtils;

/**
 * {@link GUI} category.
 */
public class GuiCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot()
			.get(GuiCategory.class);

	private Model<Locale> locale = getModel(new Property(App.class, "locale"));

	private Model<LAF> lookAndFeel = getModel(
			new Property(GUI.class, "lookAndFeel"));

	private Model<Integer> scale = getModel(new Property(GUI.class, "scale"));

	private Model<Boolean> fullScreenAutoScroll = getModel(
			new Property(FullScreen.class, "autoScroll"));

	private Model<Integer> historyMax = getModel(
		    new Property(History.class,	"max"));

	private JRadioButton localeDefaultRadioButton = new JRadioButton();

	private JRadioButton localeOtherRadioButton = new JRadioButton();

	private JComboBox<Locale> localeComboBox = new JComboBox<Locale>();

	private JComboBox<LAF> lookAndFeelComboBox = new JComboBox<LAF>();

	private JSlider scaleSlider = new JSlider(1, 4, 1);

	private JCheckBox fullScreenAutoScrollCheckBox = new JCheckBox();

	private JSpinner historyMaxSpinner = new JSpinner(new SpinnerNumberModel(0,
			0, 100, 1));

	public GuiCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();
		
		ButtonGroup localeGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				localeComboBox.setEnabled(button == localeOtherRadioButton);
			}
		};
		column.term(config.get("locale").read(new JLabel()));

		String message = config.get("localeDefault").read(new MessageBuilder())
				.build(LocaleUtils.getDefault());
		localeDefaultRadioButton.setText(message);
		localeGroup.add(localeDefaultRadioButton);
		column.definition(localeDefaultRadioButton);

		config.get("localeOther").read(localeOtherRadioButton);
		localeGroup.add(localeOtherRadioButton);
		column.definition(localeOtherRadioButton);

		localeComboBox.setEditable(true);
		localeComboBox.setModel(new BaseComboBoxModel<Locale>(LocaleUtils
				.getLocales()) {
			@Override
			protected Locale convert(String element) {
				return new Locale(element);
			}
		});
		column.definition(localeComboBox);

		column.term(config.get("lookAndFeel").read(new JLabel()));
		lookAndFeelComboBox.setModel(new DefaultComboBoxModel(LAF.values()));
		column.definition(lookAndFeelComboBox);

		column.term(config.get("scale").read(new JLabel()));
		scaleSlider.setMajorTickSpacing(1);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setPaintLabels(true);
		column.definition(scaleSlider);

		column.term(config.get("fullScreen").read(new JLabel()));
		column.definition(config.get("fullScreenAutoScroll")
				.read(fullScreenAutoScrollCheckBox));

		column.term(config.get("historyMax").read(new JLabel()));
		column.definition(historyMaxSpinner);

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		Locale locale = this.locale.getValue();
		if (locale == null) {
			localeDefaultRadioButton.setSelected(true);
			localeComboBox.setEnabled(false);
			localeComboBox.setSelectedItem(LocaleUtils.getDefault());
		} else {
			localeOtherRadioButton.setSelected(true);
			localeComboBox.setEnabled(true);
			localeComboBox.setSelectedItem(locale);
		}

		lookAndFeelComboBox.setSelectedItem(lookAndFeel.getValue());
		scaleSlider.setValue(scale.getValue());
		fullScreenAutoScrollCheckBox
				.setSelected(fullScreenAutoScroll.getValue());

		historyMaxSpinner.setValue(historyMax.getValue());
	}

	@Override
	protected void write() {
		if (localeDefaultRadioButton.isSelected()) {
			locale.setValue(null);
		} else {
			locale.setValue((Locale) localeComboBox.getSelectedItem());
		}

		lookAndFeel.setValue((LAF) lookAndFeelComboBox.getSelectedItem());
		scale.setValue(scaleSlider.getValue());
		fullScreenAutoScroll
				.setValue(fullScreenAutoScrollCheckBox.isSelected());

		historyMax.setValue((Integer) historyMaxSpinner.getValue());
	}
}