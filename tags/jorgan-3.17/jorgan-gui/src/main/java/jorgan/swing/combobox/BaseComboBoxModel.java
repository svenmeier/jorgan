package jorgan.swing.combobox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class BaseComboBoxModel<T> extends AbstractListModel implements
		ComboBoxModel {

	private T selected;

	private List<T> elements;

	public BaseComboBoxModel(T... elements) {
		this(false, elements);
	}

	public BaseComboBoxModel(List<T> elements) {
		this(false, elements);
	}

	public BaseComboBoxModel(boolean includeNull, T... elements) {
		this(includeNull, Arrays.asList(elements));
	}

	public BaseComboBoxModel(boolean includeNull, List<T> elements) {
		this.elements = new ArrayList<T>();
		if (includeNull) {
			this.elements.add(null);
		}

		this.elements.addAll(elements);
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		if (anItem instanceof String) {
			selected = convert((String) anItem);
		} else {
			selected = (T) anItem;
		}

		fireContentsChanged(this, -1, -1);
	}

	@SuppressWarnings("unchecked")
	protected T convert(String element) {
		return (T) element;
	}

	@Override
	public Object getElementAt(int index) {
		return elements.get(index);
	}

	@Override
	public int getSize() {
		return elements.size();
	}
}
