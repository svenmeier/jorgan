package jorgan.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class RowHeader extends JPanel {

	private JComponent view;

	public RowHeader(JComponent view) {
		setLayout(new Layout());

		this.view = view;
	}

	public void configureEnclosingScrollPane() {
		Container p = view.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != view) {
					return;
				}
				scrollPane.setRowHeaderView(this);
			}
		}
	}

	private class Layout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			int width = 0;
			int height = 0;

			for (Component child : parent.getComponents()) {
				Dimension size = child.getMinimumSize();

				width = Math.max(width, size.width);
				height += size.height;
			}

			return new Dimension(width, height);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			int width = 0;

			for (Component child : parent.getComponents()) {
				Dimension size = child.getPreferredSize();

				width = Math.max(width, size.width);
			}

			return new Dimension(width, view.getPreferredSize().height);
		}

		@Override
		public void layoutContainer(Container parent) {
			int width = parent.getWidth();
			int height = view.getPreferredSize().height;
			int y = 0;
			int count = parent.getComponentCount();

			for (Component child : parent.getComponents()) {
				child.setBounds(0, y, width, height / count);

				y += height / count;
			}
		}
	}
}
