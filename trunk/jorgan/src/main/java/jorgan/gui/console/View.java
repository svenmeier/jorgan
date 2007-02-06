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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganEvent;
import jorgan.gui.ConsolePanel;
import jorgan.gui.Elements;
import jorgan.skin.Skin;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

/**
 * Base class of all views representing a view on one element of an organ.
 */
public class View {

	/**
	 * The key of the {@link Element#getName()} text for {@link TextLayer}s.
	 */
	public static final String TEXT_NAME = "name";

	/**
	 * The key of the {@link Element#getDescription()} text for
	 * {@link TextLayer}s.
	 */
	public static final String TEXT_DESCRIPTION = "description";

	/**
	 * The resource bundle.
	 */
	protected static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.i18n");

	protected Dimension size = new Dimension();

	protected Point location = new Point();

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
	private Element element;

	private Map texts = new HashMap();

	/**
	 * Create a view for the given element.
	 * 
	 * @param element
	 *            element to create view for
	 */
	public View(Element element) {

		this.element = element;
	}

	protected void setText(String name, String text) {
		texts.put(name, text);
	}

	/**
	 * Get the text for the given key.
	 * 
	 * @param key
	 *            key to get text for
	 * @return text
	 */
	public String getText(String key) {
		String text = (String) texts.get(key);

		if (text == null) {
			text = "";
		}

		return text;
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
	public Element getElement() {
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

		return (location.x < x) && (location.x + size.width > x)
				&& (location.y < y) && (location.y + size.height > y);
	}

	/**
	 * Update this view in response to a change of an element.
	 * 
	 * @param event
	 *            event of disposition
	 */
	public void changeUpdate(OrganEvent event) {

		// issure repaint so old location gets cleared
		// in case of a changed bounds
		repaint();

		initLocation();

		initTexts();

		initStyle();

		repaint();
	}

	protected void initLocation() {
		location = consolePanel.getLocation(element);
	}

	protected void initTexts() {
		setText(TEXT_NAME, Elements.getDisplayName(getElement()));
		setText(TEXT_DESCRIPTION, getElement().getDescription());
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
		return size.width;
	}

	/**
	 * Get height.
	 * 
	 * @return height
	 */
	public int getHeight() {
		return size.height;
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

	protected Color getDefaultColor() {
		return Color.BLACK;
	}

	protected Style createDefaultStyle() {
		Style style = new Style();

		TextLayer layer = new TextLayer();
		layer.setText("${" + TEXT_NAME + "}");
		layer.setPadding(new Insets(4, 4, 4, 4));
		layer.setFont(Configuration.instance().getFont());
		layer.setColor(getDefaultColor());
		style.addChild(layer);

		return style;
	}
}