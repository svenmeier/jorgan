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

import java.util.*;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.dnd.*;

import javax.swing.*;
import javax.swing.event.*;

import spin.Spin;
import swingx.dnd.ObjectTransferable;

import jorgan.disposition.*;
import jorgan.disposition.event.*;
import jorgan.gui.event.*;
import jorgan.gui.mac.TweakMac;
import jorgan.gui.console.*;
import jorgan.gui.construct.layout.*;
import jorgan.skin.*;
import jorgan.config.*;

/**
 * Panel that manages views to display a console of an organ.
 */
public class ConsolePanel extends JComponent implements Scrollable {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  /**
   * The organ of the edited console.
   */
  private OrganSession session;

  /**
   * The edited console.
   */
  private Console console;

  /**
   * The element to view mapping.
   */
  private Map viewsByElement = new HashMap();
  
  /**
   * The views.
   */
  private ArrayList views = new ArrayList();

  /**
   * Currently constructing.
   */
  private boolean constructing = true;
  
  /**
   * The zoom to paint views at.
   */
  private double zoom = 1.0d;

  /**
   * The element that is currently pressed.
   */
  private jorgan.disposition.Element pressedElement;

  /**
   * The currently selected elements.
   */
  private List selectedElements = new ArrayList(); 
  
  /**
   * The comparator used to sort the views according to their position.
   */
  private ViewComparator viewComparator = new ViewComparator(true, true); 
  
  /**
   * The listener to selection changes.
   */
  private ElementSelectionListener selectionListener = new InternalElementSelectionListener();

  /**
   * The listener to organ changes.
   */
  private OrganListener organListener = new InternalOrganListener();

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
  private ConstructMouseInputListener constructMouseInputListener = new ConstructMouseInputListener();

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
  private JMenuItem hideMenuItem = new JMenuItem();
  
  /*
   * The layouts.
   */
  private Action alignLeftAction             = new LayoutAction(new AlignLeftLayout());
  private Action alignRightAction            = new LayoutAction(new AlignRightLayout());
  private Action alignCenterHorizontalAction = new LayoutAction(new AlignCenterHorizontalLayout());
  private Action alignTopAction              = new LayoutAction(new AlignTopLayout());
  private Action alignBottomAction           = new LayoutAction(new AlignBottomLayout());
  private Action alignCenterVerticalAction   = new LayoutAction(new AlignCenterVerticalLayout());
  private Action spreadHorizontalAction      = new LayoutAction(new SpreadHorizontalLayout());
  private Action spreadVerticalAction        = new LayoutAction(new SpreadVerticalLayout());
  
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

    menu.addSeparator();
    
