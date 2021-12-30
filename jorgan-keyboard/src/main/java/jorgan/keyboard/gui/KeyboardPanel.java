package jorgan.keyboard.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import jorgan.swing.MouseUtils;

/**
 * A keyboard.
 */
public class KeyboardPanel extends JComponent {

	public static int WHITE_HEIGHT = 48;

	public static int WHITE_WIDTH = 12;

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

	private List<BlackKey> blackKeys = new ArrayList<BlackKey>(128);

	private List<WhiteKey> whiteKeys = new ArrayList<WhiteKey>(128);

	private List<Key> keys = new ArrayList<Key>(128);

	private int from = 0;

	private int to = 127;

	/**
	 * Constructor.
	 */
	public KeyboardPanel() {
		MouseHandler handler = new MouseHandler();
		addMouseListener(handler);
		addMouseMotionListener(handler);

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

		setMinimumSize(new Dimension(0, WHITE_HEIGHT));
		setPreferredSize(new Dimension(x + 1, WHITE_HEIGHT));
	}

	public void reset(int from, int to) {
		this.from = from;
		this.to = to;

		for (Key key : keys) {
			key.reset();
		}

		repaint();
	}

	/**
	 * Paint the keys.
	 */
	@Override
	protected void paintComponent(Graphics g) {

		for (Key key : whiteKeys) {
			key.paint(g);
		}

		for (Key key : blackKeys) {
			key.paint(g);
		}
	}

	private class MouseHandler extends MouseInputAdapter {

		private Key key;

		private boolean wasPressed;

		@Override
		public void mousePressed(MouseEvent e) {
			if (MouseUtils.isHorizontalScroll(e)) {
				return;
			}

			Dimension preferredSize = getPreferredSize();

			int x = e.getX() - (getWidth() - preferredSize.width) / 2;
			int y = e.getY() - (getHeight() - preferredSize.height) / 2;

			setKey(getKey(x, y), y);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Dimension preferredSize = getPreferredSize();

			int x = e.getX() - (getWidth() - preferredSize.width) / 2;
			int y = e.getY() - (getHeight() - preferredSize.height) / 2;

			getKey(x, y);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isShiftDown() && !wasPressed) {
				key = null;
			} else {
				setKey(null, 0);
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
			}
		}
	}

	private Key getKey(int x, int y) {

		Key key = null;

		for (Key candidate : blackKeys) {
			if (candidate.hits(x, y)) {
				key = candidate;
				break;
			}
		}

		if (key == null) {
			for (Key candidate : whiteKeys) {
				if (candidate.hits(x, y)) {
					key = candidate;
					break;
				}
			}

		}

		if (key == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		return key;
	}

	private abstract class Key {
		protected int pitch;

		protected int x;

		protected int width;

		protected int height;

		protected boolean pressed;

		protected boolean confirmed;

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

			keys.add(this);
		}

		public void reset() {
			pressed = false;
			confirmed = false;

			repaint();
		}

		/**
		 * This key was pressed on the given y position.
		 * 
		 * @param y
		 *            y position of press
		 */
		public void press(int y) {
			if (!pressed) {
				int velocity = 127 * y / height;
				if (velocity < 0) {
					velocity = 0;
				}
				if (velocity > 127) {
					velocity = 127;
				}

				onKeyPress(pitch, velocity);

				pressed = true;
			}
		}

		/**
		 * This key was released.
		 */
		public void release() {
			if (pressed) {
				onKeyReleased(pitch);

				pressed = false;
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
			if (pitch < from || pitch > to) {
				return false;
			}

			if (x < this.x || x >= this.x + this.width) {
				return false;
			}

			if (this.height < KeyboardPanel.this.getHeight()) {
				return y < this.height;
			}

			return true;
		}

		/**
		 * Paint this key.
		 * 
		 * @param g
		 *            graphics to paint on
		 */
		public abstract void paint(Graphics g);

		public void confirm(boolean confirm) {
			this.confirmed = confirm;

			repaint(x, 0, width, height);
		}
	}

	private class BlackKey extends Key {

		private static final int WIDTH = 8;

		private static final int HEIGHT = 28;

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

			blackKeys.add(this);
		}

		@Override
		public void paint(Graphics g) {
			if (pitch < from || pitch > to) {
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, 0, WIDTH, HEIGHT);
			} else {
				g.setColor(Color.BLACK);
				g.fillRect(x, 0, WIDTH, HEIGHT);

				if (confirmed) {
					g.setColor(Color.GRAY);
					g.fillRect(x + 1, 1, WIDTH - 2, HEIGHT - 2);
				} else {
					g.setColor(Color.DARK_GRAY);
					g.fillRect(x + 1, 1, WIDTH - 2, HEIGHT - 5);
				}
			}
		}
	}

	private class WhiteKey extends Key {

		/**
		 * Create a white key for the given pitch at the given x position.
		 * 
		 * @param pitch
		 *            pitch
		 * @param x
		 *            x position
		 */
		public WhiteKey(int pitch, int x) {
			super(pitch, x, WHITE_WIDTH, WHITE_HEIGHT);

			whiteKeys.add(this);
		}

		@Override
		public void paint(Graphics g) {
			if (confirmed) {
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, 0, WHITE_WIDTH, WHITE_HEIGHT);
			} else {
				g.setColor(Color.WHITE);
				g.fillRect(x, 0, WHITE_WIDTH, WHITE_HEIGHT);

				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, 0, WHITE_WIDTH, 4);
				g.fillRect(x, WHITE_HEIGHT - 2, WHITE_WIDTH, 2);
			}

			if (pitch < from || pitch > to) {
				g.setColor(Color.LIGHT_GRAY);
			} else {
				g.setColor(Color.GRAY);
			}
			g.drawRect(x, 0, WHITE_WIDTH, WHITE_HEIGHT - 1);

			if (pitch == 60) {
				g.fillRect(x + WHITE_WIDTH / 2 - 2, WHITE_HEIGHT - 8, 4, 4);
			}
		}
	}

	private static int mod(int x, int y) {
		return x - ((x / y) * y);
	}

	protected void onKeyReleased(int pitch) {
	}

	protected void onKeyPress(int pitch, int velocity) {
	}

	public void confirmKeyPressed(int pitch) {
		keys.get(pitch).confirm(true);
	}

	public void confirmKeyReleased(int pitch) {
		keys.get(pitch).confirm(false);
	}
}