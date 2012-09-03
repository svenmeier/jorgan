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

import jorgan.disposition.Elements;
import jorgan.disposition.Label;
import jorgan.lcd.lcdproc.Screen;
import jorgan.lcd.lcdproc.StringWidget;

/**
 * A display of a {@link Label}.
 */
public class LabelDisplayer extends ElementDisplayer<Label> {

	private StringWidget string;

	public LabelDisplayer(Screen screen, int row, Label element)
			throws IOException {
		super(element);

		string = new StringWidget(screen, 1, row);

		update();
	}

	@Override
	public void update() throws IOException {
		string.value(Elements.getDescriptionName(getElement()));
	}
}
