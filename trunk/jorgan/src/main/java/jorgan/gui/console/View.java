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
package jorgan.gui.console;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.event.OrganEvent;
import jorgan.gui.ConsolePanel;
import jorgan.skin.Skin;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;
import bias.Configuration;

/**
 * Base class of all views representing a view on one element of an organ.
 */
public class View<E extends Element> {

	private static Configuration config = Configuration.getRoot().get(
			View.class);

	/**
	 * The key of the {@link Element#getName()} text for {@link TextLayer}s.
	 */
	public static final String CONTROL_NAME = "name";

	/**
	 * The key of the {@link Element#getDescription()} text for
	 * {@link TextLayer}s.
	 */
	public static final String CONTROL_DESCRIPTION = "description";

	protected Dimension size = new Dimension();

	private float zoom;

	protected Point location = new Point();

	private Font defaultFont = new Font("Arial", Font.PLAIN, 12);

	private Color defaultColor = new Color(0, 0, 255);

	private boolean showShortcut;

	private Font shortcutFont;

	private Color shortcutColor;

	/**
	 * The style of this view.
	 */
	protected Style style;

	/**
	 * The containing viewPanel.
	 */
	private ConsolePanel consolePanel;

	/**
	 * The element represented by this view.
	 */
	private E element;

	private Map<String, Object> bindings = new HashMap<String, Object>();

	/**
	 * Create a view for the given element.
	 * 
	 * @param element
	 *            element to create view for
	 */
	public View(E element) {

		this.element = element;

		config.read(this);
	}

	protected void setBinding(String name, Object binding) {
		bindings.put(name, binding);
	}

	/**
	 * Get the binding for the given key.
	 * 
	 * @param key
	 *            key to get binding for
	 * @return binding
	 */
	public <T> T getBinding(String key, Class<T> type) {
		Object binding = bindings.get(key);

		if (binding != null) {
			if (!type.isInstance(binding)) {
				binding = null;
			}
		}
		return type.cast(binding);
	}

	/**
	 * Set the containing {@link ConsolePanel}.
	 * 
	 * @param consolePanel
	 *            containing panel
	 */
	public void setConsolePanel(ConsolePanel consolePanel) {
		this.consolePanel = consolePanel;

		if (consolePanel != null) {
			changeUpdate(null);
		}
	}

	/**
	 * Get the containing {@link ConsolePanel}.
	 * 
	 * @return containing panel
	 */
	public ConsolePanel getConsolePanel() {
		return consolePanel;
	}

	/**
	 * Get the element represented by this view.
	 * 
	 * @return the element
	 */
	public E getElement() {
		return element;
	}

	/**
	 * Is the point with the given coordinates contained.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return containment
	 */
	public boolean contains(int x, int y) {

		return (location.x < x)
				&& (location.x + Math.round(size.width * zoom) > x)
				&& (location.y < y)
				&& (location.y + Math.round(size.height * zoom) > y);
	}

	/**
	 * Update this view in response to a change of an element.
	 * 
	 * @param event
	 *            event of disposition
	 */
	public void changeUpdate(OrganEvent event) {

		if (consolePanel != null) {
			// issure repaint so old location gets cleared
			// in case of a changed bounds
			repaint();

			initLocation();

			initBindings();

			initStyle();

			repaint();
		}
	}

	protected void initLocation() {
		location = consolePanel.getLocation(element);
	}

	protected void initBindings() {
		setBinding(CONTROL_NAME, new TextLayer.Binding() {
			public boolean isPressable() {
				return false;
			}

			public String getText() {
				return Elements.getDisplayName(getElement());
			}
		});

		setBinding(CONTROL_DESCRIPTION, new TextLayer.Binding() {
			public boolean isPressable() {
				return false;
			}

			public String getText() {
				return getElement().getDescription();
			}
		});
	}

	protected void initStyle() {
		style = null;

		Skin skin = consolePanel.getSkin();
		if (skin != null) {
			String styleName = element.getStyle();
			style = skin.createStyle(styleName);
		}
		if (style == null) {
			style = createDefaultStyle();
		}
		style.setView(this);

		size = style.getSize();

		zoom = element.getZoom();
	}

	protected void repaint() {
		consolePanel.repaintView(this);
	}

	/**
	 * Get x.
	 * 
	 * @return x
	 */
	public int getX() {
		return location.x;
	}

	/**
	 * Get y.
	 * 
	 * @return y
	 */
	public int getY() {
		return location.y;
	}

	/**
	 * Get width.
	 * 
	 * @return width
	 */
	public int getWidth() {
		return Math.round(size.width * zoom);
	}

	/**
	 * Get height.
	 * 
	 * @return height
	 */
	public int getHeight() {
		return Math.round(size.height * zoom);
	}

	/**
	 * Paint this view.
	 * 
	 * @param g
	 *            graphics to paint on
	 */
	public void paint(Graphics2D g) {
		if (zoom == 1.0f) {
			// premature optimization ??
			g.translate(location.x, location.y);

			style.draw(g, size);

			g.translate(-location.x, -location.y);
		} else {
			AffineTransform transform = g.getTransform();

			g.translate(location.x, location.y);
			g.scale(zoom, zoom);

			style.draw(g, size);

			g.setTransform(transform);
		}
	}

	/**
	 * Is this view pressable on the given coordinate.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return pressable
	 */
	public final boolean isPressable(int x, int y) {

		return style.isPressable(Math.round((x - location.x) / zoom), Math
				.round((y - location.y) / zoom), size);
	}

	public final void mousePressed(int x, int y) {
		style.mousePressed(Math.round((x - location.x) / zoom), Math
				.round((y - location.y) / zoom), size);
	}

	public final void mouseDragged(int x, int y) {
		style.mouseDragged(Math.round((x - location.x) / zoom), Math
				.round((y - location.y) / zoom), size);
	}

	public final void mouseReleased(int x, int y) {
		style.mouseReleased(Math.round((x - location.x) / zoom), Math
				.round((y - location.y) / zoom), size);
	}

	public void keyPressed(KeyEvent ev) {
	}

	public void keyReleased(KeyEvent ev) {
	}

	protected Style createDefaultStyle() {
		Style style = new Style();

		TextLayer layer = new TextLayer();
		layer.setBinding(CONTROL_NAME);
		layer.setPadding(new Insets(4, 4, 4, 4));
		layer.setFont(getDefaultFont());
		layer.setColor(getDefaultColor());
		style.addChild(layer);

		return style;
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color color) {
		this.defaultColor = color;

		changeUpdate(null);
	}

	public Font getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(Font font) {
		this.defaultFont = font;

		changeUpdate(null);
	}

	public Color getShortcutColor() {
		return shortcutColor;
	}

	public void setShortcutColor(Color shortcutColor) {
		this.shortcutColor = shortcutColor;

		changeUpdate(null);
	}

	public Font getShortcutFont() {
		return shortcutFont;
	}

	public void setShortcutFont(Font shortcutFont) {
		this.shortcutFont = shortcutFont;

		changeUpdate(null);
	}

	public boolean isShowShortcut() {
		return showShortcut;
	}

	public void setShowShortcut(boolean showShortcut) {
		this.showShortcut = showShortcut;

		changeUpdate(null);
	}
}