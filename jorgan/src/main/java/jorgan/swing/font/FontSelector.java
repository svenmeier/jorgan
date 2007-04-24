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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import jorgan.App;
import jorgan.swing.StandardDialog;
import bias.Context;

/**
 * Selector of a font.
 */
public class FontSelector extends JPanel {

	private static Context context = App.getBias().get(FontSelector.class);

	/**
	 * The button used to edit the selected font.
	 */
	private JButton button = new JButton();

	private FontPanel panel = new FontPanel();

	/**
	 * Create a new selector.
	 */
	public FontSelector() {
		super(new BorderLayout());

		button.setHorizontalAlignment(JButton.LEFT);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				StandardDialog dialog = StandardDialog
						.create(FontSelector.this);
				context.get("dialog").getValues(dialog);

				dialog.setVisible(true);

				setSelectedFont(getSelectedFont());
			}
		});
		add(button, BorderLayout.CENTER);

		setSelectedFont(new Font("Arial", Font.PLAIN, 12));
	}

	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

	/**
	 * Set the selected font.
	 * 
	 * @param font
	 *            the font to select
	 */
	public void setSelectedFont(Font font) {
		panel.setSelectedFont(font);

		String text;
		if (font == null) {
			text = "-";
		} else {
			String name = font.getName();
			int size = font.getSize();
			String style = panel.formatStyle(font.getStyle());
			text = (name + " " + size + " " + style);
		}

		button.setText(text);
	}

	/**
	 * Get the selected font.
	 * 
	 * @return the selected font
	 */
	public Font getSelectedFont() {
		return panel.getSelectedFont();
	}
}