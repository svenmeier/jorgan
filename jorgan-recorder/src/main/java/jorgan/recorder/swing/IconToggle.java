package jorgan.recorder.swing;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JComponent;

import jorgan.swing.MouseUtils;

public abstract class IconToggle extends JComponent {

	private Icon onIcon = new EmptyIcon();

	private Icon offIcon = new EmptyIcon();

	public IconToggle() {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (MouseUtils.isHorizontalScroll(e)) {
					return;
				}
				toggle();
			}
		});
	}

	protected void toggle() {
		repaint();
	}

	public void setOnIcon(Icon onIcon) {
		this.onIcon = onIcon;
	}

	public void setOffIcon(Icon offIcon) {
		this.offIcon = offIcon;
	}

	public void setIcon(Icon icon) {
		this.onIcon = icon;

		this.offIcon = new TranslucentIcon(icon);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(
				Math.max(onIcon.getIconWidth(), offIcon.getIconWidth()),
				Math.max(onIcon.getIconHeight(), offIcon.getIconHeight()));
	}

	protected abstract boolean isOn();

	@Override
	public void paint(Graphics g) {
		if (isOn()) {
			paintIcon(g, onIcon);
		} else {
			paintIcon(g, offIcon);
		}
	}

	private void paintIcon(Graphics g, Icon icon) {
		int x = (getWidth() - icon.getIconWidth()) / 2;
		int y = (getHeight() - icon.getIconHeight()) / 2;

		icon.paintIcon(this, g, x, y);
	}

	private class EmptyIcon implements Icon {
		public int getIconHeight() {
			return 0;
		}

		public int getIconWidth() {
			return 0;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
		}
	}

	private class TranslucentIcon implements Icon {

		private Icon icon;

		public TranslucentIcon(Icon icon) {
			this.icon = icon;
		}

		public int getIconWidth() {
			return icon.getIconWidth();
		}

		public int getIconHeight() {
			return icon.getIconHeight();
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {

			Graphics2D g2d = ((Graphics2D) g);

			Composite composite = g2d.getComposite();
			g2d.setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));

			icon.paintIcon(c, g, x, y);

			g2d.setComposite(composite);
		}
	}
}
