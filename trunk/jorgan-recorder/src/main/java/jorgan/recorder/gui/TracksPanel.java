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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import jorgan.recorder.Performance;
import jorgan.recorder.PerformanceListener;
import spin.Spin;

public class TracksPanel extends JPanel implements Scrollable {

	private Performance performance;

	private HeaderPanel headerPanel = new HeaderPanel();

	private EventListener listener = new EventListener();

	public TracksPanel(Performance performance) {
		super(new GridLayout(-1, 1));

		setBackground(Color.white);

		this.performance = performance;
		performance.addListener((PerformanceListener) Spin.over(listener));

		addMouseListener(listener);
		addMouseMotionListener(listener);

		initTracks();
	}

	public void destroy() {
		performance.removeListener((PerformanceListener) Spin.over(listener));
	}

	private void initTracks() {
		removeAll();
		headerPanel.removeAll();

		if (performance.isLoaded()) {
			for (int track = 0; track < performance.getTrackerCount(); track++) {
				add(new TrackGraph(performance, track));
				headerPanel.add(new TrackHeader(performance, track));
			}
		}

		revalidate();
		repaint();
		headerPanel.revalidate();
		headerPanel.repaint();
	}

	public Dimension getPreferredScrollableViewportSize() {
		int width = 60 * TrackGraph.SECOND_WIDTH;
		int height = getPreferredSize().height;

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
			increment = 10 * TrackGraph.SECOND_WIDTH;
		} else {
			increment = getPreferredSize().height
					/ performance.getTrackerCount();
		}

		return increment;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public void updateTime() {
		repaint();
		revalidate();

		int x = millisToX(performance.getTime());
		scrollRectToVisible(new Rectangle(x, 0, 2, getHeight()));
	}

	public JComponent getHeader() {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(headerPanel, BorderLayout.NORTH);

		return wrapper;
	}

	private class HeaderPanel extends JPanel {

		public HeaderPanel() {
			super(new GridLayout(-1, 1));

			setBackground(Color.white);
		}

		@Override
		public Dimension getPreferredSize() {
			int width = super.getPreferredSize().width;
			int height = TracksPanel.this.getPreferredSize().height;

			return new Dimension(width, height);
		}
	}

	private class EventListener extends MouseAdapter implements
			MouseMotionListener, PerformanceListener {

		private Integer offset;

		private int getOffset(MouseEvent e) {
			return e.getX() - millisToX(performance.getTime());
		}

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
				performance.setTime(xToMillis(e.getX()));
			}
			showCursor();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			offset = null;
		}

		public void mouseDragged(MouseEvent e) {
			if (offset != null) {
				int x = e.getX() - offset;

				performance.setTime(xToMillis(x));
			}
		}

		public void timeChanged(long millis) {
		}

		public void changed() {
			initTracks();
		}

		public void stateChanged(int state) {
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
