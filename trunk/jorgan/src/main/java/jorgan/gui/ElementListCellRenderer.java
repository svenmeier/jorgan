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
import javax.swing.JList;

import jorgan.disposition.Element;
import jorgan.docs.Documents;

public class ElementListCellRenderer extends DefaultListCellRenderer {

    private static Pattern repeatedWhitespace = Pattern.compile(" +");

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        Element element = getElement(value);

        super.getListCellRendererComponent(list, getText(element), index,
                isSelected, cellHasFocus);

        setToolTipText(getToolTipText(element));
        setIcon(getIcon(element));

        return this;
    }

    protected Element getElement(Object object) {
        return (Element) object;
    }

    protected String getText(Element element) {
        return noRepeatedWhitespace(Documents.getInstance().getDisplayName(
                element));
    }

    protected Icon getIcon(Element element) {
        return null;
    }

    protected String getToolTipText(Element element) {
        StringBuffer text = new StringBuffer();

        text.append(noRepeatedWhitespace(Documents.getInstance()
                .getDisplayName(element)));

        text.append(" (");
        text.append(Documents.getInstance().getDisplayName(element.getClass()));
        text.append(")");

        if (!"".equals(element.getDescription())) {
            text.append(" - ");
            text.append(element.getDescription());
        }

        return text.toString();
    }

    private static String noRepeatedWhitespace(String string) {
        return repeatedWhitespace.matcher(string).replaceAll(" ");
    }
}