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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.swing.EditableList;
import jorgan.swing.layout.TableBuilder;
import jorgan.swing.layout.TableBuilder.Row;
import bias.Configuration;

/**
 * A panel for a font selection.
 */
public class FontPanel extends JPanel {

	private static Logger logger = Logger.getLogger(FontPanel.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			FontPanel.class);

	private static String[] sizes = new String[] { "8", "10", "12", "14", "16",
			"18", "24" };

	private JLabel familyLabel = new JLabel();

	private JLabel sizeLabel = new JLabel();

	private JLabel styleLabel = new JLabel();

	private EditableList familyList = new EditableList();

	private EditableList sizeList = new EditableList();

	private EditableList stylesList = new EditableList();

	private Font font;

	private String[] styles = new String[4];

	/**
	 * Constructor.
	 */
	public FontPanel() {
		config.read(this);

		TableBuilder builder = new TableBuilder(this);

		Row row = builder.row();

		row.data(config.get("family").read(familyLabel), true);
		row.data(config.get("size").read(sizeLabel));
		row.data(config.get("style").read(styleLabel));

		row = builder.row(true);

		familyList.setValuesAsArray(GraphicsEnvironment
				.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		row.data(familyList, true);

		sizeList.setValuesAsArray(sizes);
		row.data(sizeList);

		stylesList.setValuesAsArray(styles);
		row.data(stylesList);
	}

	public void setStyles(String[] styles) {
		this.styles = styles;
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
			logger.log(Level.WARNING, "font construction failed", ex);
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
		return styles[font.getStyle()];
	}
}