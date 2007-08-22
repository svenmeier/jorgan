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

import java.awt.Graphics2D;
import java.awt.Insets;

import jorgan.disposition.Initiator;
import jorgan.skin.ButtonLayer;
import jorgan.skin.Layer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

/**
 * A view that shows an Initiator.
 * 
 * @see jorgan.disposition.Initiator
 */
public class InitiatorView extends MomentaryView<Initiator> {

	private boolean pressed;

	/**
	 * Constructor.
	 * 
	 * @param initiator
	 *            the initiator to view
	 */
	public InitiatorView(Initiator initiator) {
		super(initiator);
	}

	protected void shortcutReleased() {
		Initiator initiator = getElement();

		initiator.initiate();
	}

	public boolean isButtonPressed() {
		return pressed;
	}

	public void buttonPressed() {
		getElement().initiate();

		pressed = true;
		repaint();
	}

	public void buttonReleased() {
		pressed = false;
		repaint();
	}

	protected Style createDefaultStyle() {
		Style style = new Style();

		style.addChild(createTextLayer());

		style.addChild(createButtonLayer());

		return style;
	}

	private Layer createTextLayer() {
		TextLayer layer = new TextLayer();
		layer.setText("${" + TEXT_NAME + "}");
		layer.setPadding(new Insets(4, 4, 4, 4));
		layer.setFont(getDefaultFont());
		layer.setColor(getDefaultColor());

		return layer;
	}

	private Layer createButtonLayer() {

		ButtonLayer layer = new ButtonLayer();
		layer.setEnabled(true);
		layer.setFill(ButtonLayer.BOTH);

		layer.addChild(createBorderLayer(false));

		layer.addChild(createBorderLayer(true));

		return layer;
	}

	private Layer createBorderLayer(final boolean pressed) {
		Layer layer = new Layer() {
			protected void draw(Graphics2D g, int x, int y, int width,
					int height) {

				g.setColor(getDefaultColor());

				g.drawRect(x, y, width - 1, height - 1);

				if (pressed) {
					g.drawRect(x + 1, y + 1, width - 3, height - 3);
				}
			}
		};
		layer.setFill(Layer.BOTH);

		return layer;
	}
}