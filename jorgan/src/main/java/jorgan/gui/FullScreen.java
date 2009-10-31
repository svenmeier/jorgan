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

import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import jorgan.disposition.Console;
import jorgan.disposition.Elements;
import jorgan.gui.ConsolePanel.ConsoleStack;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.CardPanel;
import jorgan.swing.button.ButtonGroup;
import bias.Configuration;

/**
 * A window shown <em>full screen</em>.
 */
public class FullScreen extends Window implements ConsoleStack {

	private static Configuration config = Configuration.getRoot().get(
			FullScreen.class);

	/**
	 * The handler of mouse events.
	 */
	private MouseHandler mouseHandler = new MouseHandler();

	private JScrollPane scrollPane = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private CardPanel cardPanel = new CardPanel();

	private JPopupMenu popup = new JPopupMenu();

	private Map<Console, JCheckBoxMenuItem> menuItems = new HashMap<Console, JCheckBoxMenuItem>();

	private ButtonGroup group = new ButtonGroup();

	private OrganSession session;

	/**
	 */
	public FullScreen(OrganSession session, String screen) {
		super(null);

		if (session == null) {
			throw new IllegalArgumentException("session must not be null");
		}
		if (screen == null) {
			throw new IllegalArgumentException("screen must not be null");
		}

		GraphicsDevice device = getGraphicsDevice(screen);
		device.setFullScreenWindow(this);
		
		if (device.isDisplayChangeSupported()) {
			try {
				device.setDisplayMode(new DisplayMode(640, 480, 8, 60));
			} catch (RuntimeException ex) {
				device.setFullScreenWindow(null);
				throw ex;
			}
		}

		scrollPane.setBorder(null);
		add(scrollPane);
		scrollPane.setViewportView(cardPanel);

		popup.addSeparator();
		popup.add(new CloseAction());

		this.session = session;

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(processor);
	}

	@Override
	public synchronized void dispose() {
		super.dispose();

		session = null;

		menuItems.clear();

		for (Component component : cardPanel.getComponents()) {
			((ConsolePanel) component).dispose();
		}
		cardPanel.removeAll();

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(processor);
	}

	/**
	 * Add a console to be shown full screen.
	 * 
	 * @param console
	 *            console to add
	 */
	public void addConsole(final Console console) {

		ConsolePanel consolePanel = new ConsolePanel(session, console);

		consolePanel.addMouseListener(mouseHandler);
		consolePanel.addMouseMotionListener(mouseHandler);

		cardPanel.addCard(consolePanel, console);

		final JCheckBoxMenuItem check = new JCheckBoxMenuItem(Elements
				.getDisplayName(console));
		menuItems.put(console, check);
		check.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				if (check.isSelected()) {
					toFront(console);
				}
			}
		});
		group.add(check);
		popup.add(check, 0);
	}

	private GraphicsDevice getGraphicsDevice(String screen) {
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = environment.getScreenDevices();
		for (GraphicsDevice device : devices) {
			if (device.getIDstring().equals(screen)) {
				return device;
			}
		}

		throw new IllegalArgumentException("unkown device '" + screen + "'");
	}

	public void toFront(Console console) {
		JCheckBoxMenuItem menuItem = menuItems.get(console);
		if (menuItem != null) {
			menuItem.setSelected(true);

			cardPanel.selectCard(console);
		}
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

	protected void leave() {
	}

	private class CloseAction extends BaseAction {

		private CloseAction() {
			config.get("close").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			leave();
		}
	}

	private KeyEventDispatcher processor = new KeyEventDispatcher() {

		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED
					&& e.getKeyCode() == KeyEvent.VK_F11) {
				leave();
				return true;
			}

			return false;
		}
	};
}