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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputAdapter;

import jorgan.config.ConfigurationEvent;
import jorgan.config.ConfigurationListener;
import jorgan.disposition.Activateable;
import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.Element;
import jorgan.disposition.Initiator;
import jorgan.disposition.Memory;
import jorgan.disposition.Shortcut;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.console.ActivateableView;
import jorgan.gui.console.ContinuousView;
import jorgan.gui.console.InitiatorView;
import jorgan.gui.console.MemoryView;
import jorgan.gui.console.View;
import jorgan.gui.construct.Configuration;
import jorgan.gui.construct.layout.AlignBottomLayout;
import jorgan.gui.construct.layout.AlignCenterHorizontalLayout;
import jorgan.gui.construct.layout.AlignCenterVerticalLayout;
import jorgan.gui.construct.layout.AlignLeftLayout;
import jorgan.gui.construct.layout.AlignRightLayout;
import jorgan.gui.construct.layout.AlignTopLayout;
import jorgan.gui.construct.layout.SpreadHorizontalLayout;
import jorgan.gui.construct.layout.SpreadVerticalLayout;
import jorgan.gui.construct.layout.StackVerticalLayout;
import jorgan.gui.construct.layout.ViewLayout;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.gui.mac.TweakMac;
import jorgan.play.event.PlayEvent;
import jorgan.play.event.PlayListener;
import jorgan.skin.Skin;
import jorgan.skin.SkinManager;
import jorgan.skin.Style;
import swingx.dnd.ObjectTransferable;

/**
 * Panel that manages views to display a console of an organ.
 */
public class ConsolePanel extends JComponent implements Scrollable {

    protected static final ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.gui.resources");

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
    private Map viewsByElement = new HashMap();

    /**
     * Currently constructing.
     */
    private boolean constructing = false;

    /**
     * The zoom to paint views at.
     */
    private float zoom = 1.0f;

    /**
     * The element that is currently pressed.
     */
    private Element pressedElement;

    /**
     * The currently selected elements.
     */
    private List selectedElements = new ArrayList();

    /**
     * The listener to selection changes.
     */
    private ElementSelectionListener selectionListener = new InternalElementSelectionListener();

    /**
     * The listener to organ changes.
     */
    private OrganListener organListener = new InternalOrganListener();

    /**
     * The listener to play changes.
     */
    private PlayListener playListener = new InternalPlayListener();

    /**
     * The listener to configuration changes.
     */
    private ConfigurationListener configListener = new InternalConfigurationListener();

    /**
     * The listener to mouse (motion) events in play modus.
     */
    private PlayMouseInputListener playMouseInputListener = new PlayMouseInputListener();

    /**
     * The listener to mouse (motion) events in construction modus.
     */
    private ConstructionHandler constructMouseInputListener = new ConstructionHandler();

    /**
     * The listener to drop events.
     */
    private DropTargetListener dropTargetListener = new ElementDropTargetListener();

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

    /**
     * The arrangements.
     */
    private Action arrangeToFrontAction = new ArrangeToFrontAction();

    private Action arrangeToBackAction = new ArrangeToBackAction();

    private Action arrangeHideAction = new ArrangeHideAction();

    /*
     * The layouts.
     */
    private Action alignLeftAction = new LayoutAction(new AlignLeftLayout());

    private Action alignRightAction = new LayoutAction(new AlignRightLayout());

    private Action alignCenterHorizontalAction = new LayoutAction(
            new AlignCenterHorizontalLayout());

    private Action alignTopAction = new LayoutAction(new AlignTopLayout());

    private Action alignBottomAction = new LayoutAction(new AlignBottomLayout());

    private Action alignCenterVerticalAction = new LayoutAction(
            new AlignCenterVerticalLayout());

    private Action spreadHorizontalAction = new LayoutAction(
            new SpreadHorizontalLayout());

    private Action spreadVerticalAction = new LayoutAction(
            new SpreadVerticalLayout());

