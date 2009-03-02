package jorgan.graph.gui.dock;

import gj.layout.DefaultLayout;
import gj.layout.Layout2D;
import gj.layout.LayoutAlgorithmException;
import gj.layout.hierarchical.HierarchicalLayoutAlgorithm;
import gj.layout.hierarchical.VertexInLayerComparator;
import gj.model.Edge;
import gj.model.Graph;
import gj.model.Vertex;
import gj.ui.DefaultGraphRenderer;
import gj.ui.GraphWidget;
import gj.util.DefaultEdge;
import gj.util.DefaultVertex;
import gj.util.EmptyGraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Message;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.OrganDockable;
import jorgan.play.event.PlayListener;
import jorgan.session.ElementSelection;
import jorgan.session.OrganSession;
import jorgan.session.event.ElementSelectionEvent;
import jorgan.session.event.ElementSelectionListener;
import swingx.docking.Docked;
import bias.Configuration;

/**
 * Dockable for showing structure of a disposition
 */
public class StructureDockable extends OrganDockable {
  
  private static Configuration config = Configuration.getRoot().get(StructureDockable.class);
  private GraphWidget graphWidget;
  private ListenerImpl listener = new ListenerImpl(); 
  private OrganSession session = null;
  private List<Element> selection = new ArrayList<Element>();
  private Vector<ElementFilter> filters;
  private JComboBox filterCombo;
  
