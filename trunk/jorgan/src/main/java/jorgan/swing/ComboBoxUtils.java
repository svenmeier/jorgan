package jorgan.swing;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class ComboBoxUtils {

	private static Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

	public static <T> ComboBoxModel createModel(Collection<T> items) {
		Vector<Object> vector = new Vector<Object>();
		vector.addAll(items);
		return new DefaultComboBoxModel(vector);
	}

	public static <T> ComboBoxModel createModelWithNull(Collection<T> items) {
		Vector<Object> vector = new Vector<Object>();
		vector.add(null);
		vector.addAll(items);
		return new DefaultComboBoxModel(vector);
	}

	public static <T> ComboBoxModel createModelWithNull(T... items) {
		Vector<Object> vector = new Vector<Object>();
		vector.add(null);
		for (Object item : items) {
			vector.add(item);
		}
		return new DefaultComboBoxModel(vector);
	}

	public static <T> T[] withNull(T... items) {
		T[] copy = Arrays.copyOf(items, items.length + 1);
		System.arraycopy(items, 0, copy, 1, items.length);
		copy[0] = null;

		return copy;
	}

	public static void beautify(JComboBox comboBox) {
		comboBox.setBorder(EMPTY_BORDER);

		Component component = comboBox.getEditor().getEditorComponent();
		if (component instanceof JComponent) {
			((JComponent) component).setBorder(EMPTY_BORDER);
		}
	}
}
