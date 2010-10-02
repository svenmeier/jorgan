package jorgan.swing.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 */
public abstract class SimpleCellRenderer<T> extends DefaultTreeCellRenderer {

	@SuppressWarnings("unchecked")
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean isExpanded, boolean isLeaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, null, selected, isExpanded,
				isLeaf, row, hasFocus);

		if (value != BaseTreeModel.ROOT) {
			init((T) value);
		}

		return this;
	}

	protected abstract void init(T value);
}
