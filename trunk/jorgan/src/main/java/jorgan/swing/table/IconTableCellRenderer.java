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
 */
package jorgan.swing.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * A renderer for icons in a {@link javax.swing.JTable}. <br>
 * This renderer can be customized to render cells of a table according to a
 * defined mapping from values to icons, see {@link #IconTableCellRenderer(Map)}.
 * <br>
 * For example the attribute of an object could be representated by a graphical
 * representation, here the importance of a message:
 * 
 * <pre>
 *         0  -&gt;  exclamationmark.gif
 *         1  -&gt;  null
 *         2  -&gt;  downarrow.gif
 * </pre>
 * 
 * Another usage could be to map the classes of objects in the rows of the table
 * to small representativ icons:
 * 
 * <pre>
 *         Mail.class        -&gt; mail.gif
 *         Appointment.class -&gt; appointment.gif
 *         News.class        -&gt; news.gif
 * </pre>
 * 
 * (For this to work the TableModel must return the objects classes in the
 * corresponding column.)
 */
public class IconTableCellRenderer extends DefaultTableCellRenderer {

	private Icon defaultIcon;

	private Map icons;

	/**
	 * Constructor.
	 * 
	 * @param defaultIcon
	 *            icon to use for all values
	 */
	public IconTableCellRenderer(Icon defaultIcon) {

		this(defaultIcon, new HashMap<Object, Icon>());
	}

	/**
	 * Constructor. <br>
	 * The first icon in the map is used for all unknown values.
	 * 
	 * @param icons
	 *            a map defining the mapping from values to icons
	 */
	public IconTableCellRenderer(Map<?, Icon> icons) {

		this(icons.values().iterator().next(), icons);
	}

	/**
	 * Constructor.
	 * 
	 * @param defaultIcon
	 *            icon to use for all unknown values
	 * @param icons
	 *            a map defining the mapping from values to icons
	 */
	public IconTableCellRenderer(Icon defaultIcon, Map<?, Icon> icons) {

		this.defaultIcon = defaultIcon;
		this.icons = icons;
	}

	/**
	 * Overriden for icon specific behaviour.
	 */
	@Override
	protected void setValue(Object value) {

		Icon icon = defaultIcon;
		if (value != null) {
			icon = (Icon) icons.get(value);
			if (icon == null) {
				icon = defaultIcon;
			}
		}
		setIcon(icon);
	}

	/**
	 * Convinience method for configuration of a JTable. </br> The renderer is
	 * set up as the renderer for specified column, which is also configured to
	 * have the width of the defaultIcon and to be unresizable.
	 * 
	 * @param table
	 *            table to configure
	 * @param columnIndex
	 *            index of column in model
	 */
	public void configureTableColumn(JTable table, int columnIndex) {

		getTableCellRendererComponent(table, defaultIcon, false, false, 0, 0);

		columnIndex = table.convertColumnIndexToView(columnIndex);

		TableColumn column = table.getColumnModel().getColumn(columnIndex);
		column.setCellRenderer(this);
		column.setMaxWidth(this.getPreferredSize().width);
		column.setResizable(false);
	}
}