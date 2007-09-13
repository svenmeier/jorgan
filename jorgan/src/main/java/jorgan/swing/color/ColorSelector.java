/*
 * jOrgan - Java Virtual  Organ
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
package jorgan.swing.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Selector of a color.
 */
public class ColorSelector extends JPanel {

	private static Configuration config = Configuration.getRoot().get(ColorSelector.class);

	/**
	 * The selected color.
	 */
	private Color color;

	/**
	 * The button used to edit the selected font.
	 */
	private JButton button = new JButton();

	/**
	 * Should color be shown as an icon.
	 */
	private boolean showIcon;

	/**
	 * Should color be shown as text.
	 */
	private boolean showText;

	/**
	 * Create a new selector.
	 */
	public ColorSelector() {
		this(true, false);
	}

	/**
	 * Create a new selector.
	 * 
	 * @param showIcon
	 *            should an icon be shown
	 * @param showText
	 *            should text be shown
	 */
	public ColorSelector(boolean showIcon, boolean showText) {
		super(new BorderLayout());

		this.showIcon = showIcon;
		this.showText = showText;

		button.setHorizontalAlignment(JButton.LEFT);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String title = config.get("title").read(new MessageBuilder())
						.build();
				Color newColor = JColorChooser.showDialog(ColorSelector.this,
						title, color);
				if (newColor != null) {
					setSelectedColor(newColor);
				}
			}
		});
		add(button, BorderLayout.CENTER);

		setSelectedColor(Color.black);
	}

	@Override
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

	/**
	 * Set the selected color.
	 * 
	 * @param color
	 *            the color to select
	 */
	public void setSelectedColor(Color color) {
		this.color = color;

		if (showText) {
			if (color == null) {
				button.setText("-");
			} else {
				button.setText(format(color));
			}
		}

		if (showIcon) {
			if (color == null) {
				button.setIcon(null);
			} else {
				button.setIcon(new ColorIcon(color));
			}
		}
	}

	/**
	 * Get the selected color.
	 * 
	 * @return the selected color
	 */
	public Color getSelectedColor() {
		return color;
	}

	/**
	 * Utility method for formatting of a color.
	 * 
	 * @param color
	 *            color to format
	 * @return formatted color
	 */
	public static String format(Color color) {
		if (color == null) {
			return "-";
		} else {
			return (color.getRed() + ", " + color.getGreen() + ", " + color
					.getBlue());
		}
	}
}