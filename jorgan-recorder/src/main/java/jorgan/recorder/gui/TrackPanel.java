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

import javax.swing.JComponent;

import jorgan.recorder.midi.Recorder;

public class TrackPanel extends JComponent {

	private Recorder recorder;

	private int track;

	public TrackPanel(Recorder recorder, int track) {
		this.recorder = recorder;
		this.track = track;

		setPreferredSize(new Dimension(32, 32));
		setBackground(Color.white);
		setForeground(Color.red);

		EventListener listener = new EventListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	@Override
	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);

		g.setColor(getBackground().darker());
		g.drawLine(0, height / 2, width, height / 2);

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

		g.setColor(getForeground());
		long current = getCurrentTime();
		int x = Math.min(width - 1, millisToX(current));
		g.drawLine(x, 0, x, height);
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
					setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
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
			}
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
			recorder.setTime(xToMillis(e.getX()));
		}
	}
}
