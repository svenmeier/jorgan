/*
 * Created on 14.06.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jorgan.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import jorgan.disposition.Element;
import jorgan.docs.Documents;

public class ElementListCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        Element element = getElement(value);

        super.getListCellRendererComponent(list, getNameAndType(element),
                index, isSelected, cellHasFocus);

        setIcon(getIcon(element));

        setToolTipText(getToolTipText(element));

        return this;
    }

    protected Element getElement(Object object) {
        return (Element) object;
    }

    private String getNameAndType(Element element) {

        String elementName = Documents.getInstance().getDisplayName(element);
        String typeName = Documents.getInstance().getDisplayName(
                element.getClass());

        return elementName + " - " + typeName;
    }

    protected Icon getIcon(Element element) {
        return null;
    }

    protected String getToolTipText(Element element) {
        return element.getDescription();
    }
}