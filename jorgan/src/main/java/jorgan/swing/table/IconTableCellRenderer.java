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

		this(null, defaultIcon);
	}

	/**
	 * Constructor. <br>
	 * The first icon in the map is used for all unknown values.
	 * 
	 * @param icons
	 *            a map defining the mapping from values to icons
	 */
	public IconTableCellRenderer(Map icons) {

		this(icons, (Icon) icons.values().iterator().next());
	}

	/**
	 * Constructor.
	 * 
	 * @param icons
	 *            a map defining the mapping from values to icons
	 * @param defaultIcon
	 *            icon to use for all unknown values
	 */
	public IconTableCellRenderer(Map icons, Icon defaultIcon) {

		this.icons = icons;
		this.defaultIcon = defaultIcon;
	}

	/**
	 * Overriden for icon specific behaviour.
	 */
	@Override
	protected void setValue(Object value) {

		Icon icon = defaultIcon;
		if (icons != null && value != null) {
			icon = (Icon) icons.get(value);
			if (icon == null) {
				icon = defaultIcon;
			}
		}
		setIcon(icon);
	}

	/**
	 * Convinience method for configuration of a JTable. <br>
	 * A renderer is created for the specified icon and set up as the renderer
	 * for specified column. For further details see
	 * {@link #configureTableColumn(JTable, int, IconTableCellRenderer)}.
	 * 
	 * @param table
	 *            table to configure
	 * @param columnIndex
	 *            index of column in model
	 * @param icon
	 *            icon
	 */
	public static void configureTableColumn(JTable table, int columnIndex,
			Icon icon) {
		configureTableColumn(table, columnIndex,
				new IconTableCellRenderer(icon));
	}

	/**
	 * Convenience method for configuration of a JTable. <br>
	 * A renderer is created for the specified icon and set up as the renderer
	 * for specified column. For further details see
	 * {@link #configureTableColumn(JTable, int, IconTableCellRenderer)}.
	 * 
	 * @param table
	 *            table to configure
	 * @param columnIndex
	 *            index of column in model
	 * @param icons
	 *            map for mapping of values to icons
	 */
	public static void configureTableColumn(JTable table, int columnIndex,
			Map icons) {
		configureTableColumn(table, columnIndex, new IconTableCellRenderer(
				icons));
	}

	/**
	 * Convenience method for configuration of a JTable. <br>
	 * A renderer is created for the specified icon and set up as the renderer
	 * for specified column. For further details see
	 * {@link #configureTableColumn(JTable, int, IconTableCellRenderer)}.
	 * 
	 * @param table
	 *            table to configure
	 * @param columnIndex
	 *            index of column in model
	 * @param icons
	 *            map for mapping of values to icons
	 * @param defaultIcon
	 *            icon to use as default
	 */
	public static void configureTableColumn(JTable table, int columnIndex,
			Map icons, Icon defaultIcon) {
		configureTableColumn(table, columnIndex, new IconTableCellRenderer(
				icons, defaultIcon));
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
	 * @param renderer
	 *            renderer to use in configuration
	 */
	public static void configureTableColumn(JTable table, int columnIndex,
			IconTableCellRenderer renderer) {

		Object value = renderer.defaultIcon;

		renderer
				.getTableCellRendererComponent(table, value, false, false, 0, 0);

		columnIndex = table.convertColumnIndexToView(columnIndex);

		TableColumn column = table.getColumnModel().getColumn(columnIndex);
		column.setCellRenderer(renderer);
		column.setMaxWidth(renderer.getPreferredSize().width);
		column.setResizable(false);
	}
}