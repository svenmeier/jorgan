package jorgan.swing.list;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 */
public abstract class SimpleCellRenderer<T> extends DefaultListCellRenderer {

	@SuppressWarnings("unchecked")
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, null, index, isSelected,
				cellHasFocus);

		if (value == null) {
			setIcon(null);
			setText(null);
		} else {
			init((T) value, isSelected, cellHasFocus, index);
		}

		return this;
	}

	protected void init(T value, boolean isSelected, boolean hasFocus, int index) {
		init(value);
	}

	protected void init(T value) {
	}
}
