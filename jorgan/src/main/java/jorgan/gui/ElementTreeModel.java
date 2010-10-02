package jorgan.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jorgan.disposition.Element;
import jorgan.disposition.Group;
import jorgan.disposition.Organ;
import jorgan.swing.tree.BaseTreeModel;

public class ElementTreeModel extends BaseTreeModel<Element> {

	private Organ organ;

	private List<Element> roots = new ArrayList<Element>();

	private Comparator<Element> comparator;

	public ElementTreeModel() {
	}

	public void clearElements() {
		organ = null;

		roots = Collections.emptyList();
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

		fireRootsChanged();
	}

	@Override
	protected List<Element> getRoots() {
		Collections.sort(roots, comparator);

		return roots;
	}

	@Override
	protected List<Element> getChildren(Element element) {
		if (element instanceof Group) {
			List<Element> referenced = element.getReferenced(Element.class);
			Collections.sort(referenced, comparator);
			return referenced;
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
