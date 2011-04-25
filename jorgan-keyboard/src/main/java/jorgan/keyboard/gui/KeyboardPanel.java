package jorgan.keyboard.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import jorgan.swing.button.ButtonGroup;
import bias.Configuration;

/**
 * A keyboard.
 */
public class KeyboardPanel extends JComponent {

	private static Configuration config = Configuration.getRoot().get(
			KeyboardPanel.class);

	private static final int C = 0;

	private static final int CIS = 1;

	private static final int D = 2;

	private static final int DIS = 3;

	private static final int E = 4;

	private static final int F = 5;

	private static final int FIS = 6;

	private static final int G = 7;

	private static final int GIS = 8;

	private static final int A = 9;

	private static final int AIS = 10;

	private static final int B = 11;

	private JPopupMenu popupMenu;

	private JMenuItem[] channelMenuItems = new JCheckBoxMenuItem[16];

	private JMenuItem velocityMenuItem;

	private JMenuItem polyPressureMenuItem;

	private JMenuItem channelPressureMenuItem;

	private JMenuItem useNoteOffMenuItem;

	private int channel = 0;

	private boolean sendVelocity = true;

	private boolean sendPolyPressure = true;

	private boolean sendChannelPressure = false;

	private boolean useNoteOff = true;

	private List<Key> keys = new ArrayList<Key>();

	private Receiver receiver;

	/**
	 * Constructor.
	 */
	public KeyboardPanel() {
		MouseHandler handler = new MouseHandler();
		addMouseListener(handler);
		addMouseMotionListener(handler);

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		int x = 0;
		for (int pitch = 0; pitch < 128; pitch++) {
			switch (mod(pitch, 12)) {
			case C:
				x += new WhiteKey(pitch, x).width;
				break;
			case CIS:
				new BlackKey(pitch, x, 5f / 8);
				break;
			case D:
				x += new WhiteKey(pitch, x).width;
				break;
			case DIS:
				new BlackKey(pitch, x, 3f / 8);
				break;
			case E:
				x += new WhiteKey(pitch, x).width;
				break;
			case F:
				x += new WhiteKey(pitch, x).width;
				break;
			case FIS:
				new BlackKey(pitch, x, 6f / 8);
				break;
			case G:
				x += new WhiteKey(pitch, x).width;
				break;
			case GIS:
				new BlackKey(pitch, x, 4f / 8);
				break;
			case A:
				x += new WhiteKey(pitch, x).width;
				break;
			case AIS:
				new BlackKey(pitch, x, 2f / 8);
				break;
			case B:
				x += new WhiteKey(pitch, x).width;
				break;
			}
		}

		setMinimumSize(new Dimension(0, WhiteKey.HEIGHT));
		setPreferredSize(new Dimension(x, WhiteKey.HEIGHT));
	}

	/**
	 * Set a receiver.
	 * 
	 * @param receiver
	 *            receiver.
	 */
	public void setReceiver(Receiver receiver) {
		releaseKeys();

		this.receiver = receiver;
	}

	/**
	 * Clear all keys.
	 */
	public void releaseKeys() {
		for (int k = 0; k < 128; k++) {
			Key key = keys.get(k);
			key.release();
		}

		repaint();
	}

	/**
	 * Set the MIDI channel.
	 * 
	 * @param channel
	 *            channel to use
	 */
	public void setChannel(int channel) {
		releaseKeys();

		this.channel = channel;
	}

	/**
	 * Get the channel.
	 * 
	 * @return used channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * Should velocity be sent.
	 * 
	 * @param sendVelocity
	 *            <code>true</code> if velocity should be sent
	 */
	public void setSendVelocity(boolean sendVelocity) {
		this.sendVelocity = sendVelocity;
	}

	/**
	 * Is the velocity sent.
	 * 
	 * @return <code>true</code> if velocity is sent
	 */
	public boolean getSendVelocity() {
		return sendVelocity;
	}

	/**
	 * Should poly pressure be sent.
	 * 
	 * @param sendPolyPressure
	 *            <code>true</code> if poly pressure should be sent
	 */
	public void setSendPolyPressure(boolean sendPolyPressure) {
		this.sendPolyPressure = sendPolyPressure;
	}

	/**
	 * Is the poly pressure sent.
	 * 
	 * @return <code>true</code> if poly pressure is sent
	 */
	public boolean getSendPolyPressure() {
		return sendPolyPressure;
	}

