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
package jorgan.time.timer;

import jorgan.disposition.Continuous;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganAdapter;
import jorgan.time.Clock;
import jorgan.time.Timer;
import jorgan.time.WakeUp;

/**
 * A timer for {@link Continuous#getDuration()}.
 */
public class ContinuousTimer implements Timer {

	private Organ organ;

	private Clock clock;

	public ContinuousTimer(Organ organ, Clock clock) {
		this.organ = organ;
		this.clock = clock;

		organ.addOrganListener(new OrganAdapter() {
			@Override
			public void propertyChanged(Element element, String name) {
				if (element instanceof Continuous && "value".equals(name)) {
					checkDurationExpired((Continuous) element);
				}
			}
		});
	}

	public void start() {
		for (Continuous element : organ.getElements(Continuous.class)) {
			checkDurationExpired(element);
		}
	}

	@Override
	public void stop() {
	}

	private void checkDurationExpired(final Continuous element) {
		int duration = element.getDuration();
		if (element.getValue() > 0f && duration > Continuous.DURATION_NONE) {
			clock.alarm(new DurationExpired(element), duration);
		}
	}

	private class DurationExpired implements WakeUp {

		private Continuous element;

		public DurationExpired(Continuous element) {
			this.element = element;
		}

		@Override
		public boolean replaces(WakeUp wakeUp) {
			if (wakeUp instanceof DurationExpired) {
				return this.element == ((DurationExpired) wakeUp).element;
			}
			return false;
		}

		@Override
		public void trigger() {
			element.setValue(0f);
		}
	}
}