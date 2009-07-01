package jorgan.graph.gui.dock;

import gj.layout.LayoutException;
import gj.layout.graph.hierarchical.HierarchicalLayout;
import gj.model.Edge;
import gj.model.Vertex;
import gj.ui.DefaultGraphRenderer;
import gj.ui.GraphWidget;
import gj.util.DefaultEdge;
import gj.util.DefaultGraph;
import gj.util.DefaultLayoutContext;
import gj.util.DefaultVertex;
import gj.util.EmptyGraph;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import jorgan.disposition.Captor;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Message;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.OrganDockable;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.play.OrganPlay;
import jorgan.play.event.PlayListener;
import jorgan.session.OrganSession;
import jorgan.swing.button.ButtonGroup;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;

/**
 * Dockable for showing structure of a disposition
 */
public class StructureDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			StructureDockable.class);

	private GraphWidget graphWidget;

	private ListenerImpl listener = new ListenerImpl();

	private OrganSession session = null;

	private List<Element> selection = new ArrayList<Element>();

	private List<Class<? extends Element>> sources = new ArrayList<Class<? extends Element>>();

	private List<JToggleButton> sourcesToggles = new ArrayList<JToggleButton>();

	/**
	 * Constructor
	 */
	public StructureDockable() {
		config.read(this);

		graphWidget = new GraphWidget();
		graphWidget.setRenderer(new DefaultGraphRenderer() {
			@Override
			@SuppressWarnings( { "unchecked" })
			protected Color getColor(Vertex vertex) {
				if (selection.isEmpty())
					return Color.BLACK;
				if (selection.contains(((DefaultVertex<Element>) vertex)
						.getContent()))
					return Color.BLUE;
				return Color.LIGHT_GRAY;
			}

			@Override
			@SuppressWarnings( { "unchecked" })
			protected Color getColor(Edge edge) {
				if (selection.isEmpty())
					return Color.BLACK;
				if (selection.contains(((DefaultVertex<Element>) edge
						.getStart()).getContent())
						|| selection.contains(((DefaultVertex<Element>) edge
								.getEnd()).getContent()))
					return Color.BLUE;
				return Color.LIGHT_GRAY;
			}

			@Override
			@SuppressWarnings( { "unchecked" })
			protected Icon getIcon(Vertex vertex) {
				return Elements.getIcon(((DefaultVertex<Element>) vertex)
						.getContent().getClass());
			}

			@Override
			@SuppressWarnings( { "unchecked" })
			protected String getText(Vertex vertex) {
				return name(((DefaultVertex<Element>) vertex).getContent());
			}
		});

		graphWidget.addMouseListener(listener);

		// prepare source buttons
		sources.add(Captor.class);
		sources.add(Keyboard.class);

		ButtonGroup sourceGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				rebuild();
			}
		};

		for (int i = 0; i < sources.size(); i++) {
			JToggleButton toggle = new JToggleButton(sources.get(i)
					.getSimpleName(), Elements.getIcon(sources.get(i)));
			sourcesToggles.add(toggle);
			sourceGroup.add(toggle);
		}

		// prepare contents
		setContent(new JScrollPane(graphWidget));

		// done
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	/** callback - being docked */
	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		for (JToggleButton button : sourcesToggles)
			docked.addTool(button);

		rebuild();
	}

	/** calback - session opened */
	@Override
	public void setSession(OrganSession session) {

		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(listener));
			this.session.lookup(ElementSelection.class).removeListener(
					(SelectionListener) Spin.over(listener));
			this.session.lookup(OrganPlay.class).removePlayerListener(
					(PlayListener) Spin.over(listener));

			graphWidget.setGraph2D(new EmptyGraph());
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(listener));
			this.session.lookup(ElementSelection.class).addListener(
					(SelectionListener) Spin.over(listener));
			this.session.lookup(OrganPlay.class).addPlayerListener(
					(PlayListener) Spin.over(listener));

			rebuild();
		}
	}

	/**
	 * create a non-empty name representation
	 */
	private String name(Element element) {
		return element.getName().length() > 0 ? element.getName() : element
				.getClass().getSimpleName();
	}

	/**
	 * find all sources
	 */
	private Set<Element> sources() {

		Class<? extends Element> sourcetype = null;
		for (int i = 0; i < sourcesToggles.size(); i++) {
			if (sourcesToggles.get(i).isSelected()) {
				sourcetype = sources.get(i);
				break;
			}
		}
		if (sourcetype == null)
			throw new IllegalArgumentException("no source type selected");

		Set<Element> result = new HashSet<Element>();
		for (Element element : session.getOrgan().getElements()) {
			if (sourcetype.isAssignableFrom(element.getClass()))
				result.add(element);
		}

		return result;
	}

	/**
	 * rebuild structure
	 */
	private void rebuild() {
		if (!isDocked()) {
			return;
		}

		// build an element graph on all sources
		// TODO this is kinda bad to redo all over every time :)
		ElementGraph graph = new ElementGraph(sources());
		if (graph.getVertices().isEmpty()) {
			graphWidget.setGraph2D(new EmptyGraph());
			return;
		}

		// collect all info
		Rectangle bounds = new Rectangle();
		try {
			HierarchicalLayout a = new HierarchicalLayout();
			a.setDistanceBetweenLayers(30);
			a.setDistanceBetweenVertices(30);
			a.setOrderOfVerticesInLayer(new ElementComparator());
			bounds = a.apply(graph, new DefaultLayoutContext()).getBounds();
		} catch (LayoutException e) {
			// FIXME report something
			e.printStackTrace();
		}
		graphWidget.setGraph2D(graph, bounds);

	}

	/**
	 * our interpretation of sorted vertices in a layer
	 */
	private class ElementComparator implements Comparator<Vertex> {
		@SuppressWarnings( { "unchecked" })
		public int compare(Vertex v1, Vertex v2) {
			Element e1 = ((DefaultVertex<Element>) v1).getContent();
			Element e2 = ((DefaultVertex<Element>) v2).getContent();
			return name(e1).compareTo(name(e2));
		}
	} // ElementComparator

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
	} // ElementFilter

	/**
	 * a graph of elements
	 */
	private class ElementGraph extends DefaultGraph {

		private Map<Element, DefaultVertex<Element>> vertices = new HashMap<Element, DefaultVertex<Element>>();

		private List<DefaultEdge<Element>> edges = new ArrayList<DefaultEdge<Element>>();

		private ElementGraph(Set<Element> sources) {
			super(null, new Ellipse2D.Double(-30, -20, 60, 40));
			for (Element source : sources) {
				DefaultVertex<Element> vertex = new DefaultVertex<Element>(
						source);
				vertices.put(source, vertex);
				source2sink(vertex);
			}
		}

		private void source2sink(DefaultVertex<Element> from) {

			for (Reference<? extends Element> reference : from.getContent()
					.getReferences()) {
				DefaultVertex<Element> to = vertex(reference.getElement());
				edges.add(new DefaultEdge<Element>(from, to));
				source2sink(to);
			}

		}

		private DefaultVertex<Element> vertex(Element element) {
			DefaultVertex<Element> result = vertices.get(element);
			if (result == null) {
				result = new DefaultVertex<Element>(element);
				vertices.put(element, result);
			}
			return result;
		}

		@Override
		public Collection<? extends Edge> getEdges() {
			return edges;
		}

		@Override
		public Collection<? extends Vertex> getVertices() {
			return vertices.values();
		}

	} // ElementGraph

	/**
	 * our listener for organ events
	 */
	private class ListenerImpl implements MouseListener, OrganListener,
			SelectionListener, PlayListener {

		public void selectionChanged() {
			selection = session.lookup(ElementSelection.class)
					.getSelectedElements();

			if (isDocked()) {
				graphWidget.repaint();
			}
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
			// rebuild graph if name has changed (a change in order of vertices)
			if ("name".equals(name)) {
				rebuild();
			}
		}

		public void referenceAdded(Element element, Reference<?> reference) {
			rebuild();
		}

		public void referenceChanged(Element element, Reference<?> reference) {
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

		@SuppressWarnings( { "unchecked" })
		public void mousePressed(MouseEvent e) {
			DefaultVertex<Element> vertex = (DefaultVertex<Element>) graphWidget
					.getVertexAt(e.getPoint());
			if (vertex == null)
				return;
			Element element = vertex.getContent();
			ElementSelection selection = session.lookup(ElementSelection.class);
			if ((e.getModifiers() & MouseEvent.CTRL_MASK) != 0) {
				if (selection.isSelected(element))
					selection.removeSelectedElement(element);
				else
					selection.addSelectedElement(element);
			} else {
				selection.setSelectedElement(element);
			}
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void closed() {
		}

		public void opened() {
			graphWidget.repaint();
		}

		public void received(int channel, int command, int data1, int data2) {
		}

		public void sent(int channel, int command, int data1, int data2) {
		}

	} // OrganCallback

}
