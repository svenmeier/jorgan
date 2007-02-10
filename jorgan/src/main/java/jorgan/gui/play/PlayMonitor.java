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
package jorgan.gui.play;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import jorgan.util.I18N;

/**
 * Panel that displays input and output.
 */
public class PlayMonitor extends JPanel {

	private static I18N i18n = I18N.get(PlayMonitor.class);

	private static Icon noneIcon = new ImageIcon(PlayMonitor.class
			.getResource("/jorgan/gui/img/none.gif"));

	private static Icon inIcon = new ImageIcon(PlayMonitor.class
			.getResource("/jorgan/gui/img/in.gif"));

	private static Icon outIcon = new ImageIcon(PlayMonitor.class
			.getResource("/jorgan/gui/img/out.gif"));

	private JLabel inLabel = new JLabel();

	private JLabel outLabel = new JLabel();

	private Timer inTimer;

	private Timer outTimer;

	/**
	 * Constructor.
	 */
	public PlayMonitor() {
		setLayout(new GridLayout(0, 2));

		add(inLabel, null);

		inLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
		inLabel.setIcon(noneIcon);
		inLabel.setToolTipText(i18n.getString("inLabel.toolTipText"));
		inLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));

		outLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
		outLabel.setIcon(noneIcon);
		outLabel.setToolTipText(i18n.getString("outLabel.toolTipText"));
		add(outLabel, null);

		inTimer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				inLabel.setIcon(noneIcon);
			}
		});
		inTimer.setRepeats(false);

		outTimer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				outLabel.setIcon(noneIcon);
			}
		});
		outTimer.setRepeats(false);
	}

	/**
	 * Notify about input.
	 */
	public void input() {
		inLabel.setIcon(inIcon);
		inTimer.restart();
	}

	/**
	 * Notify about output.
	 */
	public void output() {
		outLabel.setIcon(outIcon);
		outTimer.restart();
	}
}