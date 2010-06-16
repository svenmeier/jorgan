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
package jorgan.skin;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;
import jorgan.swing.FontCache;

/**
 * A text layer.
 */
public class TextLayer extends Layer {

	private Alignment alignment = Alignment.CENTER;

	private Font font;

	private Color color = Color.black;

	private boolean antialiased = false;

	private transient Font scaledFont;

	private transient Lines lines;

	public Font getFont() {
		return font;
	}

	public Color getColor() {
		return color;
	}

	public boolean isAntialiased() {
		return antialiased;
	}

	/**
	 * Calculate the width based on the text's width.
	 * 
	 * @return width the text's width
	 */
	@Override
	protected int calcWidth() {
		return lines.width;
	}

	/**
	 * Calculate the height based on the text's height.
	 * 
	 * @return height the text's heigth
	 */
	@Override
	protected int calcHeight() {
		return lines.height;
	}

	public void setFont(Font font) {
		if (font == null) {
			throw new IllegalArgumentException("font cannot be null");
		}

		this.font = font;
	}

	public void setColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException("color of label cannot be null");
		}
		this.color = color;
	}

	public void setAntialiased(boolean b) {
		antialiased = b;
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	@Override
	public void setView(View<? extends Displayable> view) {
		super.setView(view);

		URL url = resolve(font.getName());
		if (url == null) {
			scaledFont = font.deriveFont(scale(font.getSize()));
		} else {
			scaledFont = FontCache.getFont(url).deriveFont(font.getStyle(),
					scale(font.getSize()));
		}

		String text = "";
		Binding binding = getBinding(Binding.class);
		if (binding != null) {
			text = binding.getText();
		}

		lines = new Lines(text.toString().trim(), view.getContainer().getHost()
				.getFontMetrics(scaledFont));
	}

	@Override
	protected void draw(Graphics2D g, int x, int y, int width, int height) {
		g.setFont(scaledFont);
		g.setColor(color);

		Object wasAntialiased = g
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		if (antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		if (alignment == Alignment.CENTER || alignment == Alignment.RIGHT
				|| alignment == Alignment.LEFT) {
			y += height / 2 - lines.height / 2;
		} else if (alignment == Alignment.BOTTOM
				|| alignment == Alignment.BOTTOM_RIGHT
				|| alignment == Alignment.BOTTOM_LEFT) {
			y += height - lines.height;
		}

		lines.draw(g, x, y, width, height);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, wasAntialiased);
	}

	@Override
	public Object clone() {
		TextLayer clone = (TextLayer) super.clone();

		return clone;
	}

	private class Line {
		private String text;

		private int ascent;

		private int descent;

		private int leading;

		private int width;

		public Line(char[] chars, int start, int length, FontMetrics metrics) {
			this.text = new String(chars, start, length);

			this.ascent = metrics.getAscent();
			this.descent = metrics.getDescent();
			this.leading = metrics.getLeading();

			this.width = metrics.charsWidth(chars, start, length);
		}

		public void draw(Graphics2D g, int x, int y) {

			g.drawString(text, x, y + ascent);
		}
	}

	public static interface Binding extends ViewBinding {
		public String getText();
	}

	private class Lines {

		private List<Line> lines = new ArrayList<Line>();

		private int width;

		private int height;

		public Lines(String text, FontMetrics metrics) {
			char[] chars = text.toCharArray();
			int start = 0;
			int end = chars.length;

			// more characters left?
			while (start != end) {
				// take all remaining characters
				int length = end - start;

				// honour line break (multiple whitespace)
				int lineBreak = text.indexOf("  ", start);
				if (lineBreak != -1) {
					length = lineBreak - start;
				}

				if (getWidth() > 0) {
					int scaledWidth = scale(getWidth());
					// width exceeded?
					while (metrics.charsWidth(chars, start, length) > scaledWidth) {

						// seek word break (single whitespace)
						int wordBreak = text.lastIndexOf(' ', start + length
								- 1);
						if (length > 1) {
							// seek intra word break (hyphen)
							wordBreak = Math.max(wordBreak, text.lastIndexOf(
									'-', start + length - 2) + 1);
						}

						// wordBreak before start?
						if (wordBreak <= start) {
							// decrease length until width fits
							while (length > 1
									&& metrics.charsWidth(chars, start, length) > scaledWidth) {
								length--;
							}
						} else {
							// let wordBreak decide length
							length = wordBreak - start;
						}
					}
				}

				Line line = new Line(chars, start, length, metrics);

				int newHeight = height + line.ascent + line.descent;
				if (lines.size() > 0) {
					newHeight += line.leading;
				}
				if (getHeight() > 0) {
					int scaledHeight = scale(getHeight());
					if (newHeight > scaledHeight) {
						return;
					}
				}
				height = newHeight;
				width = Math.max(width, line.width);
				lines.add(line);

				start = start + length;

				// trim leading whitespace
				while (start < end && chars[start] == ' ') {
					start++;
				}
			}
		}

		public void draw(Graphics2D g, int x, int y, int width, int height) {
			for (int l = 0; l < lines.size(); l++) {
				Line line = lines.get(l);

				if (l > 0) {
					y += line.leading;
				}

				int alignedX = x;
				if (alignment == Alignment.TOP || alignment == Alignment.CENTER
						|| alignment == Alignment.BOTTOM) {
					alignedX = x + width / 2 - line.width / 2;
				} else if (alignment == Alignment.RIGHT
						|| alignment == Alignment.TOP_RIGHT
						|| alignment == Alignment.BOTTOM_RIGHT) {
					alignedX = x + width - line.width;
				}

				line.draw(g, alignedX, y);

				y += line.ascent + line.descent;
			}
		}
	}
}