package jorgan.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jorgan.disposition.Element;
import jorgan.disposition.Group;
import jorgan.disposition.Organ;
import jorgan.swing.tree.BaseTreeModel;

public class ElementTreeModel extends BaseTreeModel<Element> {

	private Organ organ;

	private List<Element> roots = new ArrayList<Element>();

	private Map<Group, List<Element>> grouped = new HashMap<Group, List<Element>>();

	private Comparator<Element> comparator;

	public ElementTreeModel() {
	}

	public void clearElements() {
		organ = null;

		roots.clear();

		grouped.clear();
	}

	public void setElements(Organ organ, Set<Element> elements,
			Comparator<Element> comparator) {
		this.organ = organ;
		this.comparator = comparator;

		roots.clear();
		for (Element element : elements) {
			Set<Group> referrer = organ.getReferrer(element, Group.class);
			if (referrer.isEmpty()) {
				roots.add(element);
			}
		}
		Collections.sort(roots, comparator);

		grouped.clear();

		fireRootsChanged();
	}

	@Override
	protected List<Element> getRoots() {
		return roots;
	}

	@Override
	protected List<Element> getChildren(Element element) {
		if (element instanceof Group) {
			Group group = (Group) element;
			List<Element> grouped = this.grouped.get(group);
			if (grouped == null) {
				grouped = group.getReferenced(Element.class);
				Collections.sort(grouped, comparator);
				this.grouped.put(group, grouped);
			}
			return grouped;
		}
		return Collections.emptyList();
	}

	@Override
	protected Set<Element> getParents(Element element) {
		Set<Group> groups = organ.getReferrer(element, Group.class);

		return new HashSet<Element>(groups);
	}

	@Override
	public boolean hasChildren(Element element) {
		return element instanceof Group;
	}
}
