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
package jorgan.recorder.swing;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;

public abstract class AdaptingLabel extends JComponent {

	protected abstract String getText();

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, getFont().getSize());
	}

	@Override
	public void paint(Graphics g) {
		Insets insets = getInsets();
		int width = getWidth();
		int height = getHeight();

		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);

		String text = getText();

		float size = (float) (height - insets.top - insets.bottom);
		g.setFont(getFont().deriveFont(size));

		FontMetrics metrics = g.getFontMetrics();
		int textWidth = metrics.stringWidth(text);

		float factor = textWidth / (float) (width - insets.left - insets.right);
		if (factor > 1.0f) {
			size = size / factor;

			g.setFont(getFont().deriveFont(size));

			metrics = g.getFontMetrics();
			textWidth = metrics.stringWidth(text);
		}

		int textAscent = metrics.getAscent();
		int textDescent = metrics.getDescent();

		g.setColor(getForeground());
		g.drawString(text, width / 2 - textWidth / 2, height / 2
				+ (textAscent + textDescent) / 2 - textDescent);
	}
}
