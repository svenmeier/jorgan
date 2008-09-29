/*
 * Created on 14.06.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jorgan.gui;

import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.gui.dock.ElementsDockable;
import jorgan.gui.img.ElementIcons;
import jorgan.session.OrganSession;
import jorgan.swing.CompoundIcon;
import jorgan.swing.list.CommentedCellRenderer;

/**
 * A cell renderer for {@link Element}s.
 */
public class ElementListCellRenderer extends CommentedCellRenderer {

	private static Pattern repeatedWhitespace = Pattern.compile(" +");

	/**
	 * Icon used for indication of a warning.
	 */
	private static final Icon warningIcon = new ImageIcon(ElementsDockable.class
			.getResource("/jorgan/gui/img/elementWarning.gif"));

	/**
	 * Icon used for indication of an error.
	 */
	private static final Icon errorIcon = new ImageIcon(ElementsDockable.class
			.getResource("/jorgan/gui/img/elementError.gif"));

	/**
	 * Constructor.
	 */
	public ElementListCellRenderer() {
		setRenderer(new WrappedRenderer());
	}

	protected Element getElement(Object object) {
		return (Element) object;
	}

	@Override
	protected String getComment(Object value, int index, boolean isSelected) {
		Element element = getElement(value);

		String comment = element.getDescription();
		int newLine = comment.indexOf('\n');
		if (newLine != -1) {
			comment = comment.substring(0, newLine);
		}
		return comment;
	}

	protected OrganSession getOrgan() {
		return null;
	}

	protected Icon getIcon(Element element) {
		OrganSession session = getOrgan();

		Icon icon = ElementIcons.getIcon(element.getClass());
		if (session != null) {
			if (session.getProblems().hasErrors(element)) {
				return new CompoundIcon(icon, errorIcon);
			} else if (session.getProblems().hasWarnings(element)) {
				return new CompoundIcon(icon, warningIcon);
			}
		}
		return icon;
	}

	private class WrappedRenderer extends DefaultListCellRenderer {
		private StringBuffer text = new StringBuffer();

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			Element element = getElement(value);

			text.setLength(0);
			text.append(noRepeatedWhitespace(Elements.getDisplayName(element)));

			super.getListCellRendererComponent(list, text.toString(), index,
					isSelected, cellHasFocus);

			setIcon(ElementListCellRenderer.this.getIcon(element));

			return this;
		}
	}

	private static String noRepeatedWhitespace(String string) {
		return repeatedWhitespace.matcher(string).replaceAll(" ");
	}
}