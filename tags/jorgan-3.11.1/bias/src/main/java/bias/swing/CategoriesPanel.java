/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import bias.ConfigurationException;

/**
 * A panel showing categories.
 */
public class CategoriesPanel extends JPanel {

	private JSplitPane splitPane = new JSplitPane();

	private JTree tree = new JTree();

	private JPanel rightPanel = new JPanel();

	private JButton restoreButton;

	private JButton applyButton;

	private Category currentCategory;

	private Map<Category, JComponent> components = new HashMap<Category, JComponent>();

	private List<Category> categories;

	/**
	 * Constructor.
	 */
	public CategoriesPanel() {
		super(new BorderLayout());

		splitPane.setContinuousLayout(true);
		splitPane.setBorder(null);
		((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(null);
		add(splitPane, BorderLayout.CENTER);

		tree.setCellRenderer(new CategoryRenderer());
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath selection = e.getPath();
				if (selection == null) {
					categorySelected(null);
				} else {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selection
							.getLastPathComponent();

					categorySelected((Category) treeNode.getUserObject());
				}
			}
		});
		splitPane.setLeftComponent(tree);

		rightPanel.setLayout(new BorderLayout());
		splitPane.setRightComponent(rightPanel);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);

		restoreButton = new JButton("Restore");
		restoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					currentCategory.restore();
				} catch (ConfigurationException ex) {
					onPreferenceException(ex);
				}
			}
		});
		buttonPanel.add(restoreButton);

		applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					currentCategory.apply();
				} catch (ConfigurationException ex) {
					onPreferenceException(ex);
				}
			}
		});
		buttonPanel.add(applyButton);

		categorySelected(null);
	}

	protected void onPreferenceException(ConfigurationException ex) {
		ex.printStackTrace();
	}

	/**
	 * Set the categories.
	 * 
	 * @param categories
	 */
	public void setCategories(List<Category> categories) {

		this.categories = categories;

		tree.setModel(new CategoriesTreeModel(categories));
		tree.expandRow(0);
	}

	/**
	 * Apply all categories.
	 */
	public void applyAll() {
		for (Category category : categories) {
			if (getComponent(category, false) != null) {
				try {
					category.apply();
				} catch (ConfigurationException ex) {
					onPreferenceException(ex);
				}
			}
		}
	}

	private JComponent getComponent(Category category, boolean create) {
		JComponent component = components.get(category);
		if (component == null && create) {
			component = category.getComponent();
			components.put(category, component);
		}
		return component;
	}

	protected void categorySelected(Category category) {
		if (currentCategory != null) {
			rightPanel.remove(getComponent(currentCategory, true));
		}

		currentCategory = category;

		if (currentCategory != null) {
			rightPanel.add(getComponent(currentCategory, true),
					BorderLayout.CENTER);
		}

		restoreButton.setEnabled(currentCategory != null);
		applyButton.setEnabled(currentCategory != null);
		rightPanel.revalidate();
		rightPanel.repaint();
	}

	private class CategoryRenderer extends DefaultTreeCellRenderer {

		private CategoryRenderer() {
			setOpenIcon(null);
			setClosedIcon(null);
			setLeafIcon(null);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			String text = null;

			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;

			Object userObject = treeNode.getUserObject();
			if (userObject != null && userObject instanceof Category) {
				Category category = (Category) treeNode.getUserObject();

				text = category.getName();
			}

			Component component = super.getTreeCellRendererComponent(tree,
					text, selected, expanded, leaf, row, hasFocus);

			return component;
		}
	}
}