	/**
	 * Should channel pressure be sent.
	 * 
	 * @param sendChannelPressure
	 *            <code>true</code> if aftertouch should be sent
	 */
	public void setSendAftertouch(boolean sendChannelPressure) {
		this.sendChannelPressure = sendChannelPressure;
	}

	/**
	 * Is the aftertouch sent.
	 * 
	 * @return <code>true</code> if aftertouch is sent
	 */
	public boolean getSendAftertouch() {
		return sendChannelPressure;
	}

	/**
	 * Paint the keys.
	 */
	@Override
	protected void paintComponent(Graphics g) {

		Dimension preferredSize = getPreferredSize();

		g.translate((getWidth() - preferredSize.width) / 2,
				(getHeight() - preferredSize.height) / 2);
		for (int k = 0; k < 128; k++) {
			Key key = keys.get(k);
			key.paint(g);
		}
	}

	private class MouseHandler extends MouseInputAdapter {

		private Key key;

		private boolean wasPressed;

		@Override
		public void mousePressed(MouseEvent e) {

			Dimension preferredSize = getPreferredSize();

			int x = e.getX() - (getWidth() - preferredSize.width) / 2;
			int y = e.getY() - (getHeight() - preferredSize.height) / 2;

			setKey(getKey(x, y), y);

			showPopup(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			Dimension preferredSize = getPreferredSize();

			int x = e.getX() - (getWidth() - preferredSize.width) / 2;
			int y = e.getY() - (getHeight() - preferredSize.height) / 2;

			setKey(getKey(x, y), y);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isShiftDown() && !wasPressed) {
				key = null;
			} else {
				setKey(null, 0);
			}

			showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				KeyboardPanel.this.showPopup(e.getX(), e.getY());
			}
		}

