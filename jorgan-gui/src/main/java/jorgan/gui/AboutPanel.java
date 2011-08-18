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
package jorgan.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import jorgan.Info;
import jorgan.swing.StandardDialog;

/**
 * Panel that displays information about jOrgan.
 */
public class AboutPanel extends JPanel {

	/**
	 * Create an about panel.
	 */
	public AboutPanel() {
		setLayout(new BorderLayout());

		add(new JLabel(createIcon()));
	}

	private ImageIcon createIcon() {
		ImageIcon icon = new ImageIcon(getClass().getResource("img/about.gif"));

		int width = icon.getIconWidth();
		int height = icon.getIconHeight();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g = (Graphics2D) image.getGraphics();

		g.drawImage(icon.getImage(), 0, 0, this);

		addVersion(g, width, height);

		g.dispose();

		icon.setImage(image);

		return icon;
	}

	private void addVersion(Graphics2D g, int width, int height) {

		String version = new Info().getVersion();

		g.setFont(new Font("Sans Serif", Font.PLAIN, 16));
		g.setColor(Color.black);

		g.drawString("Version " + version, 5, 150);
	}

	/**
	 * Utility method to show an about panel in a dialog.
	 * 
	 * @param owner
	 *            owning frame
	 */
	public static void showInDialog(JFrame owner) {

		AboutPanel aboutPanel = new AboutPanel();

		StandardDialog dialog = new StandardDialog(owner);

		dialog.setBody(aboutPanel);
		dialog.setResizable(false);
		dialog.setBounds(null);
		dialog.setVisible(true);
		dialog.dispose();
	}

	private static JWindow splash;

	/**
	 * Utility method to show an about panel in a splash. <br>
	 * This method <strong>must not</strong> be called on the EDT.
	 * 
	 * @see #hideSplash()
	 */
	public static void showSplash() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					AboutPanel aboutPanel = new AboutPanel();

					splash = new JWindow();
					splash.setContentPane(aboutPanel);
					splash.pack();
					splash.setLocationRelativeTo(null);
					splash.setVisible(true);
				}
			});

			Thread.sleep(1000);
		} catch (Exception ignore) {
		}
	}

	/**
	 * Hide a previously shown splash.<br>
	 * This method <strong>must not</strong> be called on the EDT.
	 * 
	 * @see #showSplash()
	 */
	public static void hideSplash() {
		if (splash != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					splash.dispose();
					splash = null;
				}
			});
		}
	}
}