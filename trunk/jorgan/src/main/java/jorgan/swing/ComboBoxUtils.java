package jorgan.swing;

import java.util.Arrays;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

public class ComboBoxUtils {

	public static ComboBoxModel createModel(Object[] items) {
		return new DefaultComboBoxModel(items);
	}

	public static ComboBoxModel createModelWithNull(Object[] items) {
		return new DefaultComboBoxModel(withNull(items));
	}

	public static <T> T[] withNull(T[] items) {
		T[] copy = Arrays.copyOf(items, items.length + 1);
		System.arraycopy(items, 0, copy, 1, items.length);
		copy[0] = null;

		return copy;
	}
}
