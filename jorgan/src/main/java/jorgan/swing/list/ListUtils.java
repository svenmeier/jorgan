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
package jorgan.swing.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;

/**
 * Utility method for lists.
 */
public class ListUtils {

	/**
	 * Add a listener to actions to the given list, i.e. the given listener is
	 * notified if a cell is double clicked.
	 * 
	 * @param list
	 *            the list to add the listener to
	 * @param listener
	 *            the listener to add
	 */
	public static void addActionListener(final JList list,
			final ActionListener listener) {
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (list.getSelectedIndex() != -1) {
						listener.actionPerformed(new ActionEvent(list,
								ActionEvent.ACTION_PERFORMED, null));
					}
				}
			}
		});
	}

	/**
	 * Add a popup to the given list.
	 * 
	 * @param list
	 *            list to add popup to
	 * @param popup
	 *            the popup to add
	 */
	public static void addPopup(final JList list, final JPopupMenu popup) {
		list.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				checkPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				checkPopup(e);
			}

			public void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int index = list.locationToIndex(e.getPoint());
					if (index != -1) {
						list.setSelectedIndex(index);
						popup.show(list, e.getX(), e.getY());
					}
				}
			}
		});
	}
}