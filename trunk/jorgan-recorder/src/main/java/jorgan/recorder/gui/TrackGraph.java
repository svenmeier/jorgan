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
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.sound.midi.MidiEvent;
import javax.swing.JComponent;

import jorgan.recorder.SessionRecorder;
import jorgan.recorder.midi.Recorder;

public class TrackGraph extends JComponent {

	private static final int HEIGHT = 64;

	public static final int SECOND_WIDTH = 4;

	private SessionRecorder recorder;

	private int track;

	public TrackGraph(SessionRecorder recorder, int track) {
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
		int height = HEIGHT;

		int width = Math.round(SECOND_WIDTH * getDisplayTime()
				/ Recorder.SECOND);

		return new Dimension(width, height);
	}

	@Override
	public void paint(Graphics g) {
		Rectangle bounds = g.getClipBounds();

		g.setColor(getBackground());
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

		paintTicks(g, bounds);

		paintMessages(g, bounds);

		paintCursor(g, bounds);
	}

	private void paintMessages(Graphics g, Rectangle bounds) {
		g.setColor(getForeground());

		int height = getHeight();

		long tick = recorder.getRecorder().millisToTick(xToMillis(bounds.x));

		int x = -1;
		int count = 0;
		for (MidiEvent event : recorder.getRecorder().messagesForTrackFrom(
				track, tick)) {
			int nextX = millisToX(recorder.getRecorder().tickToMillis(
					event.getTick()));
			if (nextX == x) {
				count++;
				continue;
			}

			paintMessage(g, x, height, count);
			x = nextX;
			count = 1;

			if (x > bounds.x + bounds.width) {
				break;
			}
		}

		paintMessage(g, x, height, count);
	}

	private void paintMessage(Graphics g, int x, int height, int count) {
		if (count > 0) {
			int temp = Math.round((1 - (1 / (float) (count + 1))) * height);
			g.drawLine(x, height - temp, x, height - 1);
		}
	}

	private void paintCursor(Graphics g, Rectangle bounds) {
		g.setColor(Color.red);

		int x = millisToX(getCurrentTime());
		if (x <= 0) {
			x = 1;
		}
		if (x >= getWidth()) {
			x = getWidth() - 1;
		}

		g.drawLine(x, 0, x, getHeight() - 1);
		g.drawLine(x - 1, 0, x - 1, getHeight() - 1);
	}

	private void paintTicks(Graphics g, Rectangle bounds) {
		g.setColor(getBackground().darker());

		int height = getHeight();
		g.drawLine(0, height - 1, getWidth() - 1, height - 1);
		g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);

		long delta = 10 * Recorder.SECOND;
		long total = getDisplayTime();
		long millis = xToMillis(bounds.x) / delta * delta;
		while (millis < total) {
			int x = millisToX(millis);
			int tempHeight;
			if (millis == 0) {
				tempHeight = height * 4 / 4;
			} else if (millis % (60 * Recorder.SECOND) == 0) {
				tempHeight = height * 3 / 4;
			} else if (millis % (30 * Recorder.SECOND) == 0) {
				tempHeight = height * 2 / 4;
			} else {
				tempHeight = height * 1 / 4;
			}
			g.drawLine(x, height - tempHeight, x, height - 1);

			if (x > bounds.x + bounds.width) {
				break;
			}

			millis += delta;
		}
	}

	private long getCurrentTime() {
		return recorder.getRecorder().getTime();
	}

	private long getDisplayTime() {
		return Math.max(recorder.getRecorder().getTotalTime(), Recorder.MINUTE);
	}

	private int millisToX(long millis) {
		int width = getWidth();

		return Math.round(millis * width / getDisplayTime());
	}

	private long xToMillis(int x) {
		return Math.max(0, x * getDisplayTime() / getWidth());
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
					showCursor();
				} else {
					setCursor(Cursor.getDefaultCursor());
				}
			}
		}

		private void showCursor() {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
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
				recorder.getRecorder().setTime(xToMillis(e.getX()));
			}
			showCursor();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			offset = null;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (offset != null) {
				int x = e.getX() - offset;

				recorder.getRecorder().setTime(xToMillis(x));
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}
	}
}