		private void setKey(Key key, int y) {
			if (this.key != key) {
				if (this.key != null) {
					this.key.release();
				}

				this.key = key;

				if (this.key != null) {
					wasPressed = this.key.pressed;
					this.key.press(y);
				}
			} else {
				if (this.key != null) {
					this.key.drag(y);
				}
			}
		}
	}

	private Key getKey(int x, int y) {

		for (int k = 128 - 1; k >= 0; k--) {
			Key key = keys.get(k);
			if (key.hits(x, y)) {
				return key;
			}
		}
		return null;
	}

	private void send(int command, int data1, int data2) {
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(command, channel, data1, data2);

			if (receiver != null) {
				receiver.send(message, -1);
			}
		} catch (InvalidMidiDataException ex) {
			throw new Error(ex);
		}
	}

	protected void showPopup(int x, int y) {
		if (popupMenu == null) {
			popupMenu = createPopup();
		}

		channelMenuItems[channel].setSelected(true);
		velocityMenuItem.setSelected(sendVelocity);
		polyPressureMenuItem.setSelected(sendPolyPressure);
		useNoteOffMenuItem.setSelected(useNoteOff);

		popupMenu.show(this, x, y);
	}

	protected JPopupMenu createPopup() {
		JPopupMenu popupMenu = new JPopupMenu();

		ButtonGroup channelGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				setChannel((Integer) button.getClientProperty(this));
			}
		};
		for (int c = 0; c < 16; c++) {
			channelMenuItems[c] = new JCheckBoxMenuItem();
			config.get("channel").read(channelMenuItems[c]);
			channelMenuItems[c].setText(channelMenuItems[c].getText() + " "
					+ (c + 1));
			channelMenuItems[c].putClientProperty(channelGroup, c);
			channelGroup.add(channelMenuItems[c]);
			popupMenu.add(channelMenuItems[c]);
		}

		popupMenu.addSeparator();

		velocityMenuItem = new JCheckBoxMenuItem();
		config.get("velocity").read(velocityMenuItem);
		velocityMenuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				sendVelocity = velocityMenuItem.isSelected();
			}
		});
		popupMenu.add(velocityMenuItem);

		polyPressureMenuItem = new JCheckBoxMenuItem();
		config.get("polyPressure").read(polyPressureMenuItem);
		polyPressureMenuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				sendPolyPressure = polyPressureMenuItem.isSelected();
			}
		});
		popupMenu.add(polyPressureMenuItem);

		channelPressureMenuItem = new JCheckBoxMenuItem();
		config.get("channelPressure").read(channelPressureMenuItem);
		channelPressureMenuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				sendChannelPressure = channelPressureMenuItem.isSelected();
			}
		});
		popupMenu.add(channelPressureMenuItem);

		useNoteOffMenuItem = new JCheckBoxMenuItem();
		config.get("useNoteOff").read(useNoteOffMenuItem);
		useNoteOffMenuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				useNoteOff = useNoteOffMenuItem.isSelected();
			}
		});
		popupMenu.add(useNoteOffMenuItem);

		return popupMenu;
	}

	private abstract class Key {
		protected int pitch;

		protected int x;

		protected int width;

		protected int height;

		protected boolean pressed;

		/**
		 * Create a key for the given pitch.
		 * 
		 * @param pitch
		 *            pitch of key
		 * @param x
		 *            x position
		 * @param width
		 *            width
		 * @param height
		 *            height
		 */
		public Key(int pitch, int x, int width, int height) {
			this.pitch = pitch;
			this.x = x;
			this.width = width;
			this.height = height;
		}

		/**
		 * This key was pressed on the given y position.
		 * 
		 * @param y
		 *            y position of press
		 */
		public void press(int y) {
			if (!pressed) {
				int velocity = 100;
				if (sendVelocity) {
					velocity = 127 * y / height;
				}
				send(ShortMessage.NOTE_ON, pitch, velocity);
				pressed = true;
				repaint();
			}
		}

		/**
		 * This key was dragged to the given y position.
		 * 
		 * @param y
		 *            y position of drag
		 */
		public void drag(int y) {
			if (pressed) {
				int pressure = 127 * y / height;
				if (sendPolyPressure) {
					send(ShortMessage.POLY_PRESSURE, pitch, pressure);
				}
				if (sendChannelPressure) {
					send(ShortMessage.CHANNEL_PRESSURE, pressure, 0);
				}
			}
		}

		/**
		 * This key was released.
		 */
		public void release() {
			if (pressed) {
				if (useNoteOff) {
					send(ShortMessage.NOTE_OFF, pitch, 0);
				} else {
					send(ShortMessage.NOTE_ON, pitch, 0);
				}
				pressed = false;
				repaint();
			}
		}

		/**
		 * Does this key contain the given position.
		 * 
		 * @param x
		 *            x position
		 * @param y
		 *            y position
		 * @return <code>true</code> if this key contains the given position
		 */
		public boolean hits(int x, int y) {
			return x >= this.x && x < this.x + this.width && y >= 0
					&& y < this.height;
		}

		/**
		 * Paint this key.
		 * 
		 * @param g
		 *            graphics to paint on
		 */
		public abstract void paint(Graphics g);
	}

	private class BlackKey extends Key {

		private static final int WIDTH = 8;

		private static final int HEIGHT = 32;

		/**
		 * Create a black key for the given pitch at the given x position.
		 * 
		 * @param pitch
		 *            pitch
		 * @param x
		 *            x position
		 */
		public BlackKey(int pitch, int x, float offset) {
			super(pitch, x - Math.round(WIDTH * offset), WIDTH, HEIGHT);

			keys.add(this);
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(x, 0, WIDTH, HEIGHT);

			if (pressed) {
				g.setColor(Color.GRAY);
				g.fillRect(x + 1, 1, WIDTH - 2, HEIGHT - 2);
			} else {
				g.setColor(Color.DARK_GRAY);
				g.fillRect(x + 1, 1, WIDTH - 2, HEIGHT - 5);
			}
		}
	}

	private class WhiteKey extends Key {

		private static final int WIDTH = 12;

		private static final int HEIGHT = 54;

		/**
		 * Create a white key for the given pitch at the given x position.
		 * 
		 * @param pitch
		 *            pitch
		 * @param x
		 *            x position
		 */
		public WhiteKey(int pitch, int x) {
			super(pitch, x, WIDTH, HEIGHT);

			keys.add(0, this);
		}

		@Override
		public void paint(Graphics g) {
			if (pressed) {
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, 0, WIDTH, HEIGHT);
			} else {
				g.setColor(Color.WHITE);
				g.fillRect(x, 0, WIDTH, HEIGHT);

				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, 0, WIDTH, 3);
			}
			g.setColor(Color.GRAY);
			g.drawRect(x, 0, WIDTH, HEIGHT - 1);

			if (pitch == 60) {
				g.setColor(Color.BLACK);
				g.fillRect(x + WIDTH / 2 - 2, HEIGHT - 8, 4, 4);
			}
		}
	}

	private static int mod(int x, int y) {
		return x - ((x / y) * y);
	}
}