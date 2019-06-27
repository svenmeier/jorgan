package jorgan.swing.combobox;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class ComboBoxUtils {

	private static Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

	public static void beautify(JComboBox comboBox) {
		comboBox.setBorder(EMPTY_BORDER);

		Component component = comboBox.getEditor().getEditorComponent();
		if (component instanceof JComponent) {
			((JComponent) component).setBorder(EMPTY_BORDER);
		}
	}
}
