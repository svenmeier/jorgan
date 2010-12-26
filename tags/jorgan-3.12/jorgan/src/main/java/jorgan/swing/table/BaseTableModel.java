package jorgan.swing.table;

import javax.swing.table.AbstractTableModel;

public abstract class BaseTableModel<T> extends AbstractTableModel {

	private String[] columnNames;

	public void setColumnNames(String[] columnNames) {
		if (columnNames.length != getColumnCount()) {
			throw new IllegalArgumentException("must have length "
					+ getColumnCount());
		}
		this.columnNames = columnNames;
	}

	public String[] getColumnNames() {
		if (columnNames == null) {
			columnNames = new String[getColumnCount()];
		}

		return columnNames;
	}

	@Override
	public String getColumnName(int column) {
		if (columnNames == null) {
			return Integer.toString(column + 1);
		}

		return columnNames[column];
	}

	@Override
	public final Object getValueAt(int rowIndex, int columnIndex) {
		T row = getRow(rowIndex);

		return getValue(row, columnIndex);
	}

	@Override
	public final void setValueAt(Object value, int rowIndex, int columnIndex) {
		T row = getRow(rowIndex);

		setValue(row, columnIndex, value);

		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	protected abstract T getRow(int rowIndex);

	@Override
	public final boolean isCellEditable(int rowIndex, int columnIndex) {
		return isEditable(getRow(rowIndex), columnIndex);
	}

	protected boolean isEditable(T row, int columnIndex) {
		return false;
	}

	protected abstract Object getValue(T row, int columnIndex);

	protected void setValue(T row, int columnIndex, Object value) {
	}
}
