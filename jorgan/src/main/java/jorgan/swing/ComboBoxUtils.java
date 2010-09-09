package jorgan.swing;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

public class ComboBoxUtils {

	public static <T> ComboBoxModel createModelWithNull(List<T> items) {
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
}
