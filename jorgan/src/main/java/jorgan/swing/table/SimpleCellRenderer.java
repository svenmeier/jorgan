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
		return super.getTableCellRendererComponent(table,
				getDisplayValue((T) value), isSelected, hasFocus, row, column);
	}

	protected abstract Object getDisplayValue(T value);
}
