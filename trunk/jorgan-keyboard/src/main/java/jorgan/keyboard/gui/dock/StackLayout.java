package jorgan.keyboard.gui.dock;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class StackLayout implements LayoutManager {

	private int spacing;

	public StackLayout(int spacing) {
		this.spacing = spacing;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		int width = 0;
		int height = 0;

		for (Component child : parent.getComponents()) {
			if (height > 0) {
				height += spacing;
			}
			Dimension size = child.getMinimumSize();
			width = Math.max(width, size.width);
			height = Math.max(height, size.height);
		}

		return new Dimension(width, height);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		int width = 0;
		int height = 0;

		for (Component child : parent.getComponents()) {
			if (height > 0) {
				height += spacing;
			}
			Dimension size = child.getPreferredSize();
			width = Math.max(width, size.width);
			height += size.height;
		}

		return new Dimension(width, height);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void layoutContainer(Container parent) {
		Dimension preferred = parent.getPreferredSize();

		int height = parent.getHeight();
		int width = parent.getWidth();
		int top = Math.max(0, (height - preferred.height) / 2);
		int bottom = Math.min(height, top + preferred.height);
		int count = parent.getComponentCount();
		int n = 0;
		for (Component child : parent.getComponents()) {
			Dimension size = child.getPreferredSize();

			if (n == 0) {
				child.setBounds(0, top, width, size.height);

				top += size.height;
			} else {
				int y = top + (bottom - top) * n / (count - 1);

				child.setBounds(0, y - size.height, width, size.height);
			}

			n++;
		}
	}
}