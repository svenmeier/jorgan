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
package jorgan.lcd.display;

import java.io.IOException;

import jorgan.disposition.Continuous;
import jorgan.lcd.lcdproc.HBarWidget;
import jorgan.lcd.lcdproc.Screen;
import jorgan.lcd.lcdproc.StringWidget;

/**
 * A displayer of a {@link Continuous}.
 */
public class ContinuousDisplayer extends ElementDisplayer<Continuous> {

	private StringWidget string;

	private HBarWidget bar;

	public ContinuousDisplayer(Screen screen, int row, Continuous element)
			throws IOException {
		super(element);

		string = new StringWidget(screen, 1, row);

		bar = new HBarWidget(screen, screen.size.width / 2, row,
				screen.size.width / 2);

		update();
	}

	@Override
	public void update() throws IOException {
		string.value(OrganDisplay.getName(getElement()));

		bar.value(getElement().getValue());
	}
}