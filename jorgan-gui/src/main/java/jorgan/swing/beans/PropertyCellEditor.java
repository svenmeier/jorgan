package jorgan.swing.beans;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import jorgan.swing.combobox.ComboBoxUtils;

public abstract class PropertyCellEditor extends AbstractCellEditor
		implements TableCellEditor {

	private PropertyEditor editor;

	private JTextField textField = new JTextField();

	private JComboBox comboBox = new JComboBox();

	public PropertyCellEditor() {
		textField.setOpaque(false);
		textField.setBorder(null);
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				stopCellEditing();
			}
		});

		comboBox.setEditable(true);
		ComboBoxUtils.beautify(comboBox);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				stopCellEditing();
			}
		});
	}

	protected abstract PropertyEditor getEditor(int row);

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		editor = getEditor(row);
		editor.setValue(value);

		Component component;
		if (editor.supportsCustomEditor()) {
			component = editor.getCustomEditor();
		} else {
			String[] tags = editor.getTags();
			if (tags == null) {
				textField.setText(editor.getAsText());
				textField.selectAll();
				component = textField;
			} else {
				comboBox.setModel(new DefaultComboBoxModel(tags));
				comboBox.setSelectedItem(editor.getAsText());
				component = comboBox;
			}
		}

		return component;
	}

	public Object getCellEditorValue() {
		if (!editor.supportsCustomEditor()) {
			try {
				if (editor.getTags() == null) {
					editor.setAsText(textField.getText());
				} else {
					// note: comboBox#getSelectedItem() might not be up-to-date
					String item = (String) comboBox.getEditor().getItem();
					if (item != null && item.trim().length() == 0) {
						item = null;
					}
					editor.setAsText(item);
				}
			} catch (IllegalArgumentException ignore) {
			}
		}
		return editor.getValue();
	}
}