    /**
     * Create a view panel.
     */
    public ConsolePanel() {

        // must report to be opaque so containing scrollPane can use blitting
        setOpaque(true);

        // must be focusable to be used in fullScreen
        setFocusable(true);

        setFont(new Font("Arial", Font.PLAIN, 12));
        setBackground(Color.white);
        setForeground(Color.black);

        ToolTipManager.sharedInstance().registerComponent(this);
        new DropTarget(this, dropTargetListener);

        alignMenu.setText(resources.getString("view.align"));
        menu.add(alignMenu);

        alignMenu.add(alignLeftAction);
        alignMenu.add(alignCenterHorizontalAction);
        alignMenu.add(alignRightAction);
        alignMenu.add(alignTopAction);
        alignMenu.add(alignCenterVerticalAction);
        alignMenu.add(alignBottomAction);

        spreadMenu.setText(resources.getString("view.spread"));
        menu.add(spreadMenu);

        spreadMenu.add(spreadHorizontalAction);
        spreadMenu.add(spreadVerticalAction);

        arrangeMenu.setText(resources.getString("view.arrange"));
        menu.add(arrangeMenu);

        arrangeMenu.add(arrangeToFrontAction);
        arrangeMenu.add(arrangeToBackAction);
        arrangeMenu.add(arrangeHideAction);

        setConstructing(true);
    }

    public void addNotify() {
        super.addNotify();

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventPostProcessor(shortcutHandler);
    }

    public void removeNotify() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .removeKeyEventPostProcessor(shortcutHandler);

        super.removeNotify();
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

        int viewPos;
        if (orientation == SwingConstants.VERTICAL) {
            viewPos = screenToView(visibleRect.y);
        } else {
            viewPos = screenToView(visibleRect.x);
        }

        int grid = jorgan.gui.construct.Configuration.instance().getGrid();

        int newViewPos;
        if (direction > 0) {
            newViewPos = viewPos + grid;
        } else {
            newViewPos = viewPos - 1;
        }

        // snap new view position to grid
        newViewPos = newViewPos - (newViewPos % grid);

        int viewIncrement = Math.abs(viewPos - newViewPos);

