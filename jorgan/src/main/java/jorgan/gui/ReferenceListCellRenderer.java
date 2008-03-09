/*
 * Created on 14.06.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jorgan.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import jorgan.disposition.Element;
import jorgan.disposition.Reference;

/**
 * A cell renderer for {@link Reference}s.
 */
public class ReferenceListCellRenderer extends ElementListCellRenderer {

	/**
	 * Icon used for indication an reference.
	 */
	private static final Icon referenceIcon = new ImageIcon(
			ReferenceListCellRenderer.class
					.getResource("/jorgan/gui/img/reference.gif"));

	protected Icon getIcon(Element element) {
		return referenceIcon;
	}
}