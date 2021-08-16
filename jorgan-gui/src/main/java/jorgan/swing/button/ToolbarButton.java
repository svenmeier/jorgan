package jorgan.swing.button;

import javax.swing.JButton;

public class ToolbarButton extends JButton {

	public ToolbarButton() {
		putClientProperty("JButton.buttonType", "toolbar");
	}

	public ToolbarButton(String string) {
		super(string);
	}
}
