package jorgan.graph.gui.dock;

import gj.layout.DefaultLayout;
import gj.layout.Layout2D;
import gj.layout.LayoutAlgorithmException;
import gj.layout.hierarchical.HierarchicalLayoutAlgorithm;
import gj.model.Edge;
import gj.model.Graph;
import gj.model.Vertex;
import gj.ui.GraphWidget;
import gj.util.DefaultEdge;
import gj.util.DefaultVertex;
import gj.util.EmptyGraph;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.OrganDockable;
import jorgan.session.OrganSession;
import bias.Configuration;

/**
 * Dockable for showing structure of a disposition
 */
public class StructureDockable extends OrganDockable {
  
  private static Configuration config = Configuration.getRoot().get(StructureDockable.class);
  private GraphWidget graphWidget;
  private OrganListenerImpl organListener = new OrganListenerImpl(); 
  private OrganSession session = null;
  
  /**
   * Constructor
   */
  public StructureDockable() {
    config.read(this);
    
    graphWidget = new GraphWidget();
    setContent(new JScrollPane(graphWidget));
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
    
    // collect all info
    // TODO this is kinda bad to redo all over every time :)
    ElementGraph graph = new ElementGraph(session.getOrgan().getElements());
    Layout2D layout = new DefaultLayout(new Rectangle2D.Double(-20,-20,40,40));
    Shape shape = new Rectangle2D.Double();
    try {
      HierarchicalLayoutAlgorithm a = new HierarchicalLayoutAlgorithm();
      a.setDistanceBetweenLayers(80);
      a.setDistanceBetweenVertices(80);
      shape = a.apply(graph, layout, null, null);
    } catch (LayoutAlgorithmException e) {
      // FIXME report something
      e.printStackTrace();
    }
    graphWidget.setGraphLayout(layout);
    graphWidget.setGraph(graph, shape.getBounds());
    
  }

  @Override
  public void setSession(OrganSession session) {

    if (this.session!=null)
      this.session.removeOrganListener(organListener);
    
    this.session = session;

    if (this.session!=null)
      this.session.addOrganListener(organListener);
    
    rebuild();
  }
  
  /**
   * a graph of elements
   */
  private class ElementGraph implements Graph {
    
    private Map<Element, DefaultVertex<Element>> vertices;
    private List<DefaultEdge<Element>> edges = new ArrayList<DefaultEdge<Element>>();
    
    private ElementGraph(Collection<Element> elements) {
      vertices = new HashMap<Element, DefaultVertex<Element>>(elements.size());
      for (Element element : elements) {
        vertices.put(element, new DefaultVertex<Element>(element) {
          @Override
          public String toString() {
            Element e = getContent();
            return e.getName().length()>0 ? getContent().getName() : e.getClass().getSimpleName();
          }
        });
      }
      for (Element element : elements) {
        for (Reference<? extends Element> ref : element.getReferences()) {
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
   * our core listener
   */
  private class OrganListenerImpl implements OrganListener {

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
    
  } //OrganCallback

}
