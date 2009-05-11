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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.sound.midi.MidiEvent;
import javax.swing.JComponent;

import jorgan.recorder.midi.Recorder;

public class TrackPanel extends JComponent {

	public static final int SECOND_WIDTH = 4;

	private Recorder recorder;

	private int track;

	public TrackPanel(Recorder recorder, int track) {
		this.recorder = recorder;
		this.track = track;

		setBackground(Color.white);
		setForeground(Color.blue.brighter());

		EventListener listener = new EventListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	@Override
	public Dimension getPreferredSize() {
		int height = getFont().getSize() * 4;

		int width = Math.round(SECOND_WIDTH * getDisplayTime()
				/ Recorder.SECOND);

		return new Dimension(width, height);
	}

	protected String getTitle() {
		return "Track " + track;
	}

	@Override
	public void paint(Graphics g) {
		int x = 0;
		int y = 0;
		int width = getWidth();
		int height = getHeight();

		g.setColor(getBackground());
		g.fillRect(x, y, width, height);

		Insets insets = getInsets();
		x += insets.left;
		y += insets.top;
		width -= insets.left + insets.right;
		height -= insets.top + insets.bottom;

		paintTitle(g, x, y, width, height);

		int titleHeight = getFont().getSize() + 1;

		paintTicks(g, x, y + titleHeight, width, height - titleHeight);

		paintMessages(g, x, y + titleHeight, width, height - titleHeight);

		paintCursor(g, x, y, width, height);
	}

	private void paintMessages(Graphics g, int x, int y, int width, int height) {
		g.setColor(getForeground());

		Insets insets = getInsets();

		int temp = -1;
		int count = 0;
		for (MidiEvent event : recorder.messagesForTrack(track)) {
			int nextX = millisToX(recorder.tickToMillis(event.getTick()),
					insets);
			if (nextX == temp) {
				count++;
				continue;
			}

			paintMessage(g, temp, y, width, height, count);
			temp = nextX;
			count = 1;
		}

		paintMessage(g, temp, y, width, height, count);
	}

	private void paintMessage(Graphics g, int x, int y, int width, int height,
			int count) {
		if (count > 0) {
			int temp = Math.round((1 - (1 / (float) (count + 1))) * height);
			g.drawLine(x, y + height - temp, x, y + height - 1);
		}
	}

	private void paintCursor(Graphics g, int x, int y, int width, int height) {
		Insets insets = getInsets();

		g.setColor(Color.red);
		long cursor = getCurrentTime();
		x += millisToX(cursor, insets);

		g.drawLine(x, y, x, y + height - 1);
		g.drawLine(x - 1, y, x - 1, y + height - 1);
	}

	private void paintTitle(Graphics g, int x, int y, int width, int height) {
		g.setFont(getFont());

		String title = getTitle();
		int titleWidth = g.getFontMetrics().stringWidth(title);
		int titleHeight = getFont().getSize();

		g.setColor(getBackground());
		g.fillRect(x + 1, y, titleWidth + 1, titleHeight + 1);

		g.setColor(getForeground());
		g.drawString(title, x + 1, y + titleHeight);
	}

	private void paintTicks(Graphics g, int x, int y, int width, int height) {
		Insets insets = getInsets();

		g.setColor(getBackground().darker());

		g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);

		long total = getDisplayTime();
		long delta = 10 * Recorder.SECOND;
		long millis = 0;
		while (millis < total) {
			int tempX = x + millisToX(millis, insets);
			int tempHeight;
			if (millis % (60 * Recorder.SECOND) == 0) {
				tempHeight = height;
			} else if (millis % (30 * Recorder.SECOND) == 0) {
				tempHeight = height * 3 / 4;
			} else {
				tempHeight = height / 2;
			}
			g.drawLine(tempX, y + height - tempHeight, tempX, y + height - 1);

			millis += delta;
		}
	}

	private long getCurrentTime() {
		return recorder.getTime();
	}

	private long getDisplayTime() {
		return Math.max(recorder.getTotalTime(), Recorder.MINUTE);
	}

	private int millisToX(long millis, Insets insets) {
		int width = getWidth() - insets.left - insets.right;

		return Math.round(millis * width / getDisplayTime());
	}

	private long xToMillis(int x, Insets insets) {
		int width = getWidth() - insets.left - insets.right;

		return Math.max(0, x * getDisplayTime() / width);
	}

	private class EventListener extends MouseAdapter {

		private Integer offset;

		private int getOffset(MouseEvent e) {
			return e.getX() - millisToX(getCurrentTime(), getInsets());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (this.offset == null) {
				int offset = getOffset(e);
				if (Math.abs(offset) < 4) {
					setCursor(Cursor
							.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				} else {
					setCursor(Cursor.getDefaultCursor());
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				return;
			}

			int offset = getOffset(e);
			if (Math.abs(offset) < 4) {
				this.offset = offset;
			} else {
				this.offset = 0;
				recorder.setTime(xToMillis(e.getX(), getInsets()));
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			offset = null;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (offset != null) {
				int x = e.getX() - offset;

				recorder.setTime(xToMillis(x, getInsets()));
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}
	}
}
