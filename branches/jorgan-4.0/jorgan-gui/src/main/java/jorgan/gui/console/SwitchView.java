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

import jorgan.disposition.Shortcut;
import jorgan.disposition.Switch;
import jorgan.skin.Anchor;
import jorgan.skin.ButtonLayer;
import jorgan.skin.Fill;
import jorgan.skin.Layer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

/**
 * A view for a {@link Switch}.
 */
public class SwitchView<E extends Switch> extends EngageableView<E> {

	public static final String BINDING_ACTIVE = "active";

	public static final String BINDING_ACTIVATE = "activate";

	public static final String BINDING_DEACTIVATE = "deactivate";

	/**
	 * Constructor.
	 * 
	 * @param element
	 *            the element to view
	 */
	public SwitchView(E element) {
		super(element);
	}

	@Override
	protected void initBindings() {
		super.initBindings();

		setBinding(BINDING_ACTIVE, new ButtonLayer.Binding() {
			public boolean isPressable() {
				return true;
			}

			public boolean isPressed() {
				return getElement().isActive();
			}

			public void pressed() {
				if (getElement().getDuration() == Switch.DURATION_NONE) {
					// keep activate until #released()
					getElement().setActive(true);
				} else if (getElement().getDuration() == Switch.DURATION_INFINITE) {
					// always activate
					getElement().activate();
				} else {
					if (getElement().isActive()) {
						getElement().deactivate();
					} else {
						getElement().activate();
					}
				}
			}

			public void released() {
				if (getElement().getDuration() == Switch.DURATION_NONE) {
					// was kept activate in #pressed()
					getElement().setActive(false);
				}
			};
		});

		setBinding(BINDING_ACTIVATE, new ButtonLayer.Binding() {
			public boolean isPressable() {
				return true;
			}

			public boolean isPressed() {
				return false;
			}

			public void pressed() {
				getElement().activate();
			}

			public void released() {
			};
		});

		setBinding(BINDING_DEACTIVATE, new ButtonLayer.Binding() {
			public boolean isPressable() {
				return true;
			}

			public boolean isPressed() {
				return false;
			}

			public void pressed() {
				getElement().deactivate();
			}

			public void released() {
			};
		});
	}

	@Override
	protected Style createDefaultStyle() {
		Style style = new Style();

		style.addChild(createTextLayer());

		style.addChild(createButtonLayer());

		return style;
	}

	private Layer createTextLayer() {
		TextLayer layer = new TextLayer();
		layer.setBinding(BINDING_NAME);
		layer.setPadding(new Insets(4, 4 + 13 + 4, 4, 4));
		layer.setAnchor(Anchor.LEFT);
		layer.setFont(getDefaultFont());
		layer.setColor(getDefaultColor());

		return layer;
	}

	private Layer createButtonLayer() {
		ButtonLayer layer = new ButtonLayer();
		layer.setBinding(BINDING_ACTIVE);
		layer.setFill(Fill.BOTH);

		layer.addChild(createCheckLayer(false));

		layer.addChild(createCheckLayer(true));

		return layer;
	}

	private Layer createCheckLayer(final boolean activated) {
		Layer layer = new Layer() {
			@Override
			protected void draw(Graphics2D g, int x, int y, int width,
					int height) {
				g.setColor(getDefaultColor());

				g.drawRect(x, y, width - 1, height - 1);

				if (activated) {
					g.drawLine(x, y, x + width - 1, y + height - 1);
					g.drawLine(x + width - 1, y, x, y + height - 1);
				}
			}
		};
		layer.setWidth(13);
		layer.setHeight(13);
		layer.setPadding(new Insets(4, 4, 4, 4));
		layer.setAnchor(Anchor.LEFT);

		return layer;
	}

	@Override
	public void paint(Graphics2D g) {
		super.paint(g);

		paintShortcut(g);
	}

	protected void paintShortcut(Graphics2D g) {
		if (isShowShortcut()) {
			Shortcut shortcut = getElement().getShortcut();
			if (shortcut != null) {
				g.setFont(getShortcutFont());
				g.setColor(getShortcutColor());

				g.drawString(shortcut.toString(), getX(), getY() + getHeight());
			}
		}
	}
}