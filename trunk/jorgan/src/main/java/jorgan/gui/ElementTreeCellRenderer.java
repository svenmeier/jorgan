/*
 * Created on 14.06.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jorgan.gui;

import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.gui.dock.ElementsDockable;
import jorgan.problem.ElementProblems;
import jorgan.session.OrganSession;
import jorgan.swing.CompoundIcon;
import jorgan.swing.tree.SimpleCellRenderer;

/**
 * A cell renderer for {@link Element}s.
 */
public class ElementTreeCellRenderer extends SimpleCellRenderer<Element> {

	private static Pattern repeatedWhitespace = Pattern.compile(" +");

	/**
	 * Icon used for indication of a warning.
	 */
	private static final Icon warningIcon = new ImageIcon(
			ElementsDockable.class
					.getResource("/jorgan/gui/img/elementWarning.gif"));

	/**
	 * Icon used for indication of an error.
	 */
	private static final Icon errorIcon = new ImageIcon(ElementsDockable.class
			.getResource("/jorgan/gui/img/elementError.gif"));

	/**
	 * Constructor.
	 */
	public ElementTreeCellRenderer() {
	}

	protected OrganSession getOrgan() {
		return null;
	}

	@Override
	protected void init(Element element) {
		OrganSession session = getOrgan();

		Icon icon = Elements.getIcon(element.getClass());
		if (session != null) {
			if (session.lookup(ElementProblems.class).hasErrors(element)) {
				icon = new CompoundIcon(icon, errorIcon);
			} else if (session.lookup(ElementProblems.class).hasWarnings(
					element)) {
				icon = new CompoundIcon(icon, warningIcon);
			}
		}

		setIcon(icon);
		setText(noRepeatedWhitespace(Elements.getDisplayName(element)));
	}

	private static String noRepeatedWhitespace(String string) {
		return repeatedWhitespace.matcher(string).replaceAll(" ");
	}
}