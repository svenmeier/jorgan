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
package jorgan.swing.text;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A textArea used as a lable with multiple lines.
 */
public class MultiLineLabel extends JTextArea {

	/**
	 * Constructor.
	 */
	public MultiLineLabel() {
		this(0);
	}

	/**
	 * Constructor.
	 */
	public MultiLineLabel(String text) {
		this(0);

		setText(text);
	}

	/**
	 * Constructor.
	 * 
	 * @param rows
	 *            number of rows
	 */
	public MultiLineLabel(int rows) {
		super(rows, 0);

		setFont(new JTextField().getFont());
		setLineWrap(true);
		setWrapStyleWord(true);
		setEditable(false);
		setBackground(new JLabel().getBackground());
		setMinimumSize(new Dimension(0, 0));
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();

		return new Dimension(0, dim.height);
	}
}
