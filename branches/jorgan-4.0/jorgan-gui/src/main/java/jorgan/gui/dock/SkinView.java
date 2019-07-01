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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Console;
import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.console.View;
import jorgan.gui.console.ViewContainer;
import jorgan.gui.console.spi.ViewRegistry;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.session.OrganSession;
import jorgan.skin.Skin;
import jorgan.skin.SkinManager;
import jorgan.skin.Style;
import jorgan.swing.BaseAction;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 */
public class SkinView extends AbstractView {

	private static final int SIZE = 128;

	private static final Configuration config = Configuration.getRoot().get(
			SkinView.class);

	private JList list;

	private OrganSession session;

	private Displayable displayable;

	private Console console;

	private Skin skin;

	private JFormattedTextField zoomTextField;

	private ZoomInAction zoomInAction = new ZoomInAction();

	private ZoomOutAction zoomOutAction = new ZoomOutAction();

	private List<View<?>> views = new ArrayList<View<?>>();

	private EventHandler eventHandler = new EventHandler();

	private boolean updating = false;

	public SkinView() {
		config.read(this);

		zoomTextField = new JFormattedTextField(new DecimalFormat("0.00"));
		zoomTextField.addActionListener(eventHandler);
		zoomTextField.setColumns(3);

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(eventHandler);
		ToolTipManager.sharedInstance().registerComponent(list);
	}

	@Override
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.lookup(ElementSelection.class).removeListener(
					eventHandler);
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(eventHandler));
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(eventHandler));
			this.session.lookup(ElementSelection.class).addListener(
					eventHandler);
		}

		update();
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	@Override
	protected void addTools(Docked docked) {
		docked.addTool(zoomInAction);
		docked.addTool(zoomTextField);
		zoomTextField.setFocusable(true);
		zoomTextField.setRequestFocusEnabled(true);
		docked.addTool(zoomOutAction);
	}

	private void update() {
		updating = true;

		skin = null;
		console = null;
		displayable = getDisplayable();
		if (displayable != null) {
			console = getConsole(displayable);
			if (console != null) {
				skin = getSkin(console);
			}
		}

		if (console == null) {
			// make sure model and renderer are re-created, otherwise both might
			// hold reference to an already closed disposition
			zoomTextField.setValue(1.0f);
			zoomTextField.setEnabled(false);
			list.setModel(new StylesModel());
			list.setCellRenderer(new StyleRenderer());
			setContent(null);
		} else {
			zoomTextField.setValue(displayable.getZoom());
			zoomTextField.setEnabled(true);
			list.setModel(new StylesModel());
			list.setCellRenderer(new StyleRenderer());
			setContent(new JScrollPane(list));

			String style = displayable.getStyle();
			if (style != null) {
				for (View<?> view : views) {
					if (view.getStyle().getName().equals(style)) {
						list.setSelectedValue(view, true);
						break;
					}
				}
			}
		}

		zoomInAction.update();
		zoomOutAction.update();

		updating = false;
	}

	private Displayable getDisplayable() {
		if (session != null) {
			Element element = session.lookup(ElementSelection.class)
					.getSelectedElement();
			if (element instanceof Displayable) {
				return (Displayable) element;
			}
		}

		return null;
	}

	private Console getConsole(Element element) {
		if (element instanceof Console) {
			return (Console) element;
		} else {
			for (Console console : session.getOrgan().getReferrer(element,
					Console.class)) {
				return console;
			}
		}

		return null;
	}

	private Skin getSkin(Console console) {
		setStatus(null);

		Skin skin = null;
		if (console.getSkin() == null) {
			setStatus(config.get("noSkin").read(new MessageBuilder()).build());
		} else {
			skin = session.lookup(SkinManager.class).getSkin(console);
			if (skin == null) {
				setStatus(config.get("skinFailed").read(new MessageBuilder())
						.build(console.getSkin()));
			}
		}

		return skin;
	}

	private class EventHandler extends OrganAdapter implements
			SelectionListener, ActionListener, ListSelectionListener {
		public void selectionChanged() {
			update();
		}

		@Override
		public void propertyChanged(Element element, String name) {
			if (element == console) {
				update();
			} else if (element == displayable) {
				zoomTextField.setValue(displayable.getZoom());

				for (View<?> view : views) {
					view.update();
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!updating) {
				if (displayable != null) {
					displayable.setZoom(((Number) zoomTextField.getValue())
							.floatValue());
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (!updating) {
				View<?> view = (View<?>) list.getSelectedValue();
				if (view == null) {
					displayable.setStyle(null);
				} else {
					displayable.setStyle(view.getStyle().getName());
				}
			}

		}

		@Override
		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			if (element instanceof Console && Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;
				if (reference.getElement() == SkinView.this.displayable) {
					update();
				}
			}
		}

		@Override
		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			if (element instanceof Console && Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;
				if (reference.getElement() == SkinView.this.displayable) {
					update();
				}
			}
		}
	}

	private class StylesModel extends AbstractListModel {

		public StylesModel() {
			views.clear();

			if (skin != null) {
				for (final Style style : skin.createStyles()) {
					View<?> view = ViewRegistry
							.createView(session, displayable);
					view.setContainer(new ViewContainer() {
						public Component getHost() {
							return list;
						}

						public Point getLocation(
								View<? extends Displayable> view) {
							return new Point(0, 0);
						}

						@Override
						public float getScale(View<? extends Displayable> view) {
							return 1.0f;
						}

						public Style getStyle(View<? extends Displayable> view) {
							return style;
						}

						public void setLocation(
								View<? extends Displayable> view, Point location) {
						}

						public void showPopup(View<? extends Displayable> view,
								JComponent contents) {
						}

						public void hidePopup() {
						}

						public void toFront(Console console) {
						}
					});

					views.add(view);
				}
			}
		}

		public int getSize() {
			return views.size();
		}

		public Object getElementAt(int index) {
			return views.get(index);
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

			setToolTipText(view.getStyle().getName());

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

	private class ZoomInAction extends BaseAction {

		public ZoomInAction() {
			config.get("zoomIn").read(this);
		}

		public void update() {
			setEnabled(console != null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			displayable.setZoom(displayable.getZoom() + 0.1f);
		}
	}

	private class ZoomOutAction extends BaseAction {

		public ZoomOutAction() {
			config.get("zoomOut").read(this);
		}

		public void update() {
			setEnabled(console != null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			displayable.setZoom(displayable.getZoom() - 0.1f);
		}
	}
}