  /**
   * Constructor
   */
  public StructureDockable() {
    config.read(this);
    
    graphWidget = new GraphWidget();
    graphWidget.setRenderer(new DefaultGraphRenderer() {
      @Override
      @SuppressWarnings({ "unchecked" })
      protected Color getColor(Vertex vertex) {
        if (selection.isEmpty()) 
          return Color.BLACK;
        if (selection.contains(((DefaultVertex<Element>)vertex).getContent()))
          return Color.BLUE;
        return Color.LIGHT_GRAY;
      }
      @Override
      @SuppressWarnings({ "unchecked" })
      protected Color getColor(Edge edge) {
        if (selection.isEmpty()) 
          return Color.BLACK;
        if ( selection.contains(((DefaultVertex<Element>)edge.getStart()).getContent()) || selection.contains(((DefaultVertex<Element>)edge.getEnd()).getContent()) )
          return Color.BLUE;
        return Color.LIGHT_GRAY;
      }
    });
    
    graphWidget.addMouseListener(listener);
    
    // prepare filter
    filters= new Vector<ElementFilter>(20);
    filters.add(null);
    filters.add(new ElementFilter(Console.class, false));
    filters.add(new ElementFilter(Keyboard.class, false));
    filterCombo = new JComboBox(filters);
    filterCombo.setRenderer(new ListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        ElementFilter filter = (ElementFilter)value;
        JComponent result;
        if (filter==null)
          result = new JLabel("Filter");
        else 
          result = new JCheckBox(filter.type.getSimpleName(), filter.isSelected);
        result.setOpaque(true);
        if (isSelected) {
          result.setBackground(list.getSelectionBackground());
          result.setForeground(list.getSelectionForeground());
        } else {
          result.setBackground(list.getBackground());
          result.setForeground(list.getForeground());
        }
        return result;
      }
    });
    filterCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ElementFilter filter = (ElementFilter)filterCombo.getSelectedItem();
        if (filter!=null) {
          filter.isSelected = !filter.isSelected;
          rebuild();
        }
        filterCombo.setSelectedIndex(0);
      }
    });
    
    setContent(new JScrollPane(graphWidget));
  }
  
  /** callback - being docked */
  @Override
  public void docked(Docked docked) {
    super.docked(docked);

    docked.addTool(filterCombo);
  }

  /** calback - session opened */
  @Override
  public void setSession(OrganSession session) {

    if (this.session!=null) {
      this.session.removeOrganListener(listener);
      this.session.removeSelectionListener(listener);
      this.session.removePlayerListener(listener);
    }
    
    this.session = session;

    if (this.session!=null) {
      this.session.addOrganListener(listener);
      this.session.addSelectionListener(listener);
      this.session.addPlayerListener(listener);
    }
    
    rebuild();
  }
  

  /**
   * filter out elements?
   */
  private boolean filter(Element element) {
    for (ElementFilter filter : filters) {
      if (filter==null) continue;
      if (!filter.isSelected && filter.type.isAssignableFrom(element.getClass()))
        return true;
    }
    return false;
  }
  
  /**
   * create a non-empty name representation
   */
  private String name(Element element) {
    return element.getName().length()>0 ? element.getName() : element.getClass().getSimpleName();
  }
                                        
  
  /**
   * rebuild structure
   */
  private void rebuild() {
    
    // got a session?
    if (session==null) {
      graphWidget.setGraph(new EmptyGraph());
      return;
    }
    
    // build an element graph
    // TODO this is kinda bad to redo all over every time :)
    ElementGraph graph = new ElementGraph(session.getOrgan().getElements());
    if (graph.getVertices().isEmpty()) {
      graphWidget.setGraph(new EmptyGraph());
      return;
    }
    
    // collect all info
    Layout2D layout = new DefaultLayout(new Rectangle2D.Double(-20,-20,40,40));
    Rectangle bounds = new Rectangle();
    try {
      HierarchicalLayoutAlgorithm a = new HierarchicalLayoutAlgorithm();
      a.setDistanceBetweenLayers(30);
      a.setDistanceBetweenVertices(30);
      bounds = a.apply(graph, layout, null, null, new ElementComparator()).getBounds();
    } catch (LayoutAlgorithmException e) {
      // FIXME report something
      e.printStackTrace();
    }
    graphWidget.setGraphLayout(layout);
    graphWidget.setGraph(graph, bounds);
    
  }
  
  /**
   * our interpretation of sorted vertices in a layer
   */
  private class ElementComparator implements VertexInLayerComparator {
    public int compare(Vertex v1, Vertex v2, int layer, Layout2D layout) {
      Element e1 = ((DefaultVertex<Element>)v1).getContent();
      Element e2 = ((DefaultVertex<Element>)v2).getContent();
      return name(e1).compareTo(name(e2));
    }
  } //ElementComparator

  /** 
   * a filter action
   */
  private class ElementFilter {
    boolean isSelected;
    Class<? extends Element> type;
    
    ElementFilter(Class<? extends Element> type, boolean isSelected) {
      this.type = type;
      this.isSelected = isSelected;
    }
  } //ElementFilter
  
  /**
   * a graph of elements
   */
  private class ElementGraph implements Graph {
    
    private Map<Element, DefaultVertex<Element>> vertices;
    private List<DefaultEdge<Element>> edges = new ArrayList<DefaultEdge<Element>>();
    
    private ElementGraph(Collection<Element> elements) {
      vertices = new HashMap<Element, DefaultVertex<Element>>(elements.size());
      for (Element element : elements) {
        if (!filter(element))
          vertices.put(element, new DefaultVertex<Element>(element) {
            @Override
            public String toString() {
              return name(getContent());
            }
          });
      }
      for (Element element : elements) {
        if (!filter(element))
          for (Reference<? extends Element> ref : element.getReferences()) {
            if (!filter(ref.getElement()))
            edges.add(new DefaultEdge<Element>(vertices.get(element), vertices.get(ref.getElement())) {
              @Override
              public String toString() {
                return getContent().getName();
              }
            });
          }
      }
    }

    public Collection<? extends Edge> getEdges() {
      return edges;
    }

    public Collection<? extends Vertex> getVertices() {
      return vertices.values();
    }
    
  } //ElementGraph
  
  /**
   * our listener for organ events
   */
  private class ListenerImpl implements MouseListener, OrganListener, ElementSelectionListener, PlayListener {
    
    public void selectionChanged(ElementSelectionEvent ev) {
      ElementSelection es = session.getElementSelection();
      selection = es.getSelectedElements();
      graphWidget.repaint();
    }
    
    public void elementAdded(Element element) {
      rebuild();
    }

    public void elementRemoved(Element element) {
      rebuild();
    }

    public void messageAdded(Element element, Message message) {
    }

    public void messageChanged(Element element, Message message) {
    }

    public void messageRemoved(Element element, Message message) {
    }

    public void propertyChanged(Element element, String name) {
      graphWidget.repaint();
    }

    public void referenceAdded(Element element, Reference<?> reference) {
      rebuild();
    }

    public void referenceChanged(Element element, Reference<?> reference) {
      rebuild();
    }

    public void referenceRemoved(Element element, Reference<?> reference) {
      rebuild();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    @SuppressWarnings({ "unchecked" })
    public void mousePressed(MouseEvent e) {
      DefaultVertex<Element> vertex = (DefaultVertex<Element>)graphWidget.getVertexAt(e.getPoint());
      if (vertex!=null)
        session.getElementSelection().setSelectedElement(vertex.getContent());
    }

    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void closed() {
    }

    @Override
    public void opened() {
      selection = null;
      graphWidget.repaint();
    }

    @Override
    public void received(int channel, int command, int data1, int data2) {
    }

    @Override
    public void sent(int channel, int command, int data1, int data2) {
    }
    
  } //OrganCallback

}
