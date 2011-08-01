/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */package jorgan.swing.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

/**
 * A cell editor/renderer that adds a {@link javax.swing.JCheckBox} to any tree cell.
 */
public class CheckedTreeCell extends AbstractCellEditor implements TreeCellEditor, TreeCellRenderer {

	private TreeCellRenderer renderer;
	
	private boolean updating = false;
	
	private JPanel panel = new JPanel(new BorderLayout());
	private JCheckBox checkBox = new JCheckBox();
	
	/**
	 * Cosntructor.
	 */
	public CheckedTreeCell() {
		this(createDefaultTreeCellRenderer());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param renderer	the renderer to wrap
	 */
	public CheckedTreeCell(TreeCellRenderer renderer) {
		this.renderer = renderer;
		
		panel.setOpaque(false);
		checkBox.setOpaque(false);
		checkBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!updating) {
					fireEditingStopped();
				}
			}
		});
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component component = renderer.getTreeCellRendererComponent(tree, convertValue(value), selected, expanded, leaf, row, hasFocus);
		
		if (isCheckable(value, leaf)) {
			panel.removeAll();
			
			updating = true;
			checkBox.setSelected(isChecked(value));
			updating = false;
			
			panel.add(checkBox, BorderLayout.WEST);
			panel.add(component, BorderLayout.CENTER);

			panel.revalidate();
			panel.repaint();
			
			return panel;
		} else {
			return component; 
		}
	}
	
	protected boolean isCheckable(Object value, boolean leaf) {
		return leaf;
	}
	
	protected boolean isChecked(Object value) {
		return true;
	}
	
    protected Object convertValue(Object value) {
    	return value;
    }

    @Override
	public boolean shouldSelectCell(EventObject anEvent) {
    	return true;
    }
    
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
    	
		Component component = renderer.getTreeCellRendererComponent(tree, convertValue(value), true, expanded, leaf, row, true);

		if (isCheckable(value, leaf)) {
			panel.removeAll();
			
			updating = true;
			checkBox.setSelected(isChecked(value));
			updating = false;

			panel.add(checkBox, BorderLayout.WEST);
			panel.add(component);

			panel.revalidate();
			
			return panel;
		} else {
			return component; 
		}
    }
    
    public Object getCellEditorValue() {    	
    	return Boolean.valueOf(checkBox.isSelected());
    }
    
	private static TreeCellRenderer createDefaultTreeCellRenderer() {
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		
		renderer.setLeafIcon(null);
		
		return renderer;
	}
}