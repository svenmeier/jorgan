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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.gui.ConsolePanel;
import jorgan.skin.ButtonLayer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;
import jorgan.skin.Layer.ViewBinding;
import bias.Configuration;

/**
 * Base class of all views representing a view on one element of an organ.
 */
public class View<E extends Displayable> {

	private static Configuration config = Configuration.getRoot().get(
			View.class);

	/**
	 * The key of the {@link Element#getName()} text for {@link TextLayer}s.
	 */
	public static final String BINDING_NAME = "name";

	/**
	 * The popup's key of for {@link ButtonLayer}s.
	 */
	public static final String BINDING_POPUP = "popup";

	protected Dimension size = new Dimension();

	private float scale;

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
	 * The container.
	 */
	private ViewContainer container;

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

	protected void setBinding(String name, ViewBinding binding) {
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

	protected void updateBinding(String name) {
		style.updateBinding(name);
	}

	/**
	 * Set the container.
	 * 
	 * @param consolePanel
	 *            containing panel
	 */
	public void setContainer(ViewContainer container) {
		this.container = container;

		if (container != null) {
			update();
		}
	}

	/**
	 * Get the containing {@link ConsolePanel}.
	 * 
	 * @return containing panel
	 */
	public ViewContainer getContainer() {
		return container;
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

		return (location.x < x) && (location.x + Math.round(size.width) > x)
				&& (location.y < y)
				&& (location.y + Math.round(size.height) > y);
	}

	/**
	 * Update this view in response to a change of an element.
	 */
	public void update() {
		initLocation();

		initBindings();

		initStyle();
	}

	public void update(String name) {
	}

	protected void initLocation() {
		location = container.getLocation(this);
	}

	protected void initBindings() {
		bindings.clear();

		setBinding(BINDING_NAME, new TextLayer.Binding() {
			public boolean isPressable() {
				return false;
			}

			public String getText() {
				return Elements.getDisplayName(getElement());
			}
		});

		String description = getElement().getDescription();
		BufferedReader reader = new BufferedReader(
				new StringReader(description));
		while (true) {
			try {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				int equalSign = line.indexOf("=");
				if (equalSign != -1) {
					final String name = line.substring(0, equalSign).trim();
					final String text = line.substring(equalSign + 1).trim();

					if (name.length() > 0) {
						setBinding(name, new TextLayer.Binding() {
							public boolean isPressable() {
								return false;
							}

							public String getText() {
								return text;
							}
						});
					}
				}
			} catch (IOException unexpected) {
				throw new Error(unexpected);
			}
		}

		setBinding(BINDING_POPUP, new ButtonLayer.Binding() {
			public boolean isPressable() {
				return true;
			}

			public boolean isPressed() {
				return false;
			}

			public void pressed() {
				openPopup();
			}

			public void released() {
			}
		});
	}

	private void openPopup() {

		JComponent popupContents = createPopupContents();
		if (popupContents != null) {
			container.showPopup(this, popupContents);
		}
	}

	protected JComponent createPopupContents() {
		return null;
	}

	protected void closePopup() {
		container.hidePopup();
	}

	protected void initStyle() {
		scale = container.getScale(this);

		style = container.getStyle(this);
		if (style == null) {
			style = createDefaultStyle();
		}
		style.setView(this);

		size = style.getSize();
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
		return Math.round(size.width);
	}

	/**
	 * Get height.
	 * 
	 * @return height
	 */
	public int getHeight() {
		return Math.round(size.height);
	}

	/**
	 * Paint this view.
	 * 
	 * @param g
	 *            graphics to paint on
	 */
	public void paint(Graphics2D g) {
		g.translate(location.x, location.y);

		style.draw(g, size);

		g.translate(-location.x, -location.y);
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

		return style.isPressable(x - location.x, y - location.y, size);
	}

	public final void mousePressed(int x, int y) {
		style.mousePressed(x - location.x, y - location.y, size);
	}

	public final void mouseDragged(int x, int y) {
		style.mouseDragged(x - location.x, y - location.y, size);
	}

	public final void mouseReleased(int x, int y) {
		style.mouseReleased(x - location.x, y - location.y, size);
	}

	public void keyPressed(KeyEvent ev) {
	}

	public void keyReleased(KeyEvent ev) {
	}

	protected Style createDefaultStyle() {
		Style style = new Style();

		TextLayer layer = new TextLayer();
		layer.setBinding(BINDING_NAME);
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
	}

	public Font getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(Font font) {
		this.defaultFont = font;
	}

	public Color getShortcutColor() {
		return shortcutColor;
	}

	public void setShortcutColor(Color shortcutColor) {
		this.shortcutColor = shortcutColor;
	}

	public Font getShortcutFont() {
		return shortcutFont;
	}

	public void setShortcutFont(Font shortcutFont) {
		this.shortcutFont = shortcutFont;
	}

	public boolean isShowShortcut() {
		return showShortcut;
	}

	public void setShowShortcut(boolean showShortcut) {
		this.showShortcut = showShortcut;
	}

	public Style getStyle() {
		return style;
	}

	public float getScale() {
		return scale;
	}
}