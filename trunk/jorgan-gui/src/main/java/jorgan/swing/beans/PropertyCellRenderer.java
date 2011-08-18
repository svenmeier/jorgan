package jorgan.swing.beans;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public abstract class PropertyCellRenderer extends JLabel implements
		TableCellRenderer {

	public PropertyCellRenderer() {
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
		setText("Dummy");
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setFont(table.getFont());

		if (getEditor(row) == null) {
			if (value == null) {
				setText("");
			} else {
				setText(value.toString());
			}
			setToolTipText(null);
		} else {
			getEditor(row).setValue(value);

			setText(getEditor(row).getAsText());
		}

		setToolTipText(getDescription(row));
		return this;
	}

	protected abstract PropertyEditor getEditor(int row);

	protected abstract String getDescription(int row);
}
