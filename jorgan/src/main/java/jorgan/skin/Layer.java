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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.net.URL;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;

/**
 * A layer.
 */
public abstract class Layer implements Cloneable {

	private Anchor anchor = Anchor.CENTER;

	private Fill fill = Fill.NONE;

	private int width;

	private int height;

	private Insets padding = new Insets(0, 0, 0, 0);

	private String binding = null;

	private transient Resolver resolver = new Resolver() {
		public URL resolve(String name) {
			return null;
		}
	};

	protected transient View<? extends Displayable> view;

	public void setResolver(Resolver resolver) {
		this.resolver = resolver;
	}

	public URL resolve(String name) {
		return this.resolver.resolve(name);
	}

	public void setView(View<? extends Displayable> view) {
		this.view = view;
	}

	public View<? extends Displayable> getView() {
		return view;
	}

	public void draw(Graphics2D g, Dimension dimension) {

		Rectangle rectangle = getUnpaddedBounds(dimension);

		draw(g, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	protected Rectangle getUnpaddedBounds(Dimension size) {
		Rectangle rectangle = new Rectangle(0, 0, getUnpaddedWidth(),
				getUnpadddedHeight());

		if (fill == Fill.BOTH || fill == Fill.HORIZONTAL) {
			rectangle.width = size.width - padding.left - padding.right;
		}
		if (fill == Fill.BOTH || fill == Fill.VERTICAL) {
			rectangle.height = size.height - padding.top - padding.bottom;
		}

		if (anchor == Anchor.TOP_LEFT || anchor == Anchor.LEFT || anchor == Anchor.BOTTOM_LEFT) {
			rectangle.x = padding.left;
		} else if (anchor == Anchor.TOP_RIGHT || anchor == Anchor.RIGHT
				|| anchor == Anchor.BOTTOM_RIGHT) {
			rectangle.x = size.width - padding.right - rectangle.width;
		} else {
			rectangle.x = padding.left
					+ (size.width - padding.left - padding.right) / 2
					- rectangle.width / 2;
		}

		if (anchor == Anchor.TOP_LEFT || anchor == Anchor.TOP || anchor == Anchor.TOP_RIGHT) {
			rectangle.y = padding.top;
		} else if (anchor == Anchor.BOTTOM_LEFT || anchor == Anchor.BOTTOM
				|| anchor == Anchor.BOTTOM_RIGHT) {
			rectangle.y = size.height - padding.bottom - rectangle.height;
		} else {
			rectangle.y = padding.top
					+ (size.height - padding.top - padding.bottom) / 2
					- rectangle.height / 2;
		}

		return rectangle;
	}

	protected void draw(Graphics2D g, int x, int y, int width, int height) {
	}

	@Override
	public Object clone() {
		try {
			Layer clone = (Layer) super.clone();

			clone.setPadding(new Insets(padding.top, padding.left,
					padding.bottom, padding.right));

			return clone;
		} catch (CloneNotSupportedException ex) {
			throw new Error();
		}
	}

	public void setFill(Fill fill) {
		this.fill = fill;
	}

	public Fill getFill() {
		return fill;

	}

	public void setPadding(Insets padding) {
		this.padding = padding;
	}

	public Insets getPadding() {
		return padding;
	}

	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public Dimension getSize() {
		return new Dimension(getUnpaddedWidth() + padding.left + padding.right,
				getUnpadddedHeight() + padding.top + padding.bottom);

	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	protected int getUnpaddedWidth() {
		if (this.width == 0) {
			return calcWidth();
		} else {
			return this.width;
		}
	}

	/**
	 * Calculate the width in case is is not explicitely set, i.e. it is
	 * <code>0</code>.
	 * 
	 * @return the calculated width
	 */
	protected int calcWidth() {
		return 0;
	}

	protected int getUnpadddedHeight() {
		if (this.width == 0) {
			return calcHeight();
		} else {
			return this.height;
		}
	}

	/**
	 * Calculate the height in case is is not explicitely set, i.e. it is
	 * <code>0</code>.
	 * 
	 * @return the calculated height
	 */
	protected int calcHeight() {
		return 0;
	}

	public boolean isPressable(int x, int y, Dimension dimension) {
		ViewBinding binding = getBinding(ViewBinding.class); 
		if (binding != null && binding.isPressable()) {
			Rectangle rectangle = getUnpaddedBounds(dimension);

			return rectangle.contains(x, y);
		}

		return false;
	}

	public void mousePressed(int x, int y, Dimension size) {
	}

	public void mouseDragged(int x, int y, Dimension size) {
	}

	public void mouseReleased(int x, int y, Dimension size) {
	}

	protected void released() {
	}

	public String getBinding() {
		return binding;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	protected <C> C getBinding(Class<C> clazz) {
		return view.getBinding(binding, clazz);
	}
	
    public static interface ViewBinding {
    	
    	public boolean isPressable();
    }
}