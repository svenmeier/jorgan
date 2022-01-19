package jorgan.keyboard.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import jorgan.disposition.Elements;
import jorgan.disposition.Keyboard;
import jorgan.midi.mpl.ProcessingException;
import jorgan.play.KeyboardPlayer;
import jorgan.play.OrganPlay;
import jorgan.play.OrganPlay.Playing;
import jorgan.play.event.KeyListener;
import jorgan.swing.RowHeader;
import spin.Spin;

/**
 * A keyboard.
 */
public class KeyboardsPanel extends JPanel implements Scrollable {

	private RowHeader header = new RowHeader(this);

	private Listener listener = new Listener();

	private Map<Keyboard, KeyboardPanel> keyboardPanels = new HashMap<Keyboard, KeyboardPanel>();

	private OrganPlay play;

	public KeyboardsPanel(OrganPlay play) {
		super(new GridLayout(-1, 1));

		setBackground(Color.white);

		this.play = play;

		for (Keyboard keyboard : play.getOrgan().getElements(Keyboard.class)) {
			add(createKeyboardPanel(keyboard));

			JLabel label = new JLabel(Elements.getDescriptionName(keyboard));
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setBorder(new EmptyBorder(2, 2, 2, 2));
			header.add(label);
		}

		play.addKeyListener((KeyListener) Spin.over(listener));
	}

	public void destroy() {
		play.removeKeyListener((KeyListener) Spin.over(listener));
	}

	public void addNotify() {
		super.addNotify();

		header.configureEnclosingScrollPane();
	}

	public Dimension getPreferredScrollableViewportSize() {
		int width = getPreferredSize().width;
		int height = KeyboardPanel.WHITE_HEIGHT;

		return new Dimension(width, height);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		int increment;

		if (orientation == SwingConstants.HORIZONTAL) {
			increment = visibleRect.width;
		} else {
			increment = visibleRect.height;
		}

		return increment;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		int increment;

		if (orientation == SwingConstants.HORIZONTAL) {
			increment = 7 * KeyboardPanel.WHITE_WIDTH;
		} else {
			increment = getPreferredSize().height / getComponentCount();
		}

		return increment;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	private KeyboardPanel createKeyboardPanel(final Keyboard keyboard) {
		KeyboardPanel panel = new KeyboardPanel() {
			@Override
			protected void onKeyPress(final int pitch, final int velocity) {
				play(keyboard, new Playing<KeyboardPlayer>() {
					@Override
					public void play(KeyboardPlayer player) {
						player.press(pitch, velocity);
					}
				});
			}

			@Override
			protected void onKeyReleased(final int pitch) {
				play(keyboard, new Playing<KeyboardPlayer>() {
					@Override
					public void play(KeyboardPlayer player) {
						player.release(pitch);
					}
				});
			}
		};

		try {
			panel.reset(keyboard.getFrom(), keyboard.getTo());
		} catch (ProcessingException noRange) {
		}

		keyboardPanels.put(keyboard, panel);

		return panel;
	}

	protected void play(Keyboard keyboard, Playing playing) {
		play.play(keyboard, playing);
	}

	private class Listener implements KeyListener {
		@Override
		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
			keyboardPanels.get(keyboard).confirmKeyPressed(pitch);
		}

		@Override
		public void keyReleased(Keyboard keyboard, int pitch) {
			keyboardPanels.get(keyboard).confirmKeyReleased(pitch);
		}
	}
}