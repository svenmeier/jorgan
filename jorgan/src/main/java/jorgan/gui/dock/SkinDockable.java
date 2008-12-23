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
package jorgan.gui.dock;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Console;
import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.gui.console.View;
import jorgan.gui.console.ViewContainer;
import jorgan.gui.console.spi.ProviderRegistry;
import jorgan.session.OrganSession;
import jorgan.session.event.ElementSelectionEvent;
import jorgan.session.event.ElementSelectionListener;
import jorgan.skin.Skin;
import jorgan.skin.SkinManager;
import jorgan.skin.Style;
import jorgan.swing.SpinnerSlider;
import swingx.docking.Docked;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 */
public class SkinDockable extends OrganDockable {

	private static final int SIZE = 128;

	private static final Configuration config = Configuration.getRoot().get(
			SkinDockable.class);

	private JList list;

	private OrganSession session;

	private Displayable element;

	private Console console;

	private Skin skin;

	private SpinnerSlider slider;

	private List<View<?>> styles = new ArrayList<View<?>>();

	private EventHandler eventHandler = new EventHandler();

	private boolean updating = false;

	public SkinDockable() {
		config.read(this);

		slider = new SpinnerSlider(Math.round(Displayable.MIN_ZOOM * 100), 100,
				Math.round(Displayable.MAX_ZOOM * 100));
		slider.setEnabled(false);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					element.setZoom(slider.getValue() / 100.0f);
				}
			}
		});

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.setCellRenderer(new StyleRenderer());
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!updating) {
					View<?> style = (View<?>) list.getSelectedValue();
					if (style == null) {
						element.setStyle(null);
					} else {
						element.setStyle(style.getStyle().getName());
					}
				}
			}
		});
	}

	@Override
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.removeSelectionListener(eventHandler);
			this.session.removeOrganListener(eventHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(eventHandler);
			this.session.addSelectionListener(eventHandler);
		}

		update();
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		docked.addTool(slider);
	}

	private void update() {
		updating = true;

		setStatus(null);

		skin = null;
		element = null;
		console = null;

		if (session != null) {
			Element element = session.getElementSelection()
					.getSelectedElement();
			if (element != null && element instanceof Displayable) {
				this.element = (Displayable) element;

				if (element instanceof Console) {
					this.console = (Console) element;
				} else {
					for (Console console : session.getOrgan().getReferrer(
							element, Console.class)) {
						this.console = console;
						break;
					}
				}

				if (this.console != null) {
					if (this.console.getSkin() == null) {
						setStatus(config.get("noSkin").read(
								new MessageBuilder()).build());
					} else {
						try {
							File file = session.resolve(this.console.getSkin());
							this.skin = SkinManager.instance().getSkin(file);
						} catch (IOException ex) {
							setStatus(config.get("skinFailed").read(
									new MessageBuilder()).build());
						}
					}
				}
			}
		}

		if (skin == null) {
			setContent(null);
			slider.setEnabled(false);
		} else {
			list.setModel(new StylesModel());
			setContent(new JScrollPane(list));
			slider.setEnabled(true);
		}

		if (element != null && element.getStyle() != null) {
			for (View<?> view : styles) {
				if (view.getStyle().getName().equals(element.getStyle())) {
					list.setSelectedValue(view, true);
					break;
				}
			}
		}

		updating = false;
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	private class EventHandler extends OrganAdapter implements
			ElementSelectionListener {
		public void selectionChanged(ElementSelectionEvent ev) {
			update();
		}

		@Override
		public void propertyChanged(Element element, String name) {
			if (element == SkinDockable.this.element || element == console) {
				update();
			}
		}
	}

	private class StylesModel extends AbstractListModel {

		public StylesModel() {
			styles.clear();

			for (final Style style : skin.createStyles()) {
				View<?> view = ProviderRegistry.createView(element);
				view.setContainer(new ViewContainer() {
					public Component getHost() {
						return list;
					}

					public Point getLocation(View<? extends Displayable> view) {
						return new Point(0, 0);
					}

					public Style getStyle(View<? extends Displayable> view) {
						return style;
					}

					public void repaintView(View<? extends Displayable> view) {
					}

					public void setLocation(View<? extends Displayable> view,
							Point location) {
					}
				});

				styles.add(view);
			}
		}

		public int getSize() {
			return styles.size();
		}

		public Object getElementAt(int index) {
			return styles.get(index);
		}
	}

	private class StyleRenderer extends DefaultListCellRenderer implements Icon {
		private View<Displayable> view;

		public StyleRenderer() {
			setIcon(this);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			this.view = (View<Displayable>) value;

			return super.getListCellRendererComponent(list, this, index,
					isSelected, cellHasFocus);
		}

		public int getIconHeight() {
			return SIZE;
		}

		public int getIconWidth() {
			return SIZE;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2D = (Graphics2D) g;
			Shape clip = g2D.getClip();
			g2D.clipRect(x, y, SIZE, SIZE);

			int width = view.getWidth();
			int height = view.getHeight();
			x += SIZE / 2 - width / 2;
			y += SIZE / 2 - height / 2;

			g.translate(x, y);
			view.paint(g2D);
			g.translate(-x, -y);

			g2D.setClip(clip);
		}
	}
}