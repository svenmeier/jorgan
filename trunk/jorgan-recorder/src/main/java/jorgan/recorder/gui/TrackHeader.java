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
package jorgan.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.recorder.Performance;
import jorgan.recorder.swing.IconToggle;
import jorgan.swing.BaseAction;
import bias.Configuration;

public class TrackHeader extends JPanel {

	private static final int MAX_WIDTH = 128;

	private static Configuration config = Configuration.getRoot().get(
			TrackHeader.class);

	private Performance performance;

	private int track;

	public TrackHeader(final Performance performance, final int track) {
		super(new BorderLayout());

		this.performance = performance;
		this.track = track;

		JLabel label = new JLabel();
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(new EmptyBorder(2, 2, 2, 2));
		Element element = performance.getElement(track);
		if (element == null) {
			config.get("none").read(label);
		} else {
			label.setText(Elements.getDisplayName(element));
		}
		label.setToolTipText(label.getText());
		add(label, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(buttonPanel, BorderLayout.CENTER);

		IconToggle playToggle = new IconToggle() {
			@Override
			protected boolean isOn() {
				return performance.getTracker(track).isPlayEnabled();
			}

			@Override
			protected void toggle() {
				if (performance.getState() == Performance.STATE_STOP) {
					performance.getTracker(track).setPlayEnabled(!isOn());
					super.toggle();
				}
			}
		};
		config.get("play").read(playToggle);
		buttonPanel.add(playToggle);

		IconToggle recordToggle = new IconToggle() {
			@Override
			protected boolean isOn() {
				return performance.getTracker(track).isRecordEnabled();
			}

			@Override
			protected void toggle() {
				if (performance.getState() == Performance.STATE_STOP) {
					performance.getTracker(track).setRecordEnabled(!isOn());
					super.toggle();
				}
			}
		};
		config.get("record").read(recordToggle);
		buttonPanel.add(recordToggle);

		final JPopupMenu menu = new JPopupMenu();
		menu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				init(menu);
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		label.setComponentPopupMenu(menu);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = Math.min(size.width, MAX_WIDTH);
		return size;
	}

	protected void init(JPopupMenu menu) {
		menu.removeAll();

		final JRadioButtonMenuItem noneItem = new JRadioButtonMenuItem();
		config.get("none").read(noneItem);
		noneItem.setSelected(performance.getElement(track) == null);
		noneItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (noneItem.isSelected()) {
					performance.setElement(track, null);
				}
			}
		});
		menu.add(noneItem);

		for (final Element element : performance.getTrackableElements()) {
			final JRadioButtonMenuItem item = new JRadioButtonMenuItem(Elements
					.getDisplayName(element));
			if (performance.getElement(track) == element) {
				item.setSelected(true);
			}
			item.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (item.isSelected()) {
						performance.setElement(track, element);
					}
				}
			});
			menu.add(item);
		}

		menu.addSeparator();

		menu.add(new AddAction());
		menu.add(new RemoveAction());

		menu.addSeparator();
		
		menu.add(new AutoAction());
	}

	private class RemoveAction extends BaseAction {
		public RemoveAction() {
			config.get("remove").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			performance.removeTrack(track);
		}
	}

	private class AddAction extends BaseAction {
		public AddAction() {
			config.get("add").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			performance.addTrack();
		}
	}
	
	private class AutoAction extends BaseAction {
		public AutoAction() {
			config.get("auto").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			performance.autoTracks();
		}
	}
}
