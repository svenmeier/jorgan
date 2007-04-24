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
package jorgan.swing.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.App;
import jorgan.swing.EditableList;
import jorgan.swing.GridBuilder;
import bias.Context;

/**
 * A panel for a font selection.
 */
class FontPanel extends JPanel {

	private static Logger logger = Logger.getLogger(FontPanel.class.getName());

	private static Context context = App.getBias().get(FontPanel.class);

	private static String[] sizes = new String[] { "8", "10", "12", "14", "16",
			"18", "24" };

	private JLabel familyLabel = new JLabel();

	private JLabel sizeLabel = new JLabel();

	private JLabel styleLabel = new JLabel();

	private EditableList familyList = new EditableList();

	private EditableList sizeList = new EditableList();

	private EditableList stylesList = new EditableList();

	private Font font;

	/**
	 * Constructor.
	 */
	public FontPanel() {
		super(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d, 0.0d, 0.0d });

		builder.nextRow();

		context.get("familyLabel").getValues(familyLabel);
		context.get("sizeLabel").getValues(sizeLabel);
		context.get("styleLabel").getValues(styleLabel);

		add(familyLabel, builder.nextColumn());
		add(sizeLabel, builder.nextColumn());
		add(styleLabel, builder.nextColumn());

		builder.nextRow(1.0d);

		familyList.setValues(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames());
		add(familyList, builder.nextColumn().fillBoth());

		sizeList.setValues(sizes);
		add(sizeList, builder.nextColumn().fillBoth());

		context.get("stylesList").getValues(stylesList);
		add(stylesList, builder.nextColumn().fillBoth());
	}

	/**
	 * Set the selected font.
	 * 
	 * @param font
	 *            the selected font
	 */
	public void setSelectedFont(Font font) {
		this.font = font;

		if (font != null) {
			familyList.setSelectedValue("" + font.getFamily());
			sizeList.setSelectedValue("" + font.getSize());
			stylesList.setSelectedValue(formatStyle(font.getStyle()));
		}
	}

	/**
	 * Get the selected font.
	 * 
	 * @return the selected font
	 */
	public Font getSelectedFont() {
		try {
			int size = Integer.parseInt(sizeList.getSelectedValue());
			int style = stylesList.getSelectedIndex();
			String family = familyList.getSelectedValue();

			font = new Font(family, style, size);
		} catch (Exception ex) {
			logger.log(Level.FINE, "font construction failed", ex);
		}
		return font;
	}

	/**
	 * Format the given style.
	 * 
	 * @param style
	 *            style to format
	 * @return formatted style
	 */
	public String formatStyle(int style) {
		return stylesList.getValues().get(font.getStyle());
	}
}