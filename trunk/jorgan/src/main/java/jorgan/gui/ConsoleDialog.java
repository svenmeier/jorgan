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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import jorgan.disposition.Console;
import jorgan.disposition.Elements;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.CardPanel;
import jorgan.swing.button.ButtonGroup;
import bias.Configuration;

/**
 * JDialog subclass to show a console <em>full screen</em>.
 */
public class ConsoleDialog extends JDialog {

	private static Configuration config = Configuration.getRoot().get(
			ConsoleDialog.class);

	/**
	 * The handler of mouse events.
	 */
	private MouseHandler mouseHandler = new MouseHandler();

	private JScrollPane scrollPane = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private CardPanel cardPanel = new CardPanel();

	private JPopupMenu popup = new JPopupMenu();

	private ButtonGroup group = new ButtonGroup() {
		protected void onSelected(AbstractButton button) {
			cardPanel.selectCard(button.getClientProperty(this));
		}
	};

	private OrganSession session;

	/**
	 * Create a dialog.
	 * 
	 * @param owner
	 *            the owner
	 * @param configuration
	 *            the graphics configuration
	 */
	public ConsoleDialog(JFrame owner, GraphicsConfiguration configuration) {
		super(owner, null, false, configuration);

		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Rectangle bounds = getGraphicsConfiguration().getBounds();
		setBounds(bounds);

		scrollPane.setBorder(null);
		getContentPane().add(scrollPane);
		scrollPane.setViewportView(cardPanel);

		popup.addSeparator();
		popup.add(new CloseAction());
	}

	/**
	 * Set the organ session.
	 * 
	 * @param session
	 *            organ session
	 */
	public void setOrgan(OrganSession session) {
		this.session = session;
	}

	/**
	 * Add a console to be shown on this dialog.
	 * 
	 * @param console
	 *            console to add
	 */
	public void addConsole(final Console console) {

		ConsolePanel consolePanel = new ConsolePanel();
		consolePanel.setOrgan(session);
		consolePanel.setConsole(console);

		consolePanel.addMouseListener(mouseHandler);
		consolePanel.addMouseMotionListener(mouseHandler);

		cardPanel.addCard(consolePanel, console);

		JCheckBoxMenuItem check = new JCheckBoxMenuItem(Elements
				.getDisplayName(console));
		check.putClientProperty(group, console);
		group.add(check);
		popup.add(check, 0);
	}

	/**
	 * The handler for mouse events.
	 */
	private class MouseHandler extends MouseInputAdapter implements
			ActionListener {

		private Timer timer;

		private int deltaX;

		private int deltaY;

		private MouseHandler() {
			timer = new Timer(50, this);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			checkPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			checkPopup(e);
		}

		protected void checkPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (timer.isRunning()) {
				timer.stop();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			if (popup.isVisible()) {
				return;
			}

			Rectangle rect = scrollPane.getViewport().getViewRect();

			int x = e.getX() - (rect.x + rect.width / 2);
			int y = e.getY() - (rect.y + rect.height / 2);

			deltaX = (int) (Math.pow((double) x / (rect.width / 2), 5) * (rect.width / 5));
			deltaY = (int) (Math.pow((double) y / (rect.height / 2), 5) * (rect.height / 5));

			if (deltaX != 0 || deltaY != 0) {
				if (!timer.isRunning()) {
					timer.start();
				}
			} else {
				if (timer.isRunning()) {
					timer.stop();
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			// must change horizontal and vertical value separately
			// or otherwise scrollpane will not use blitting :(
			scrollPane.getHorizontalScrollBar().setValue(
					scrollPane.getHorizontalScrollBar().getValue() + deltaX);
			scrollPane.getVerticalScrollBar().setValue(
					scrollPane.getVerticalScrollBar().getValue() + deltaY);
		}
	}

	private class CloseAction extends BaseAction {

		private CloseAction() {
			config.get("close").read(this);

			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), this);
			getRootPane().getActionMap().put(this, this);
		}

		public void actionPerformed(ActionEvent ev) {
			ConsoleDialog.this.setVisible(false);
		}
	}

	/**
	 * Create a dialog for the given owner and screen.
	 * 
	 * @param owner
	 *            owner
	 * @param screen
	 *            screen
	 * @return created dialog
	 */
	public static ConsoleDialog create(JFrame owner, String screen) {

		if (screen == null) {
			throw new IllegalArgumentException("screen must not be null");
		}

		GraphicsConfiguration configuration = null;
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = environment.getScreenDevices();
		for (GraphicsDevice device : devices) {
			if (device.getIDstring().equals(screen)) {
				configuration = device.getDefaultConfiguration();
			}
		}
		if (configuration == null) {
			environment.getDefaultScreenDevice().getDefaultConfiguration();
		}

		ConsoleDialog dialog = new ConsoleDialog(owner, configuration);

		return dialog;
	}
}