    hideMenuItem.setText(resources.getString("view.hide"));
    hideMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (int s = 0; s < selectedElements.size(); s++) {
          jorgan.disposition.Element element = (jorgan.disposition.Element)selectedElements.get(s);
          
          console.unreference(element);
        }
      }
    });
    menu.add(hideMenuItem);

    setConstructing(false);
  }

  public void addNotify() {
    super.addNotify();
    
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(shortcutHandler);
  }

  public void removeNotify() {
    super.removeNotify();

    KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(shortcutHandler);
  }

  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  public boolean getScrollableTracksViewportHeight() {
    if (getParent() instanceof JViewport) {
        return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
    }
    return false;
  }

  public boolean getScrollableTracksViewportWidth() {
    if (getParent() instanceof JViewport) {
        return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
    }
    return false;
  }

  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    if (orientation == SwingConstants.VERTICAL) {
      return visibleRect.height;
    } else {
      return visibleRect.width;
    }
  }

  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

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
    return Math.max(1, viewToScreen(viewIncrement));
  }
  
  public void setZoom(double zoom) {
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
      this.session.getOrgan().removeOrganListener((OrganListener)Spin.over(organListener));
      this.session.getSelectionModel().removeSelectionListener(selectionListener);

      jorgan.gui.console.Configuration.instance().removeConfigurationListener(configListener);
      jorgan.gui.construct.Configuration.instance().removeConfigurationListener(configListener);
    }

    this.session = session;
    
    if (this.session != null) {
      this.session.getOrgan().addOrganListener((OrganListener)Spin.over(organListener));
      this.session.getSelectionModel().addSelectionListener(selectionListener);

      jorgan.gui.console.Configuration.instance().addConfigurationListener(configListener);
      jorgan.gui.construct.Configuration.instance().addConfigurationListener(configListener);
    }
  }
  
  public void scrollElementToVisible(jorgan.disposition.Element element) {
    if (element != null) {
      View view = getView(element);
      if (view != null) {
        scrollRectToVisible(new Rectangle(viewToScreen(view.getX())    , viewToScreen(view.getY()),
                                          viewToScreen(view.getWidth()), viewToScreen(view.getHeight())));
      }
    }
  }

  protected int viewToScreen(int viewPos) {
    return (int)(viewPos * zoom);  
  }
  
  protected int screenToView(int screenPos) {
    return (int)(screenPos / zoom);  
  }
  
  /**
   * Construct an organ.
   *
   * @param constructing  should organ be constructed
   */
  public void setConstructing(boolean constructing) {
    if (constructing != this.constructing) {
      this.constructing = constructing;
    
      if (constructing) {
        removeMouseListener      (playMouseInputListener);
        removeMouseMotionListener(playMouseInputListener);

        addMouseListener      (constructMouseInputListener);
        addMouseMotionListener(constructMouseInputListener);
      } else {
        removeMouseListener      (constructMouseInputListener);
        removeMouseMotionListener(constructMouseInputListener);

        addMouseListener      (playMouseInputListener);
        addMouseMotionListener(playMouseInputListener);
      }
    }
  }

  /**
   * Test if organ is currently constructed.
   *
   * @return  <code>true</code> if organ is currently constructed
   */
  public boolean isConstructing() {
    return constructing;
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
   * @param console console to be edited
   */
  public void setConsole(Console console) {
    if (this.console != null) {       
      setZoom(1.0d);

      for (int v = views.size() - 1; v >= 0; v--) {
        View view = (View)views.get(v);
        
        dropView(view.getElement());
      }
    }

    this.console = console;

    if (console != null) {
      setZoom(console.getZoom());
      
      for (int r = 0; r < console.getReferencesCount(); r++) {
        createView(console.getReference(r).getElement());
      }
      viewComparator.sort(views);
    }
  }

  protected View getView(jorgan.disposition.Element element) {
    return (View)viewsByElement.get(element);
  }
    
  private void createView(jorgan.disposition.Element element) {
    View view = null;

    if (element instanceof Registratable) {
      view = new RegistratableView((Registratable)element);   
    } else if (element instanceof Swell) {
      view = new SwellView((Swell)element);   
    } else if (element instanceof Piston) {
      view = new PistonView((Piston)element);   
    } else {
      view = new OtherView(element);
    }
      
    viewsByElement.put(element, view);
    views.add(view);
    view.setConsolePanel(this);
    
    repaint();
    revalidate();
  }
   
  protected void dropView(jorgan.disposition.Element element) {
    View view = getView(element);

    viewsByElement.remove(element);
    views.remove(view);
    view.setConsolePanel(null);

    repaint();
    revalidate();
  }
  
  /**
   * Show the popup menu for the currently pressed element.
   * <br>
   * The popup menu is not shown if no element is currently pressed.
   *  
   * @param x   x position to use
   * @param y   y position to use
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
   * @param x   x position
   * @param y   y position
   * @return    
   */
  protected jorgan.disposition.Element getElement(int x, int y) {
    for (int e = 0; e < selectedElements.size(); e++) {
      jorgan.disposition.Element element = (jorgan.disposition.Element)selectedElements.get(e);
      
      View view = getView(element);
      if (view != null && view.contains(x, y)) {
        return element;
      }
    }

    for (int v = views.size() - 1; v >= 0; v--) {
      View view = (View)views.get(v);
      if (view.contains(x, y)) {
        return view.getElement();
      }
    }   
    return null;
  }

  /**
   * Get an view located at the given position.
   * 
   * @param x   x position
   * @param y   y position
   * @return    
   */
  protected View getView(int x, int y) {
    jorgan.disposition.Element element = getElement(x, y);
    if (element == null) {
      return null;
    }
    
    return getView(element); 
  }
  
  /**
   * The preferred size is determined by the contained views
   * positiosn and size.
   * 
   * @return the preferred size
   */
  public Dimension getPreferredSize() {
    
    int width  = 0;
    int height = 0;

    for (int v = 0; v < views.size(); v++) {
      View view = (View)views.get(v);

      width  = Math.max(width , view.getX() + view.getWidth());
      height = Math.max(height, view.getY() + view.getHeight());
    }   

    return new Dimension(viewToScreen(width), viewToScreen(height));
  }
  
  /**
   * Request repainting of a view.
   * 
   * @param view view to repaint
   */
  public void repaintView(View view) {
    repaint(viewToScreen(view.getX()),     viewToScreen(view.getY()),
            viewToScreen(view.getWidth()), viewToScreen(view.getHeight()));
  }

  /**
   * Paint this component, i.e. the background, all views
   * and a possibly visible selections.
   * 
   * @param graphics  graphics to paint on 
   */
  public void paintComponent(Graphics graphics) {
    Graphics2D graphics2D = (Graphics2D)graphics;
    AffineTransform original = graphics2D.getTransform();
    graphics2D.scale(zoom, zoom);
    if (jorgan.gui.console.Configuration.instance().getInterpolate()) {
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                  RenderingHints.VALUE_INTERPOLATION_BICUBIC);   
    }
    paintBackground(graphics2D);
    paintViews     (graphics2D);
    graphics2D.setTransform(original);

    if (constructing) {    
      constructMouseInputListener.paint(graphics);
    }
  }

  /**
   * Paint the contained views.
   * 
   * @param g   graphics to paint on
   */
  private void paintViews(Graphics2D g) {
    Rectangle       clip      = g.getClipBounds();
    AffineTransform transform = g.getTransform();
    for (int v = 0; v < views.size(); v++) {
      View view = (View)views.get(v);
       
      int x = view.getX();
      int y = view.getY();
      int width  = view.getWidth();
      int height = view.getHeight();
         
      if ((clip.x + clip.width  > x && clip.x < x + width ) &&
          (clip.y + clip.height > y && clip.y < y + height)) {

        g.clipRect(x, y, width, height);
        g.translate(x, y);
        view.paint(g);
        g.setTransform(transform);
        g.setClip(clip);
      }
    }
  }

  /**
   * Paint the background.
   * 
   * @param g   graphics to paint on
   */
  private void paintBackground(Graphics2D g) {
    Rectangle clip = g.getClipBounds();    

    java.awt.Image image = null;
    if (console != null) {
      Skin skin = SkinManager.instance().getSkin(console.getSkin());
      if (skin != null) {
        Style style = skin.getStyle(console.getStyle());
        if (style != null) {
          if (style.getStateCount() > 0) {
            State state = style.getState(0);
            if (state.getImage() != null) {
              image = SkinManager.instance().getImage(style.getSkin(), state.getImage());
              
              int width  = image.getWidth(this);
              int height = image.getHeight(this);
      
              for (int y =   clip.y                / (height);
                       y <= (clip.y + clip.height) / (height);
                       y++) {
                for (int x =   clip.x               / (width);
                         x <= (clip.x + clip.width) / (width);
                         x++) {
                  g.drawImage(image, x * width, y * height, this);
                }
              }
              return;
            }
          }
        }
      }
    }
    
    g.setColor(getBackground());
    g.fillRect(clip.x, clip.y, clip.width, clip.height);
  }

  /**
   * The listener to organ events.
   * <br/>
   * Note that <em>Spin</em> ensures that the methods of this listeners are called
   * on the EDT, although a change in the organ might be triggered by a change
   * on a MIDI thread.
   */
  private class InternalOrganListener extends OrganAdapter {

    public void elementChanged(final OrganEvent event) {
      jorgan.disposition.Element element = event.getElement();
      
      if (element == console) {
        setZoom(console.getZoom());
        
        for (int v = 0; v < views.size(); v++) {
          ((View)views.get(v)).changeUpdate(null);
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

      jorgan.disposition.Element element = event.getElement();
      
      if (element.getReferrer(Console.class).contains(console)) {
        createView(element);

        viewComparator.sort(views);
      }
    }

    public void elementRemoved(OrganEvent event) {

      jorgan.disposition.Element element = event.getElement();

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

        viewComparator.sort(views);
      }
    }

    public void referenceRemoved(OrganEvent event) {
      if (event.getElement() == console) {
        dropView(event.getReference().getElement());
      }
    }
  }
  
  /**
   * The listener to configuration events.
   */
  private class InternalConfigurationListener implements ConfigurationListener {
 
    public void configurationChanged(ConfigurationEvent ev) {
      setConsole(console);
    }
    
    public void configurationBackup(ConfigurationEvent event) { }
  }

  /**
   * The mouse listener for construction.
   */
  private class ConstructMouseInputListener extends MouseInputAdapter {

    private Point mouseFrom;
    private Point mouseTo;
    private Point mouseDrag;
    
    private boolean wasSelected; 
    
    private boolean isMultiSelect(MouseEvent ev) {
      return (ev.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
    }
    
    public void mousePressed(MouseEvent e) {

      mouseFrom = e.getPoint();

      pressedElement = getElement(screenToView(e.getX()), screenToView(e.getY()));
      
      if (pressedElement != null) {
        wasSelected = selectedElements.contains(pressedElement);
      }

      if (isMultiSelect(e)) {
        if (pressedElement != null) {
          session.getSelectionModel().addSelectedElement(pressedElement);
        }
      } else {
        if (pressedElement == null) {
          session.getSelectionModel().setSelectedElement(null);
        } else {
          if (!selectedElements.contains(pressedElement)) {
            session.getSelectionModel().setSelectedElement(pressedElement);
          }
        }
      }
              
      showPopup(e);      
    }

    public void mouseMoved(MouseEvent e) {
      jorgan.disposition.Element element = getElement(screenToView(e.getX()), screenToView(e.getY()));
      if (element == null) {
        setCursor(Cursor.getDefaultCursor());
      } else {
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      }
    }

    public void mouseDragged(MouseEvent e) {
           
      if (pressedElement == null) {
        if (mouseTo == null) {
          setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
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
        for (int v = 0; v < views.size(); v++) {
          View view = (View)views.get(v);
            
          if (view.getX() > x1 && (view.getX() + view.getWidth() ) < x2 &&
              view.getY() > y1 && (view.getY() + view.getHeight()) < y2) {
            elements.add(view.getElement());
          }
        }
        session.getSelectionModel().setSelectedElements(elements);
      } else {
        mouseTo = e.getPoint();

        if (mouseDrag == null) {
            mouseDrag = mouseFrom;
        } else {
            int grid = jorgan.gui.construct.Configuration.instance().getGrid();

            View view = getView(pressedElement);
        
            int x = view.getX() + screenToView(mouseTo.x - mouseDrag.x);
            int y = view.getY() + screenToView(mouseTo.y - mouseDrag.y);
         
            int gridX = (x + grid/2) / grid * grid;
            int gridY = (y + grid/2) / grid * grid;

            int deltaX = gridX - view.getX();
            int deltaY = gridY - view.getY();
        
            for (int s = 0; s < selectedElements.size(); s++) {
              jorgan.disposition.Element selectedElement = (jorgan.disposition.Element)selectedElements.get(s);
           
              view = getView(selectedElement);

              view.setPosition(view.getX() + deltaX, view.getY() + deltaY);
            }

            mouseDrag = new Point(mouseTo.x + viewToScreen(gridX - x), mouseTo.y + viewToScreen(gridY - y));        
        }
      }
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
      mouseTo   = null;
      mouseDrag = null;
      
      showPopup(e);      
    }

    public void mouseClicked(MouseEvent e) {
      if (pressedElement != null) {
        if (isMultiSelect(e) && wasSelected) {
          session.getSelectionModel().removeSelectedElement(pressedElement);
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
      g.setColor(jorgan.gui.construct.Configuration.instance().getColor());
      g.setXORMode(Color.white);

      for (int s = 0; s < selectedElements.size(); s++) {
        jorgan.disposition.Element selectedElement = (jorgan.disposition.Element)selectedElements.get(s);
    
        View view = getView(selectedElement);
        if (view != null) {
          int x      = viewToScreen(view.getX());
          int y      = viewToScreen(view.getY());
          int width  = viewToScreen(view.getWidth());
          int height = viewToScreen(view.getHeight());
          g.drawRect(x, y, width - 1, height - 1);
        }
      }

      g.setPaintMode();
    }

    private void paintSelector(Graphics g) {
      g.setColor(jorgan.gui.construct.Configuration.instance().getColor());
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
      if (view != null && view.isPressable(x - view.getX(), y - view.getY(), e)) {
        this.view = view;
        this.view.pressed(x - view.getX(), y - view.getY(), e);
      } else {
        this.view = null;
      }
      setToolTipText(null);      
    }

    public void mouseMoved(MouseEvent e) {
      View view = getView(screenToView(e.getX()), screenToView(e.getY()));
      
      Cursor cursor  = Cursor.getDefaultCursor();
      String tooltip = null;
      if (view != null) {
        String description = view.getElement().getDescription(); 
        if (!"".equals(description)) {
          tooltip = description; 
        }
        int x = screenToView(e.getX());
        int y = screenToView(e.getY());

        if (view.isPressable(x - view.getX(), y - view.getY(), e)) { 
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

        view.dragged(x - view.getX(), y - view.getY(), e);
      }
    }

    public void mouseReleased(MouseEvent e) {
      if (view != null) {
        int x = screenToView(e.getX());
        int y = screenToView(e.getY());

        view.released(x - view.getX(), y - view.getY(), e);
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
        Object[] elements = (Object[])ObjectTransferable.getObject(dtde.getTransferable()); 
      
        ArrayList views = new ArrayList();
        for (int e = 0; e < elements.length; e++) {
            jorgan.disposition.Element element = (jorgan.disposition.Element)elements[e];
            
            if (console.canReference(element)) {
                console.reference(element);          
            }

            View view = getView(element);
            if (view != null) { 
              views.add(view);
            }
        }
        // new positions of (old) views might have changed preferred size
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
  private class InternalElementSelectionListener implements ElementSelectionListener {
    public void selectionChanged(ElementSelectionEvent ev) {

      List newElements = session.getSelectionModel().getSelectedElements();
      
      for (int e = 0; e < newElements.size(); e++) {
          jorgan.disposition.Element element = (jorgan.disposition.Element)newElements.get(e);
          
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
        scrollElementToVisible((jorgan.disposition.Element)selectedElements.get(0));
      }
    }
  }

  private class ShortcutHandler implements KeyEventPostProcessor {

    public boolean postProcessKeyEvent(KeyEvent e) {
      if (e.getID() == KeyEvent.KEY_PRESSED && !constructing) {
        if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() == SwingUtilities.getWindowAncestor(ConsolePanel.this)) {
          for (int v = 0; v < views.size(); v++) {
            View view = (View)views.get(v);
        
            view.pressed(e);
          }
        }
      }
      
      return false;
    }
  }
  
  /**
   * The action for layouts.
   */
  private class LayoutAction extends AbstractAction {

    private ViewLayout layout;
    
    public LayoutAction(ViewLayout layout) {
      this.layout = layout;
      
      putValue(Action.NAME      , layout.getName());
      putValue(Action.SMALL_ICON, layout.getIcon());      
    }
    
    public void actionPerformed(ActionEvent ev) {
      ArrayList views = new ArrayList();
      for (int s = 0; s < selectedElements.size(); s++) {
        View view = getView((jorgan.disposition.Element)selectedElements.get(s));
        if (view != null) { 
          views.add(view);
        }
      }

      View pressed = getView(pressedElement);
      layout.layout(pressed, views);      
    }
  }
}