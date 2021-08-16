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
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import bias.Configuration;
import jorgan.disposition.Console;
import jorgan.disposition.Elements;
import jorgan.gui.console.ConsoleStack;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.CardPanel;
import jorgan.swing.button.ButtonGroup;
import jorgan.util.NativeUtils;

/**
 * A window shown <em>full screen</em>.
 */
public class FullScreen extends JDialog implements ConsoleStack {

	public static final KeyStroke KEY_STROKE;
	static {
		if (NativeUtils.isMac()) {
			KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F,
					KeyEvent.META_MASK | KeyEvent.SHIFT_MASK);
		} else {
			KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
		}
	}

	private static Configuration config = Configuration.getRoot()
			.get(FullScreen.class);

	/**
	 * The handler of events.
	 */
	private EventHandler eventHandler = new EventHandler();

	private JScrollPane scrollPane = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private CardPanel cardPanel = new CardPanel();

	private JPopupMenu popup = new JPopupMenu();

	private Map<Console, JCheckBoxMenuItem> menuItems = new HashMap<Console, JCheckBoxMenuItem>();

	private ButtonGroup group = new ButtonGroup();

	private boolean autoScroll = false;

	private OrganSession session;

	/**
	 */
	public FullScreen(OrganSession session,
			GraphicsConfiguration configuration) {
		super((JDialog) null, configuration.getDevice().getIDstring(), false,
				configuration);

		config.read(this);

		setUndecorated(true);

		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setOpaque(true);
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setViewportView(cardPanel);
		add(scrollPane);

		popup.add(new CloseAction());
		popup.addSeparator();

		this.session = session;

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(processor);
	}

	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}

	@Override
	public synchronized void dispose() {
		super.dispose();

		session = null;

		eventHandler.stop();

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

		consolePanel.addMouseListener(eventHandler);
		if (autoScroll) {
			consolePanel.addMouseMotionListener(eventHandler);
		}

		cardPanel.addCard(consolePanel, console);

		final JCheckBoxMenuItem check = new JCheckBoxMenuItem(
				Elements.getDisplayName(console));
		menuItems.put(console, check);
		check.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (check.isSelected()) {
					toFront(console);
				}
			}
		});
		group.add(check);
		popup.add(check);
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
	private class EventHandler extends MouseInputAdapter
			implements ActionListener {

		private Timer timer;

		private int deltaX;

		private int deltaY;

		private EventHandler() {
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
			// event might be notified while full screen is
			// no longer showing,
			// resulting in IllegalComponentStateException
			// when showing the popup :(
			if (e.getComponent().isShowing() && e.isPopupTrigger()) {
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

			deltaX = (int) (Math.pow((double) x / (rect.width / 2), 5)
					* (rect.width / 5));
			deltaY = (int) (Math.pow((double) y / (rect.height / 2), 5)
					* (rect.height / 5));

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
			Rectangle rect = scrollPane.getViewport().getViewRect();
			Dimension size = scrollPane.getViewport().getView().getSize();

			int x = Math.min(size.width - rect.width,
					Math.max(rect.x + deltaX, 0));
			int y = Math.min(size.height - rect.height,
					Math.max(rect.y + deltaY, 0));

			scrollPane.getViewport().setViewPosition(new Point(x, y));
		}

		public void stop() {
			if (timer.isRunning()) {
				timer.stop();
			}
		}
	}

	private void leave() {
		setVisible(false);
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
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if (e.getKeyCode() == KEY_STROKE.getKeyCode() && e
						.getModifiers() == (KEY_STROKE.getModifiers() & 0xf)) {
					leave();
					return true;
				}
			}

			return false;
		}
	};

	private static GraphicsDevice getGraphicsDevice(String screen) {
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

	public static FullScreen create(OrganSession session, String screen,
			boolean real) throws IllegalArgumentException {

		GraphicsDevice device = getGraphicsDevice(screen);
		GraphicsConfiguration configuration = device.getDefaultConfiguration();

		FullScreen fullScreen = new FullScreen(session, configuration);
		if (real) {
			device.setFullScreenWindow(fullScreen);
		} else {
			fullScreen.setBounds(device.getDefaultConfiguration().getBounds());
			fullScreen.setVisible(true);
		}
		return fullScreen;
	}

	public static String[] getIDs() {
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = environment.getScreenDevices();

		String[] ids = new String[devices.length];
		for (int d = 0; d < devices.length; d++) {
			ids[d] = devices[d].getIDstring();
		}

		return ids;
	}
}