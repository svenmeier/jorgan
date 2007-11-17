/*
 * Created on 14.06.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jorgan.gui;

import javax.swing.table.DefaultTableCellRenderer;

import jorgan.disposition.Elements;

/**
 * A cell renderer for messages.
 */
public class MessageTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	protected void setValue(Object value) {
		if (value instanceof Class) {
			super.setValue(Elements.getDisplayName((Class<?>)value));
		} else {
			super.setValue(value);
		}
	}
}