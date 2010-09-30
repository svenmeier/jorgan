package jorgan.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 */
public abstract class SimpleCellRenderer<T> extends DefaultTableCellRenderer {

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component component = super.getTableCellRendererComponent(table, null,
				isSelected, hasFocus, row, column);

		init((T) value);

		return component;
	}

	protected abstract void init(T value);
}
