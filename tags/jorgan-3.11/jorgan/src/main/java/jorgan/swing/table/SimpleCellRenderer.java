package jorgan.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Simple base class for cell renderers.
 * 
 * @see #init(Object)
 * @see #init(Object, boolean, boolean, int, int)
 */
public abstract class SimpleCellRenderer<T> extends DefaultTableCellRenderer {

	@SuppressWarnings("unchecked")
	@Override
	public final Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus,
				row, column);

		init((T) value, isSelected, hasFocus, row, column);

		return this;
	}

	protected void init(T value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		init(value);
	}

	protected void init(T value) {
	}
}
