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
package jorgan.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;

import jorgan.disposition.Console;
import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Reference;
import jorgan.disposition.Shortcut;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.console.View;
import jorgan.gui.console.ViewContainer;
import jorgan.gui.console.spi.ViewRegistry;
import jorgan.gui.construct.layout.StackVerticalLayout;
import jorgan.gui.construct.layout.ViewLayout;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.skin.Skin;
import jorgan.skin.SkinManager;
import jorgan.skin.Style;
import jorgan.swing.BaseAction;
import jorgan.swing.StandardDialog;
import spin.Spin;
import swingx.Marker;
import swingx.dnd.ObjectTransferable;
import bias.Configuration;

/**
 * Panel that manages views to display a console of an organ.
 */
public class ConsolePanel extends JComponent implements Scrollable,
		ViewContainer {

	private static Configuration config = Configuration.getRoot().get(
			ConsolePanel.class);

	/**
	 * The organ of the edited console.
	 */
	private OrganSession session;

	/**
	 * The edited console.
	 */
	private Console console;

	/**
	 * The skin of the console.
	 */
	private Skin skin;

	/**
	 * The view for the console itself.
	 */
	private ConsoleView consoleView;

	/**
	 * The element to view mapping.
	 */
	private Map<Displayable, View<? extends Displayable>> viewsByDisplayable = new HashMap<Displayable, View<? extends Displayable>>();

	/**
	 * Currently constructing.
	 */
	private boolean constructing = false;

	private int grid = 1;

	private Color popupBackgound = new Color(255, 255, 225);

	/**
	 * The currently pressed view.
	 */
	private View<? extends Displayable> pressedView;

	/**
	 * The currently selected views.
	 */
	private List<View<? extends Displayable>> selectedViews = new ArrayList<View<? extends Displayable>>();

	/**
	 * The listener to organ changes.
	 */
	private EventHandler eventHandler = new EventHandler();

	/**
	 * The listener to mouse (motion) events in play modus.
	 */
	private PlayHandler playHandler = new PlayHandler();

	/**
	 * The listener to mouse (motion) events in construction modus.
	 */
	private ConstructionHandler constructionHandler = new ConstructionHandler();

	/**
	 * The listener to drop events.
	 */
	private DropTargetListener dropTargetListener = new DisplayableDropTargetListener();

	/**
	 * The key handler for shortcuts.
	 */
	private ShortcutHandler shortcutHandler = new ShortcutHandler();

	/*
	 * The menus.
	 */
	private JPopupMenu menu = new JPopupMenu();

	private JMenu alignMenu = new JMenu();

	private JMenu spreadMenu = new JMenu();

	private JMenu arrangeMenu = new JMenu();

	private JMenu sendMenu = new JMenu();

	/**
	 * The arrangements.
	 */
	private Action arrangeToFrontAction = new ArrangeToFrontAction();

	private Action arrangeToBackAction = new ArrangeToBackAction();

	private Action arrangeHideAction = new ArrangeHideAction();

	private StandardDialog popup;

	/**
	 * Create a view panel.
	 */
	public ConsolePanel(OrganSession session, Console console) {
		if (session == null) {
			throw new IllegalArgumentException("session must not be null");
		}
		if (console == null) {
			throw new IllegalArgumentException("console must not be null");
		}
		this.session = session;
		this.session.getOrgan().addOrganListener(
				(OrganListener) Spin.over(eventHandler));
		this.session.lookup(ElementSelection.class).addListener(eventHandler);
		this.session.addListener((SessionListener) Spin.over(eventHandler));

		this.console = console;

		config.read(this);

		// must report to be opaque so containing scrollPane can use blitting
		setOpaque(true);

		// must be focusable to be used in fullScreen
		setFocusable(true);

		ToolTipManager.sharedInstance().registerComponent(this);
		new DropTarget(this, dropTargetListener);

		config.get("alignMenu").read(alignMenu);
		menu.add(alignMenu);

		config.get("spreadMenu").read(spreadMenu);
		menu.add(spreadMenu);

		for (ViewLayout layout : jorgan.gui.construct.layout.spi.LayoutRegistry
				.lookupLayouts()) {
			if (layout.isAlign()) {
				alignMenu.add(new LayoutAction(layout));
			} else {
				spreadMenu.add(new LayoutAction(layout));
			}
		}

		config.get("arrangeMenu").read(arrangeMenu);
		menu.add(arrangeMenu);

		arrangeMenu.add(arrangeToFrontAction);
		arrangeMenu.add(arrangeToBackAction);
		arrangeMenu.add(arrangeHideAction);

		config.get("sendMenu").read(sendMenu);
		menu.add(sendMenu);

		initSkin();

		consoleView = new ConsoleView(console);
		consoleView.setContainer(this);

		for (Reference<? extends Displayable> reference : console
				.getReferences(Console.LocationReference.class)) {
			createView(reference.getElement());
		}

		constructing = !this.session.isConstructing();
		setConstructing(!constructing);
	}

	public void dispose() {
		this.session.getOrgan().removeOrganListener(
				(OrganListener) Spin.over(eventHandler));
		this.session.lookup(ElementSelection.class)
				.removeListener(eventHandler);
		this.session.removeListener(eventHandler);
		this.session = null;

		if (this.popup != null) {
			this.popup.dispose();
			this.popup = null;
		}
	}

	@Override
	public void addNotify() {
		super.addNotify();

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventPostProcessor(shortcutHandler);
	}

	@Override
	public void removeNotify() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventPostProcessor(shortcutHandler);

		super.removeNotify();
	}

	public int getGrid() {
		return grid;
	}

	public void setGrid(int grid) {
		this.grid = grid;
	}

	public void setPopupBackground(Color color) {
		this.popupBackgound = color;
	}

	public Color getPopupBackground() {
		return popupBackgound;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public boolean getScrollableTracksViewportHeight() {
		if (getParent() instanceof JViewport) {
			return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
		}
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		if (getParent() instanceof JViewport) {
			return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
		}
		return false;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.VERTICAL) {
			return visibleRect.height;
		} else {
			return visibleRect.width;
		}
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {

		int dispositionPos;
		if (orientation == SwingConstants.VERTICAL) {
			dispositionPos = screenToDisposition(visibleRect.y);
		} else {
			dispositionPos = screenToDisposition(visibleRect.x);
		}

		int newDispositionPos;
		if (direction > 0) {
			newDispositionPos = dispositionPos + grid;
		} else {
			newDispositionPos = dispositionPos - 1;
		}

		// snap new view position to grid
		newDispositionPos = newDispositionPos - (newDispositionPos % grid);

		int viewIncrement = Math.abs(dispositionPos - newDispositionPos);

		// Ensure an increment of at least 1, since translation to screen
		// can make view increment irrelevant.
		return Math.max(1, dispositionToScreen(viewIncrement));
	}

	/**
	 * Scroll the given element to visible.
	 * 
	 * @param element
	 *            element to scroll to
	 */
	public void scrollDisplayableToVisible(View<?> view) {
		int x1 = view.getX();
		int y1 = view.getY();
		int x2 = view.getX() + view.getWidth();
		int y2 = view.getY() + view.getHeight();

		scrollRectToVisible(new Rectangle(x1, y1, x2 - x1, y2 - y1));
	}

	private int dispositionToScreen(int dispositionPos) {
		return (int) (dispositionPos * console.getZoom());
	}

	private int screenToDisposition(int screenPos) {
		return (int) (screenPos / console.getZoom());
	}

	@Override
	public float getScale(View<? extends Displayable> view) {
		float scale = view.getElement().getZoom();
		if (scale < 0.5f) {
			// don't trust element's zoom
			scale = 0.5f;
		}

		if (view != consoleView) {
			float consoleScale = console.getZoom();
			if (consoleScale < 0.5f) {
				// don't trust element's zoom
				consoleScale = 0.5f;
			}
			scale = scale * consoleScale;
		}

		return scale;
	}

	public Style getStyle(View<? extends Displayable> view) {
		if (skin != null) {
			String styleName = view.getElement().getStyle();
			if (styleName != null) {
				return skin.createStyle(styleName);
			}
		}
		return null;
	}

	public void showPopup(View<? extends Displayable> view, JComponent contents) {
		if (popup == null) {
			popup = StandardDialog.create(this);
			popup.setUndecorated(true);
			popup.setModal(false);
			popup.closeOnFocusLost();
		}

		contents.setBorder(new LineBorder(popupBackgound.darker()));
		contents.setBackground(popupBackgound);
		popup.setBody(contents);

		Point location = getLocationOnScreen();
		location.x += view.getX();
		location.y += view.getY();
		popup.setLocation(location);
		popup.setVisible(true);
	}

	public void hidePopup() {
		if (popup != null) {
			popup.setVisible(false);
		}
	}

	private void setConstructing(boolean constructing) {
		if (constructing != this.constructing) {
			this.constructing = constructing;

			if (constructing) {
				removeMouseListener(playHandler);
				removeMouseMotionListener(playHandler);

				addMouseListener(constructionHandler);
				addMouseMotionListener(constructionHandler);
			} else {
				removeMouseListener(constructionHandler);
				removeMouseMotionListener(constructionHandler);

				addMouseListener(playHandler);
				addMouseMotionListener(playHandler);
			}
		}
	}

	/**
	 * Get the console.
	 * 
	 * @return console console to be edited
	 */
	public Console getConsole() {
		return console;
	}

	private void initSkin() {
		session.lookup(ElementProblems.class).removeProblem(
				new Problem(Severity.ERROR, console, "skin", null));

		String skin = console.getSkin();
		if (skin == null) {
			this.skin = null;
		} else {
			this.skin = session.lookup(SkinManager.class).getSkin(console);
		}
	}

	protected View<? extends Displayable> getView(Displayable element) {
		return viewsByDisplayable.get(element);
	}

	private void createView(Displayable displayable) {
		View<? extends Displayable> view = ViewRegistry.createView(session,
				displayable);

		viewsByDisplayable.put(displayable, view);
		view.setContainer(this);

		repaint();
		revalidate();
	}

	protected void dropView(Displayable element) {
		View<? extends Displayable> view = getView(element);

		viewsByDisplayable.remove(element);
		view.setContainer(null);

		repaint();
		revalidate();
	}

	/**
	 * Show the popup menu for the currently pressed element. <br>
	 * The popup menu is not shown if no element is currently pressed.
	 * 
	 * @param x
	 *            x position to use
	 * @param y
	 *            y position to use
	 */
	protected void showMenu(int x, int y) {

		if (pressedView != null) {
			alignMenu.setEnabled(selectedViews.size() > 1);

			spreadMenu.setEnabled(selectedViews.size() > 2);

			sendMenu.removeAll();
			sendMenu.setEnabled(false);
			for (final Console other : session.getOrgan().getElements(
					Console.class)) {
				if (other != console) {
					JMenuItem consoleItem = new JMenuItem(Elements
							.getDisplayName(other));
					consoleItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							sendTo(other);
						}
					});
					sendMenu.add(consoleItem);
					sendMenu.setEnabled(true);
				}
			}

			menu.show(this, x, y);
		}
	}

	/**
	 * Get an element located at the given position, testing the selected
	 * elements first.
	 * 
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @return element
	 */
	protected View<? extends Displayable> getView(int x, int y) {
		// prefer already marked
		for (View<? extends Displayable> view : selectedViews) {
			if (view.contains(x, y)) {
				return view;
			}
		}

		// iterate over elements from front to back
		for (int r = console.getReferenceCount() - 1; r >= 0; r--) {
			Displayable element = (Displayable) console.getReference(r)
					.getElement();
			View<? extends Displayable> view = getView(element);
			if (view != null && view.contains(x, y)) {
				return view;
			}
		}
		return null;
	}

	/**
	 * The preferred size is determined by the contained views position and
	 * size.
	 * 
	 * @return the preferred size
	 */
	@Override
	public Dimension getPreferredSize() {

		int x = 0;
		int y = 0;

		for (View<? extends Displayable> view : viewsByDisplayable.values()) {
			x = Math.max(x, view.getX() + view.getWidth());
			y = Math.max(y, view.getY() + view.getHeight());
		}

		return new Dimension(x, y);
	}

	private void repaintView(View<? extends Displayable> view) {
		int x1 = view.getX();
		int y1 = view.getY();
		int x2 = view.getX() + view.getWidth();
		int y2 = view.getY() + view.getHeight();

		repaint(x1, y1, x2 - x1, y2 - y1);
	}

	public Component getHost() {
		return this;
	}

	public void toFront(Console console) {
		Component parent = getParent();
		while (parent != null) {
			if (parent instanceof ConsoleStack) {
				((ConsoleStack) parent).toFront(console);
				break;
			}
			parent = parent.getParent();
		}
	}

	/**
	 * Paint this component, i.e. the background, all views and a possibly
	 * visible selections.
	 * 
	 * @param graphics
	 *            graphics to paint on
	 */
	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D graphics2D = (Graphics2D) graphics;

		// Image createImage = createImage(10, 10);

		Rectangle clip = graphics.getClipBounds();
		graphics.setColor(getBackground());
		// graphics.setColor(new Color((int)(Math.random() * 255),
		// (int)(Math.random() * 255), (int)(Math.random() * 255)));
		graphics.fillRect(clip.x, clip.y, clip.width, clip.height);

		if (console != null) {
			paintViews(graphics2D);
		}

		if (constructing) {
			constructionHandler.paint(graphics2D);
		}
	}

	/**
	 * Paint the contained views.
	 * 
	 * @param g
	 *            graphics to paint on
	 */
	private void paintViews(Graphics2D g) {

		consoleView.paint(g);

		// iterate elements in order defined by console
		Rectangle clip = g.getClipBounds();
		for (Displayable element : console.getReferenced(Displayable.class)) {
			View<?> view = getView(element);
			if (view != null) {
				int x = view.getX();
				int y = view.getY();
				int width = view.getWidth();
				int height = view.getHeight();

				if ((clip.x + clip.width > x && clip.x < x + width)
						&& (clip.y + clip.height > y && clip.y < y + height)) {

					// clipping a scaled graphics corrupts the clip so don't do
					// it
					// g.clipRect(x, y, width, height);
					view.paint(g);
					// g.setClip(clip);
				}
			}
		}
	}

	/**
	 * The handler of events.
	 */
	private class EventHandler extends OrganAdapter implements SessionListener,
			SelectionListener {

		@Override
		public void propertyChanged(Element element, String name) {
			if (element == console) {
				initSkin();

				consoleView.update();
				for (View<? extends Displayable> view : viewsByDisplayable
						.values()) {
					view.update();
				}

				repaint();
				revalidate();

				if (constructing) {
					constructionHandler.updateViewMarkers();
				}
			} else if (element instanceof Displayable) {
				View<? extends Displayable> view = getView((Displayable) element);
				if (view != null) {
					repaintView(view);

					if (session.isConstructing()) {
						view.update();
					} else {
						view.update(name);
					}

					repaintView(view);
				}

				if (constructing) {
					constructionHandler.updateViewMarkers();
				}
			}
		}

		@Override
		public void indexedPropertyChanged(Element element, String name,
				Object value) {
			if (element == console && Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;

				View<? extends Displayable> view = getView((Displayable) reference
						.getElement());
				if (view != null) {
					view.update();

					constructionHandler.updateViewMarkers();
				}
			}
		}

		@Override
		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			if (element == console && Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;

				if (reference.getElement() instanceof Displayable) {
					createView((Displayable) reference.getElement());
				}
			}
		}

		@Override
		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			if (element == console && Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;

				if (reference.getElement() instanceof Displayable) {
					dropView((Displayable) reference.getElement());
				}
			}
		}

		public void constructingChanged(boolean constructing) {
			setConstructing(constructing);
		}

		public void modified() {
		}

		public void saved(File file) {
		}

		public void destroyed() {
		}

		public void selectionChanged() {

			selectedViews.clear();
			for (Element element : session.lookup(ElementSelection.class)
					.getSelectedElements()) {
				if (element instanceof Displayable) {
					View<? extends Displayable> view = getView((Displayable) element);
					if (view != null) {
						selectedViews.add(view);
					}
				}
			}

			if (constructing && selectedViews.size() == 1) {
				scrollDisplayableToVisible(selectedViews.get(0));
			}

			if (constructing) {
				constructionHandler.updateViewMarkers();
			}
		}
	}

	/**
	 * The mouse listener for construction.
	 */
	private class ConstructionHandler extends MouseInputAdapter {

		private Point pressedOrigin;

		private boolean pressedWasSelected;

		private Point mouseFrom;

		private Point mouseTo;

		private Marker dragMarker;

		private List<Marker> viewMarkers = new ArrayList<Marker>();

		private final BasicStroke stroke = new BasicStroke(0.0f,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
				new float[] { 4.0f, 4.0f }, 0.0f);

		private boolean isMultiSelect(MouseEvent ev) {
			return (ev.getModifiers() & Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask()) != 0;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseFrom = e.getPoint();

			session.lookup(UndoManager.class).compound();

			pressedView = getView(e.getX(), e.getY());
			if (pressedView != null) {
				pressedOrigin = new Point(pressedView.getX(), pressedView
						.getY());
				pressedWasSelected = selectedViews.contains(pressedView);
			}

			if (isMultiSelect(e)) {
				if (pressedView != null) {
					session.lookup(ElementSelection.class).addSelectedElement(
							pressedView.getElement());
				}
			} else {
				if (pressedView == null) {
					session.lookup(ElementSelection.class).setSelectedElement(
							null);
				} else {
					if (!selectedViews.contains(pressedView)) {
						session.lookup(ElementSelection.class)
								.setSelectedElement(pressedView.getElement());
					}
				}
			}

			showMenu(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			View<? extends Displayable> view = getView(e.getX(), e.getY());
			if (view == null) {
				setCursor(Cursor.getDefaultCursor());
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseTo = e.getPoint();

			if (pressedView == null) {
				if (getCursor().getType() == Cursor.DEFAULT_CURSOR) {
					setCursor(Cursor
							.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}

				if (dragMarker != null) {
					dragMarker.release();
					dragMarker = null;
				}

				dragMarker = createMarker(mouseFrom.x, mouseFrom.y, mouseTo.x,
						mouseTo.y);

				List<Displayable> elements = new ArrayList<Displayable>();
				for (View<? extends Displayable> view : viewsByDisplayable
						.values()) {
					if (dragMarker.contains(view.getX(), view.getY(), view
							.getWidth(), view.getHeight())) {
						elements.add(view.getElement());
					}
				}
				session.lookup(ElementSelection.class).setSelectedElements(
						elements);
			} else {
				int deltaX = grid(screenToDisposition(pressedOrigin.x
						+ mouseTo.x - mouseFrom.x))
						- screenToDisposition(pressedView.getX());
				int deltaY = grid(screenToDisposition(pressedOrigin.y
						+ mouseTo.y - mouseFrom.y))
						- screenToDisposition(pressedView.getY());

				for (View<? extends Displayable> markedView : selectedViews) {
					Displayable element = markedView.getElement();
					console.setLocation(element,
							console.getX(element) + deltaX, console
									.getY(element)
									+ deltaY);
				}
			}
		}

		private int grid(int pos) {
			return (pos + grid / 2) / grid * grid;
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			session.lookup(UndoManager.class).compound();

			if (dragMarker != null) {
				dragMarker.release();
				dragMarker = null;
			}

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			// new positions of views might have changed preferred size
			revalidate();

			showMenu(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (pressedView != null) {
				if (isMultiSelect(e)) {
					if (pressedWasSelected) {
						session
								.lookup(ElementSelection.class)
								.removeSelectedElement(pressedView.getElement());
					}
				} else {
					session.lookup(ElementSelection.class).setSelectedElement(
							pressedView.getElement());
				}
			}
		}

		protected void showMenu(MouseEvent e) {
			if (e.isPopupTrigger()) {
				ConsolePanel.this.showMenu(e.getX(), e.getY());
			}
		}

		public void paint(Graphics2D g) {
			if (dragMarker != null) {
				dragMarker.paint(g);
			}

			for (Marker marker : viewMarkers) {
				marker.paint(g);
			}
		}

		public void updateViewMarkers() {
			for (Marker marker : viewMarkers) {
				marker.release();
			}
			viewMarkers.clear();

			for (View<? extends Displayable> view : selectedViews) {
				viewMarkers.add(createMarker(view.getX(), view.getY(), view
						.getX()
						+ view.getWidth(), view.getY() + view.getHeight()));
			}
		}

		private Marker createMarker(int x1, int y1, int x2, int y2) {
			return Marker.create(ConsolePanel.this, false, null,
					getForeground(), stroke, x1, y1, x2, y2);

		}
	}

	/**
	 * The mouse listener for playing.
	 */
	private class PlayHandler extends MouseInputAdapter {

		private View<? extends Displayable> view;

		@Override
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();

			if (!e.isPopupTrigger()) {
				View<? extends Displayable> view = getView(x, y);
				if (view != null && view.isPressable(x, y)) {
					this.view = view;
					this.view.mousePressed(x, y);
				} else {
					this.view = null;
				}
			}
			setToolTipText(null);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			View<? extends Displayable> view = getView(e.getX(), e.getY());

			Cursor cursor = Cursor.getDefaultCursor();
			String tooltip = null;
			if (view != null) {
				tooltip = getTooltip(view.getElement());

				int x = e.getX();
				int y = e.getY();

				if (view.isPressable(x, y)) {
					cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				}
			}
			setCursor(cursor);
			setToolTipText(tooltip);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (view != null) {
				int x = screenToDisposition(e.getX());
				int y = screenToDisposition(e.getY());

				view.mouseDragged(x, y);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (view != null) {
				int x = screenToDisposition(e.getX());
				int y = screenToDisposition(e.getY());

				view.mouseReleased(x, y);
			}
			view = null;
		}
	}

	/**
	 * The listener to drop events.
	 */
	private class DisplayableDropTargetListener extends DropTargetAdapter {

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
			dragOver(dtde);
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			dtde.acceptDrag(DnDConstants.ACTION_LINK);
		}

		public void drop(DropTargetDropEvent dtde) {
			dtde.acceptDrop(DnDConstants.ACTION_LINK);

			try {
				Object[] elements = (Object[]) ObjectTransferable
						.getObject(dtde.getTransferable());

				ArrayList<View<? extends Displayable>> views = new ArrayList<View<? extends Displayable>>();
				for (int e = 0; e < elements.length; e++) {
					Element element = (Element) elements[e];

					if (console.canReference(element)) {
						console.reference(element);
					}

					if (element instanceof Displayable) {
						View<? extends Displayable> view = getView((Displayable) element);
						if (view != null) {
							views.add(view);
						}
					}
				}
				// new positions of (old) views might have changed preferred
				// size
				revalidate();

				int x = screenToDisposition(dtde.getLocation().x);
				int y = screenToDisposition(dtde.getLocation().y);

				new StackVerticalLayout(x, y, grid).layout(null, views);

				dtde.dropComplete(true);
			} catch (RuntimeException ex) {
				throw ex;
			} catch (Exception ex) {
				dtde.dropComplete(false);
			}
		}
	}

	private class ShortcutHandler implements KeyEventPostProcessor {

		public boolean postProcessKeyEvent(KeyEvent e) {
			if (constructing) {
				return false;
			}

			if (!Shortcut.maybeShortcut(e)) {
				return false;
			}

			if (KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.getFocusedWindow() == SwingUtilities
					.getWindowAncestor(ConsolePanel.this)) {

				boolean pressed = (e.getID() == KeyEvent.KEY_PRESSED);
				for (View<? extends Displayable> view : viewsByDisplayable
						.values()) {
					if (pressed) {
						view.keyPressed(e);
					} else {
						view.keyReleased(e);
					}
				}
			}

			return false;
		}
	}

	private class ArrangeToFrontAction extends BaseAction {
		private ArrangeToFrontAction() {
			config.get("arrangeToFront").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			for (View<? extends Displayable> view : selectedViews) {
				console.toFront(view.getElement());
			}
		}
	}

	private class ArrangeToBackAction extends BaseAction {
		private ArrangeToBackAction() {
			config.get("arrangeToBack").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			for (View<? extends Displayable> view : selectedViews) {
				console.toBack(view.getElement());
			}
		}
	}

	private class ArrangeHideAction extends BaseAction {
		private ArrangeHideAction() {
			config.get("arrangeHide").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			for (View<? extends Displayable> view : new ArrayList<View<? extends Displayable>>(
					selectedViews)) {
				console.unreference(view.getElement());
			}
		}
	}

	/**
	 * The action for layouts.
	 */
	private class LayoutAction extends BaseAction {

		private ViewLayout layout;

		private LayoutAction(ViewLayout layout) {
			this.layout = layout;

			putValue(Action.NAME, layout.getName());
			putValue(Action.SMALL_ICON, layout.getIcon());
		}

		public void actionPerformed(ActionEvent ev) {
			layout.layout(pressedView, selectedViews);
		}
	}

	/**
	 * Get the location of the given element.
	 * 
	 * @param element
	 *            element to get location for
	 * @return location
	 */
	public Point getLocation(View<? extends Displayable> view) {
		Displayable element = view.getElement();
		return new Point(dispositionToScreen(console.getX(element)),
				dispositionToScreen(console.getY(view.getElement())));
	}

	public void setLocation(View<? extends Displayable> view, Point location) {
		console.setLocation(view.getElement(), location.x, location.y);
	}

	private String getTooltip(Displayable element) {

		String description = element.getDescription();
		if ("".equals(description)) {
			return null;
		}
		int newLine = description.indexOf('\n');
		if (newLine != -1) {
			description = description.substring(0, newLine);
		}
		return description;
	}

	private void sendTo(final Console other) {
		session.lookup(UndoManager.class).compound(new Compound() {
			@Override
			public void run() {
				for (Element element : session.lookup(ElementSelection.class)
						.getSelectedElements()) {
					Reference<? extends Element> reference = console
							.getReference(element);

					console.removeReference(reference);

					other.addReference(reference);
				}
			}
		});
	}

	private class ConsoleView extends View<Console> {
		private ConsoleView(Console console) {
			super(console);
		}

		@Override
		protected void initLocation() {
			location = new Point(0, 0);
		}

		@Override
		protected Style createDefaultStyle() {
			return new Style();
		}

		@Override
		public void paint(Graphics2D g) {
			if (size.width > 0 && size.height > 0) {
				Rectangle clip = g.getClipBounds();

				for (int x = clip.x - clip.x % size.width; x < clip.x
						+ clip.width; x = x + size.width) {
					for (int y = clip.y - clip.y % size.height; y < clip.y
							+ clip.height; y = y + size.height) {
						g.translate(x, y);
						style.draw(g, size);
						g.translate(-x, -y);
					}
				}
			}
		}
	}

	public static interface ConsoleStack {
		public void toFront(Console console);
	}
}