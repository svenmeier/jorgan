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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * A model for a {@link javax.swing.JTree} serving a hierarchy of
 * {@link bias.swing.Category}s.
 * 
 * @see Category#getName()
 * @see Category#getParentCategory()
 */
public class CategoriesTreeModel extends DefaultTreeModel {

	private List<Category> categories;

	/**
	 * Create a tree for the given provider of categories.
	 * 
	 * @param provider
	 *            provider of categories
	 */
	public CategoriesTreeModel(List<Category> categories) {
		super(new DefaultMutableTreeNode());

		this.categories = categories;

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
		append(root, null);
	}

	private void append(DefaultMutableTreeNode parentNode, Class<?> parentCategory) {
		for (Category category : categories) {
			if (category.getParentCategory() == parentCategory) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(
						category);

				parentNode.add(child);

				append(child, category.getClass());
			}
		}
	}
}