        // Ensure an increment of at least 1, since translation to screen
        // can make view increment irrelevant.
        return Math.max(1, viewToScreen(viewIncrement, false));
    }

    public void setZoom(float zoom) {
        if (this.zoom != zoom) {
            this.zoom = zoom;

            repaint();
            revalidate();
        }
    }

    public double getZoom() {
        return zoom;
    }

    public void setOrgan(OrganSession session) {
        if (this.session != null) {
            this.session.removeOrganListener(organListener);
            this.session.removeSelectionListener(selectionListener);
            this.session.removePlayerListener(playListener);

            jorgan.gui.console.Configuration.instance()
                    .removeConfigurationListener(configListener);
            jorgan.gui.construct.Configuration.instance()
                    .removeConfigurationListener(configListener);
        }

        this.session = session;

        if (this.session != null) {
            this.session.addOrganListener(organListener);
            this.session.addSelectionListener(selectionListener);
            this.session.addPlayerListener(playListener);

            jorgan.gui.console.Configuration.instance()
                    .addConfigurationListener(configListener);
            jorgan.gui.construct.Configuration.instance()
                    .addConfigurationListener(configListener);
            
            setConstructing(!this.session.getPlay().isOpen());
        }
    }

    public void scrollElementToVisible(Element element) {
        if (element != null) {
            View view = getView(element);
            if (view != null) {
                int x1 = viewToScreen(view.getX(), false);
                int y1 = viewToScreen(view.getY(), false);
                int x2 = viewToScreen(view.getX() + view.getWidth(), true);
                int y2 = viewToScreen(view.getY() + view.getHeight(), true);

                scrollRectToVisible(new Rectangle(x1, y1, x2 - x1, y2 - y1));
            }
        }
    }

    protected int viewToScreen(int viewPos, boolean roundUp) {
        if (roundUp) {
            return Math.round(viewPos * zoom);
        } else {
            return (int) (viewPos * zoom);
        }
    }

    protected int screenToView(int screenPos) {
        return (int) (screenPos / zoom);
    }

    public Skin getSkin() {
        return skin;
    }

    private void setConstructing(boolean constructing) {
        if (constructing != this.constructing) {
            this.constructing = constructing;

            if (constructing) {
                removeMouseListener(playMouseInputListener);
                removeMouseMotionListener(playMouseInputListener);

                addMouseListener(constructMouseInputListener);
                addMouseMotionListener(constructMouseInputListener);
            } else {
                removeMouseListener(constructMouseInputListener);
                removeMouseMotionListener(constructMouseInputListener);

                addMouseListener(playMouseInputListener);
                addMouseMotionListener(playMouseInputListener);
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

    /**
     * Set the console to be edited.
     * 
     * @param console
     *            console to be edited
     */
    public void setConsole(Console console) {
        if (this.console != null) {
            consoleView = null;

            viewsByElement.clear();

            skin = null;

            setZoom(1.0f);
        }

        this.console = console;

        if (console != null) {
            setZoom(console.getZoom());

            initSkin();

            consoleView = new ConsoleView(console);
            consoleView.setConsolePanel(this);

            for (int r = 0; r < console.getReferenceCount(); r++) {
                createView(console.getReference(r).getElement());
            }
        }
    }

    private void initSkin() {
        if (console.getSkin() == null) {
            skin = null;
        } else {
            skin = SkinManager.instance().getSkin(console.getSkin());
        }
    }

    protected View getView(Element element) {
        return (View) viewsByElement.get(element);
    }

    private void createView(Element element) {
        View view = null;

        if (element instanceof Activateable) {
            view = new ActivateableView((Activateable) element);
        } else if (element instanceof Initiator) {
            view = new InitiatorView((Initiator) element);
        } else if (element instanceof Memory) {
            view = new MemoryView((Memory) element);
        } else if (element instanceof Continuous) {
            view = new ContinuousView((Continuous) element);
        } else {
            view = new View(element);
        }

        viewsByElement.put(element, view);
        view.setConsolePanel(this);

        repaint();
        revalidate();
    }

    protected void dropView(Element element) {
        View view = getView(element);

        viewsByElement.remove(element);
        view.setConsolePanel(null);

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
    protected void showPopup(int x, int y) {

        if (pressedElement != null) {
            alignMenu.setEnabled(selectedElements.size() > 1);

            spreadMenu.setEnabled(selectedElements.size() > 2);

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
     * @return
     */
    protected Element getElement(int x, int y) {
        for (int e = 0; e < selectedElements.size(); e++) {
            Element element = (Element) selectedElements.get(e);

            View view = getView(element);
            if (view != null && view.contains(x, y)) {
                return element;
            }
        }

        // iterate over elements from front to back
        for (int r = console.getReferenceCount() - 1; r >= 0; r--) {
            Element element = console.getReference(r).getElement();
            View view = getView(element);
            if (view.contains(x, y)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Get an view located at the given position.
     * 
     * @param x
     *            x position
     * @param y
     *            y position
     * @return
     */
    protected View getView(int x, int y) {
        Element element = getElement(x, y);
        if (element == null) {
            return null;
        }

        return getView(element);
    }

    /**
     * The preferred size is determined by the contained views position and
     * size.
     * 
     * @return the preferred size
     */
    public Dimension getPreferredSize() {

        int x = 0;
        int y = 0;

        for (int r = 0; r < console.getReferenceCount(); r++) {
            Element element = console.getReference(r).getElement();
            View view = getView(element);

            x = Math.max(x, view.getX() + view.getWidth());
            y = Math.max(y, view.getY() + view.getHeight());
        }

        return new Dimension(viewToScreen(x, true), viewToScreen(y, true));
    }

    /**
     * Request repainting of a view.
     * 
     * @param view
     *            view to repaint
     */
    public void repaintView(View view) {
        int x1 = viewToScreen(view.getX(), false);
        int y1 = viewToScreen(view.getY(), false);
        int x2 = viewToScreen(view.getX() + view.getWidth(), true);
        int y2 = viewToScreen(view.getY() + view.getHeight(), true);

        repaint(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Paint this component, i.e. the background, all views and a possibly
     * visible selections.
     * 
     * @param graphics
     *            graphics to paint on
     */
    public void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;

        Rectangle clip = graphics.getClipBounds();
        graphics.setColor(getBackground());
        graphics.fillRect(clip.x, clip.y, clip.width, clip.height);

        AffineTransform original = graphics2D.getTransform();
        graphics2D.scale(zoom, zoom);
        if (jorgan.gui.console.Configuration.instance().getInterpolate()) {
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        paintViews(graphics2D);
        graphics2D.setTransform(original);

        if (constructing) {
            constructMouseInputListener.paint(graphics);
        }
    }

    /**
     * Paint the contained views.
     * 
     * @param g
     *            graphics to paint on
     */
    private void paintViews(Graphics2D g) {

        Rectangle clip = g.getClipBounds();

        consoleView.paint(g);

        for (int r = 0; r < console.getReferenceCount(); r++) {
            Element element = console.getReference(r).getElement();
            View view = getView(element);

            int x = view.getX();
            int y = view.getY();
            int width = view.getWidth();
            int height = view.getHeight();

            if ((clip.x + clip.width > x && clip.x < x + width)
                    && (clip.y + clip.height > y && clip.y < y + height)) {

                // clipping a scaled graphics corrupts the clip so don't do it
                // g.clipRect(x, y, width, height);
                view.paint(g);
                // g.setClip(clip);
            }
        }
    }

    /**
     * The listener to organ events.
     */
    private class InternalOrganListener extends OrganAdapter {

        public void elementChanged(final OrganEvent event) {
            Element element = event.getElement();

            if (element == console) {
                setZoom(console.getZoom());

                initSkin();

                consoleView.changeUpdate(event);
                for (int r = 0; r < console.getReferenceCount(); r++) {
                    View view = getView(console.getReference(r).getElement());
                    view.changeUpdate(event);
                }

                repaint();
            } else {
                View view = getView(element);
                if (view != null) {
                    view.changeUpdate(event);
                }
            }
        }

        public void elementAdded(OrganEvent event) {

            Element element = event.getElement();

            if (element.getReferrer(Console.class).contains(console)) {
                createView(element);
            }
        }

        public void elementRemoved(OrganEvent event) {

            Element element = event.getElement();

            if (getView(element) != null) {
                dropView(element);
            }
        }

        public void referenceChanged(OrganEvent event) {

            if (event.getElement() == console) {
                getView(event.getReference().getElement()).changeUpdate(event);
            }
        }

        public void referenceAdded(OrganEvent event) {
            if (event.getElement() == console) {
                createView(event.getReference().getElement());
            }
        }

        public void referenceRemoved(OrganEvent event) {
            if (event.getElement() == console) {
                dropView(event.getReference().getElement());
            }
        }
    }

    private class InternalPlayListener implements PlayListener {

        public void opened() {
            setConstructing(false);
        }
        
        public void closed() {
            setConstructing(true);
        }

        public void inputAccepted() {
        }

        public void outputProduced() {
        }

        public void playerAdded(PlayEvent ev) {
        }

        public void playerRemoved(PlayEvent ev) {
        }

        public void problemAdded(PlayEvent ev) {
        }

        public void problemRemoved(PlayEvent ev) {
        }        
    }
    
    /**
     * The listener to configuration events.
     */
    private class InternalConfigurationListener implements
            ConfigurationListener {

        public void configurationChanged(ConfigurationEvent ev) {
            setConsole(console);
        }

        public void configurationBackup(ConfigurationEvent event) {
        }
    }

    /**
     * The mouse listener for construction.
     */
    private class ConstructionHandler extends MouseInputAdapter {

        private Point mouseFrom;

        private Point mouseTo;

        private Point original;

        private boolean wasSelected;

        private boolean isMultiSelect(MouseEvent ev) {
            return (ev.getModifiers() & Toolkit.getDefaultToolkit()
                    .getMenuShortcutKeyMask()) != 0;
        }

        public void mousePressed(MouseEvent e) {

            mouseFrom = e.getPoint();

            pressedElement = getElement(screenToView(e.getX()), screenToView(e
                    .getY()));

            if (pressedElement != null) {
                wasSelected = selectedElements.contains(pressedElement);
            }

            if (isMultiSelect(e)) {
                if (pressedElement != null) {
                    session.getSelectionModel().addSelectedElement(
                            pressedElement);
                }
            } else {
                if (pressedElement == null) {
                    session.getSelectionModel().setSelectedElement(null);
                } else {
                    if (!selectedElements.contains(pressedElement)) {
                        session.getSelectionModel().setSelectedElement(
                                pressedElement);
                    }
                }
            }

            showPopup(e);
        }

        public void mouseMoved(MouseEvent e) {
            Element element = getElement(screenToView(e.getX()), screenToView(e
                    .getY()));
            if (element == null) {
                setCursor(Cursor.getDefaultCursor());
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        public void mouseDragged(MouseEvent e) {

            if (pressedElement == null) {
                if (mouseTo == null) {
                    setCursor(Cursor
                            .getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                } else {
                    updateSelector();
                }

                mouseTo = e.getPoint();

                updateSelector();

                int x1 = screenToView(Math.min(mouseFrom.x, mouseTo.x));
                int y1 = screenToView(Math.min(mouseFrom.y, mouseTo.y));
                int x2 = screenToView(Math.max(mouseFrom.x, mouseTo.x));
                int y2 = screenToView(Math.max(mouseFrom.y, mouseTo.y));

                List elements = new ArrayList();
                for (int r = 0; r < console.getReferenceCount(); r++) {
                    Element element = console.getReference(r).getElement();
                    View view = getView(element);

                    if (view.getX() > x1
                            && (view.getX() + view.getWidth()) < x2
                            && view.getY() > y1
                            && (view.getY() + view.getHeight()) < y2) {
                        elements.add(element);
                    }
                }
                session.getSelectionModel().setSelectedElements(elements);
            } else {
                View view = getView(pressedElement);

                int deltaX = 0;
                int deltaY = 0;
                if (mouseTo == null) {
                    original = new Point(view.getX(), view.getY());
                } else {
                    deltaX = original.x - view.getX();
                    deltaY = original.y - view.getY();
                }
                mouseTo = e.getPoint();

                deltaX += grid(original.x
                        + screenToView(mouseTo.x - mouseFrom.x))
                        - original.x;
                deltaY += grid(original.y
                        + screenToView(mouseTo.y - mouseFrom.y))
                        - original.y;

                for (int s = 0; s < selectedElements.size(); s++) {
                    Element selectedElement = (Element) selectedElements.get(s);

                    view = getView(selectedElement);

                    console.setLocation(selectedElement, view.getX() + deltaX,
                            view.getY() + deltaY);
                }
            }
        }

        private int grid(int pos) {
            int grid = Configuration.instance().getGrid();

            return (pos + grid / 2) / grid * grid;
        }

        public void mouseReleased(MouseEvent e) {
            if (pressedElement == null) {
                if (mouseTo != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    updateSelector();
                }
            } else {
                // new positions of views might have changed preferred size
                revalidate();
            }

            mouseFrom = null;
            mouseTo = null;

            showPopup(e);
        }

        public void mouseClicked(MouseEvent e) {
            if (pressedElement != null) {
                if (isMultiSelect(e) && wasSelected) {
                    session.getSelectionModel().removeSelectedElement(
                            pressedElement);
                }
            }
        }

        protected void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                ConsolePanel.this.showPopup(e.getX(), e.getY());
            }
        }

        public void updateSelector() {
            if (isShowing()) {
                Graphics g = getGraphics();
                if (g != null) {
                    paintSelector(g);
                    g.dispose();
                }
            }
        }

        public void updateSelection() {
            if (isShowing()) {
                Graphics g = getGraphics();
                if (g != null) {
                    paintSelection(g);
                    g.dispose();
                }
            }
        }

        public void paint(Graphics g) {
            paintSelection(g);

            paintSelector(g);
        }

        private void paintSelection(Graphics g) {
            g
                    .setColor(jorgan.gui.construct.Configuration.instance()
                            .getColor());
            g.setXORMode(Color.white);

            for (int s = 0; s < selectedElements.size(); s++) {
                Element selectedElement = (Element) selectedElements.get(s);

                View view = getView(selectedElement);
                if (view != null) {
                    int x1 = viewToScreen(view.getX(), false);
                    int y1 = viewToScreen(view.getY(), false);
                    int x2 = viewToScreen(view.getX() + view.getWidth(), true);
                    int y2 = viewToScreen(view.getY() + view.getHeight(), true);

                    g.drawRect(x1, y1, x2 - x1 - 1, y2 - y1 - 1);
                }
            }

            g.setPaintMode();
        }

        private void paintSelector(Graphics g) {
            g
                    .setColor(jorgan.gui.construct.Configuration.instance()
                            .getColor());
            g.setXORMode(Color.white);

            if (pressedElement == null && mouseFrom != null && mouseTo != null) {
                int x1 = Math.min(mouseFrom.x, mouseTo.x);
                int y1 = Math.min(mouseFrom.y, mouseTo.y);
                int x2 = Math.max(mouseFrom.x, mouseTo.x);
                int y2 = Math.max(mouseFrom.y, mouseTo.y);
                g.drawRect(x1, y1, x2 - x1, y2 - y1);
            }

            g.setPaintMode();
        }
    }

    /**
     * The mouse listener for playing.
     */
    private class PlayMouseInputListener extends MouseInputAdapter {

        View view;

        public void mousePressed(MouseEvent e) {
            int x = screenToView(e.getX());
            int y = screenToView(e.getY());

            View view = getView(x, y);
            if (view != null && view.isPressable(x, y)) {
                this.view = view;
                this.view.mousePressed(x, y);
            } else {
                this.view = null;
            }
            setToolTipText(null);
        }

        public void mouseMoved(MouseEvent e) {
            View view = getView(screenToView(e.getX()), screenToView(e.getY()));

            Cursor cursor = Cursor.getDefaultCursor();
            String tooltip = null;
            if (view != null) {
                String description = view.getElement().getDescription();
                if (!"".equals(description)) {
                    tooltip = description;
                }
                int x = screenToView(e.getX());
                int y = screenToView(e.getY());

                if (view.isPressable(x, y)) {
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                }
            }
            setCursor(cursor);
            setToolTipText(tooltip);
        }

        public void mouseDragged(MouseEvent e) {
            if (view != null) {
                int x = screenToView(e.getX());
                int y = screenToView(e.getY());

                view.mouseDragged(x, y);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (view != null) {
                int x = screenToView(e.getX());
                int y = screenToView(e.getY());

                view.mouseReleased(x, y);
            }
            view = null;
        }
    }

    /**
     * The listener to drop events.
     */
    private class ElementDropTargetListener extends DropTargetAdapter {

        public void dropActionChanged(DropTargetDragEvent dtde) {
            dragOver(dtde);
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (TweakMac.isMac()) {
                dtde.acceptDrag(DnDConstants.ACTION_LINK);
            } else {
                // On Windows LINK is not accepted by default but user
                // has to press CTRL by himself - so just accept MOVE.
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            }
        }

        public void drop(DropTargetDropEvent dtde) {
            dtde.acceptDrop(DnDConstants.ACTION_LINK);

            try {
                Object[] elements = (Object[]) ObjectTransferable
                        .getObject(dtde.getTransferable());

                ArrayList views = new ArrayList();
                for (int e = 0; e < elements.length; e++) {
                    Element element = (Element) elements[e];

                    if (console.canReference(element)) {
                        console.reference(element);
                    }

                    View view = getView(element);
                    if (view != null) {
                        views.add(view);
                    }
                }
                // new positions of (old) views might have changed preferred
                // size
                revalidate();

                int x = screenToView(dtde.getLocation().x);
                int y = screenToView(dtde.getLocation().y);

                new StackVerticalLayout(x, y).layout(null, views);

                dtde.dropComplete(true);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                dtde.dropComplete(false);
            }
        }
    }

    /**
     * The listener to element selections.
     */
    private class InternalElementSelectionListener implements
            ElementSelectionListener {
        public void selectionChanged(ElementSelectionEvent ev) {

            List newElements = session.getSelectionModel()
                    .getSelectedElements();

            for (int e = 0; e < newElements.size(); e++) {
                Element element = (Element) newElements.get(e);

                if (selectedElements.contains(element)) {
                    selectedElements.remove(element);
                } else {
                    selectedElements.add(element);
                }
            }

            constructMouseInputListener.updateSelection();

            selectedElements.clear();
            selectedElements.addAll(newElements);

            if (selectedElements.size() == 1) {
                scrollElementToVisible((Element) selectedElements.get(0));
            }
        }
    }

    private class ShortcutHandler implements KeyEventPostProcessor {

        public boolean postProcessKeyEvent(KeyEvent e) {
            if (!constructing && e.getID() == KeyEvent.KEY_PRESSED
                    && !Shortcut.isModifier(e.getKeyCode())) {
                if (KeyboardFocusManager.getCurrentKeyboardFocusManager()
                        .getFocusedWindow() == SwingUtilities
                        .getWindowAncestor(ConsolePanel.this)) {

                    for (int r = 0; r < console.getReferenceCount(); r++) {
                        View view = getView(console.getReference(r)
                                .getElement());

                        view.keyPressed(e);
                    }
                }
            }

            return false;
        }
    }

    private class ArrangeToFrontAction extends AbstractAction {
        public ArrangeToFrontAction() {
            putValue(Action.NAME, resources.getString("view.arrangeToFront"));
        }

        public void actionPerformed(ActionEvent e) {
            for (int s = 0; s < selectedElements.size(); s++) {
                Element element = (Element) selectedElements.get(s);

                console.toFront(element);
            }
        }
    }

    private class ArrangeToBackAction extends AbstractAction {
        public ArrangeToBackAction() {
            putValue(Action.NAME, resources.getString("view.arrangeToBack"));
        }

        public void actionPerformed(ActionEvent e) {
            for (int s = 0; s < selectedElements.size(); s++) {
                Element element = (Element) selectedElements.get(s);

                console.toBack(element);
            }
        }
    }

    private class ArrangeHideAction extends AbstractAction {
        public ArrangeHideAction() {
            putValue(Action.NAME, resources.getString("view.arrangeHide"));
        }

        public void actionPerformed(ActionEvent e) {
            for (int s = 0; s < selectedElements.size(); s++) {
                Element element = (Element) selectedElements.get(s);

                console.unreference(element);
            }
        }
    }

    /**
     * The action for layouts.
     */
    private class LayoutAction extends AbstractAction {

        private ViewLayout layout;

        public LayoutAction(ViewLayout layout) {
            this.layout = layout;

            putValue(Action.NAME, layout.getName());
            putValue(Action.SMALL_ICON, layout.getIcon());
        }

        public void actionPerformed(ActionEvent ev) {
            ArrayList views = new ArrayList();
            for (int s = 0; s < selectedElements.size(); s++) {
                View view = getView((Element) selectedElements.get(s));
                if (view != null) {
                    views.add(view);
                }
            }

            View pressed = getView(pressedElement);
            layout.layout(pressed, views);
        }
    }

    /**
     * Get the location of the given element.
     * 
     * @param element
     *            element to get location for
     * @return location
     */
    public Point getLocation(Element element) {
        return new Point(console.getX(element), console.getY(element));
    }

    private class ConsoleView extends View {
        public ConsoleView(Console console) {
            super(console);
        }

        protected void initLocation() {
            location = new Point(0, 0);
        }

        protected Style createDefaultStyle() {
            return new Style();
        }

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
}