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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler.TransferSupport;

/**
 * Utility method for lists.
 */
public class ListUtils {

	/**
	 * Add a listener to actions to the given list, i.e. the given listener is
	 * notified if a cell is clicked. If the listener is an instance of
	 * {@link Action} it will be bound to {@link KeyEvent#VK_ENTER} too.
	 * 
	 * @param list
	 *            the list to add the listener to
	 * @param listener
	 *            the listener to add
	 */
	public static void addActionListener(final JList list,
			final int clickCount, final ActionListener listener) {
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == clickCount) {
					if (list.getSelectedIndex() != -1) {
						listener.actionPerformed(new ActionEvent(list,
								ActionEvent.ACTION_PERFORMED, null));
					}
				}
			}
		});

		if (listener instanceof Action) {
			list.getInputMap().put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), listener);
			list.getActionMap().put(listener, (Action) listener);
		}
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
			@Override
			public void mousePressed(MouseEvent e) {
				checkPopup(e);
			}

			@Override
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

	public static void addHoverSelection(final JList list) {
		list.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent event) {
				int index = list.locationToIndex(event.getPoint());
				if (index != -1) {
					list.setSelectedIndex(index);
				}
			}
		});
	}

	public static int importIndex(JList list, TransferSupport transferSupport) {
		int index = -1;

		int[] rows = list.getSelectedIndices();
		for (int row : rows) {
			index = Math.max(index, row + 1);
		}

		if (transferSupport.isDrop()) {
			JList.DropLocation location = (JList.DropLocation) transferSupport
					.getDropLocation();
			index = location.getIndex();
		}

		if (index == -1) {
			index = list.getModel().getSize();
		}

		return index;
	}
}