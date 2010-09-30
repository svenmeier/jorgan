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
		Component component = super.getListCellRendererComponent(list, null,
				index, isSelected, cellHasFocus);

		init((T) value);

		return component;
	}

	protected abstract void init(T value);
}
