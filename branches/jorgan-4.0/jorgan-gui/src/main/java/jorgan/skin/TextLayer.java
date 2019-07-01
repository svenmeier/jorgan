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
import java.awt.geom.AffineTransform;
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

	private transient Paragraph paragraph;

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
		return scale(paragraph.width);
	}

	/**
	 * Calculate the height based on the text's height.
	 * 
	 * @return height the text's heigth
	 */
	@Override
	protected int calcHeight() {
		return scale(paragraph.height);
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

		String text = "";
		Binding binding = getBinding(Binding.class);
		if (binding != null) {
			text = binding.getText();
		}

		paragraph = new Paragraph(text.toString().trim(), this.font);
	}

	@Override
	protected void draw(Graphics2D g, int x, int y, int width, int height) {
		g.setColor(color);

		AffineTransform transform = g.getTransform();
		Object wasAntialiased = g
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		if (antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g.translate(x, y);
		g.scale(view.getScale(), view.getScale());
		paragraph.draw(g, unscale(width), unscale(height));

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, wasAntialiased);
		g.setTransform(transform);
	}

	@Override
	public Object clone() {
		TextLayer clone = (TextLayer) super.clone();

		return clone;
	}

	/**
	 * A paragraph of text - note that all calculations in this class (and
	 * {@link Line}) are based on non-scaled values.
	 */
	private class Paragraph {

		private List<Line> lines = new ArrayList<Line>();

		private int ascent;

		private int descent;

		private int leading;

		private int width;

		private int height;

		private Font font;

		public Paragraph(String text, Font font) {
			URL url = resolve(font.getName());
			if (url == null) {
				this.font = font;
			} else {
				this.font = FontCache.getFont(url).deriveFont(font.getStyle(),
						font.getSize());
			}

			int maxWidth = getWidth();
			int maxHeight = getHeight();

			FontMetrics metrics = view.getContainer().getHost().getFontMetrics(
					this.font);
			this.ascent = metrics.getAscent();
			this.descent = metrics.getDescent();
			this.leading = metrics.getLeading();

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

				if (maxWidth > 0) {
					// width exceeded?
					while (metrics.charsWidth(chars, start, length) > maxWidth) {

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
							while (metrics.charsWidth(chars, start, length) > maxWidth) {
								length--;
								if (length == 0) {
									// even single character doesn't fit
									return;
								}
							}
						} else {
							// let wordBreak decide length
							length = wordBreak - start;
						}
					}
				}

				int newHeight = height + ascent + descent;
				if (lines.size() > 0) {
					newHeight += leading;
				}
				if (maxHeight > 0) {
					if (newHeight > maxHeight) {
						return;
					}
				}

				Line line = new Line(chars, start, length, metrics);
				lines.add(line);
				width = Math.max(width, line.width);
				height = newHeight;

				start = start + length;

				// trim leading whitespace
				while (start < end && chars[start] == ' ') {
					start++;
				}
			}
		}

		public void draw(Graphics2D g, int width, int height) {
			g.setFont(this.font);

			int x = 0;
			int y = 0;

			if (alignment == Alignment.CENTER || alignment == Alignment.RIGHT
					|| alignment == Alignment.LEFT) {
				y += height / 2 - this.height / 2;
			} else if (alignment == Alignment.BOTTOM
					|| alignment == Alignment.BOTTOM_RIGHT
					|| alignment == Alignment.BOTTOM_LEFT) {
				y += height - this.height;
			}

			for (int l = 0; l < lines.size(); l++) {
				Line line = lines.get(l);

				if (l > 0) {
					y += leading;
				}

				line.draw(g, x, width, y);

				y += ascent + descent;
			}
		}

		private class Line {
			private String text;

			private int width;

			public Line(char[] chars, int start, int length, FontMetrics metrics) {
				this.text = new String(chars, start, length);

				this.width = metrics.charsWidth(chars, start, length);
			}

			public void draw(Graphics2D g, int x, int width, int y) {

				if (alignment == Alignment.TOP || alignment == Alignment.CENTER
						|| alignment == Alignment.BOTTOM) {
					x = x + width / 2 - this.width / 2;
				} else if (alignment == Alignment.RIGHT
						|| alignment == Alignment.TOP_RIGHT
						|| alignment == Alignment.BOTTOM_RIGHT) {
					x = x + width - this.width;
				}

				g.drawString(text, x, y + ascent);
			}
		}
	}

	public static interface Binding extends ViewBinding {
		public String getText();
	}

}