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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.sound.midi.MidiEvent;
import javax.swing.JComponent;

import jorgan.recorder.midi.Recorder;

public class TrackPanel extends JComponent {

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
		int size = getFont().getSize();

		return new Dimension(size + 1, size + 1);
	}

	protected String getTitle() {
		return "Track " + track;
	}

	@Override
	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		paintBackground(g, width, height);

		paintTicks(g, width, height);

		paintTitle(g, width, height);

		paintMessages(g, width, height);

		paintCursor(g, width, height);
	}

	private void paintMessages(Graphics g, int width, int height) {
		g.setColor(getForeground());

		int x = -1;
		int count = 0;
		for (MidiEvent event : recorder.messagesForTrack(track)) {
			int nextX = millisToX(recorder.tickToMillis(event.getTick()));
			if (nextX == x) {
				count++;
				continue;
			}

			paintMessages(g, width, height, x, count);
			x = nextX;
			count = 1;
		}

		paintMessages(g, width, height, x, count);
	}

	private void paintMessages(Graphics g, int width, int height, int x,
			int count) {
		if (count > 0) {
			int temp = Math.round((1 - (1 / (float) (count + 1))) * height);
			g.drawLine(x, height - temp, x, height);
		}
	}

	private void paintCursor(Graphics g, int width, int height) {
		g.setColor(Color.red);
		long cursor = getCurrentTime();
		int x = millisToX(cursor);
		g.drawLine(x, 0, x, height);
		g.drawLine(x - 1, 0, x - 1, height);
	}

	private void paintTitle(Graphics g, int width, int height) {
		g.setFont(getFont());

		String title = getTitle();
		int titleWidth = g.getFontMetrics().stringWidth(title);
		int titleHeight = getFont().getSize();

		g.setColor(getBackground());
		g.fillRect(1, 0, titleWidth + 1, titleHeight + 1);

		g.setColor(getBackground().darker());
		g.drawString(title, 1, titleHeight);
	}

	private void paintTicks(Graphics g, int width, int height) {
		g.setColor(getBackground().darker());

		long total = getTotalTime();
		long millis = 0;
		long delta = 10 * Recorder.SECOND;
		if (millisToX(delta) < 10) {
			delta = Recorder.MINUTE;
		}
		while (millis < total) {
			int x = millisToX(millis);
			g.drawLine(x, 0, x, height);

			millis += delta;
		}
	}

	private void paintBackground(Graphics g, int width, int height) {
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);

		g.setColor(getBackground().darker());
		g.drawLine(0, height - 1, width, height - 1);
	}

	private long getCurrentTime() {
		return recorder.getTime();
	}

	private long getTotalTime() {
		return Math.max(recorder.getTotalTime(), Recorder.MINUTE);
	}

	private int millisToX(long millis) {
		return Math.round(millis * getWidth() / getTotalTime());
	}

	private long xToMillis(int x) {
		return Math.max(0, x * getTotalTime() / getWidth());
	}

	private class EventListener extends MouseAdapter {

		private Integer offset;

		private int getOffset(MouseEvent e) {
			return e.getX() - millisToX(getCurrentTime());
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
			int offset = getOffset(e);
			if (Math.abs(offset) < 4) {
				this.offset = offset;
			} else {
				this.offset = 0;
				recorder.setTime(xToMillis(e.getX()));
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

				recorder.setTime(xToMillis(x));
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}
	}
}
