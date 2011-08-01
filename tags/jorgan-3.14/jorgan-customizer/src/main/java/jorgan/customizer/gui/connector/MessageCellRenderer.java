package jorgan.customizer.gui.connector;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import jorgan.disposition.Message;
import jorgan.gui.OrganPanel;
import jorgan.swing.table.SimpleCellRenderer;

public abstract class MessageCellRenderer extends SimpleCellRenderer<Message> {

	private static final Icon inputIcon = new ImageIcon(OrganPanel.class
			.getResource("img/input.gif"));

	private Color defaultBackground;

	public MessageCellRenderer() {
		setHorizontalAlignment(CENTER);

		defaultBackground = getBackground();
	}

	@Override
	protected void init(Message value) {
		if (value != null && isHighlighted(value)) {
			setBackground(Color.yellow);
		} else {
			setBackground(defaultBackground);
		}

		if (value != null) {
			setIcon(inputIcon);

			setToolTipText(value.getTuple().toString());
		} else {
			setIcon(null);

			setToolTipText(null);
		}
	}

	protected abstract boolean isHighlighted(Message value);
}