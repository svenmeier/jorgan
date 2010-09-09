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
		return super.getListCellRendererComponent(list,
				getDisplayValue((T) value), index, isSelected, cellHasFocus);
	}

	protected abstract Object getDisplayValue(T value);
}
