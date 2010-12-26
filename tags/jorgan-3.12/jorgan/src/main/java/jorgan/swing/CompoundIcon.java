package jorgan.swing;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * An icon composed of other icons.
 */
public class CompoundIcon implements Icon {

	private Icon[] icons;

	public CompoundIcon(Icon... icons) {
		this.icons = icons;
	}

	public int getIconHeight() {
		int height = 0;
		for (Icon icon : icons) {
			height = Math.max(height, icon.getIconHeight());
		}
		return height;
	}

	public int getIconWidth() {
		int width = 0;
		for (Icon icon : icons) {
			width = Math.max(width, icon.getIconWidth());
		}
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		for (Icon icon : icons) {
			icon.paintIcon(c, g, x, y);
		}
	}
}
