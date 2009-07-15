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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.sound.midi.MidiEvent;
import javax.swing.JComponent;

import jorgan.recorder.Performance;
import jorgan.recorder.midi.Recorder;

public class TrackGraph extends JComponent {

	private static final int HEIGHT = 48;

	public static final int SECOND_WIDTH = 4;

	private Performance performance;

	private int track;

	public TrackGraph(Performance performance, int track) {
		this.performance = performance;
		this.track = track;

		setBackground(Color.white);
		setForeground(Color.blue.brighter());
	}

	@Override
	public Dimension getPreferredSize() {
		int height = HEIGHT;

		int width = Math.round(SECOND_WIDTH * performance.getTotalTime()
				/ Recorder.SECOND);

		return new Dimension(Math.max(2, width), height);
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
		int width = getWidth();

		long tick = performance.millisToTick(xToMillis(bounds.x));

		int x = -1;
		int count = 0;
		for (MidiEvent event : performance.eventsFromTick(track,
				tick)) {
			int nextX = millisToX(performance.tickToMillis(
					event.getTick()));
			if (nextX >= width) {
				nextX = width - 1;
			}
			if (count == 0 || nextX == x) {
				x = nextX;
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
			int size = Math.round((1 - (1 / (float) (count + 1))) * height);

			g.drawLine(x, height - size, x, height - 1);
		}
	}

	private void paintCursor(Graphics g, Rectangle bounds) {
		g.setColor(Color.red);

		int x = millisToX(performance.getTime());
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
		long total = performance.getTotalTime();
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

	private int millisToX(long millis) {
		long displayTime = performance.getTotalTime();
		if (displayTime == 0) {
			return 0;
		}

		return Math.round(millis * getWidth() / displayTime);
	}

	private long xToMillis(int x) {
		return Math.max(0, x * performance.getTotalTime() / getWidth());
	}
}
