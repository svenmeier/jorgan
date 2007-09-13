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
package jorgan.gui.console;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import jorgan.disposition.Momentary;
import jorgan.disposition.Shortcut;

/**
 * A view for a {@link Momentary}.
 */
public abstract class MomentaryView<E extends Momentary> extends View<E> {

	/**
	 * Constructor.
	 * 
	 * @param momentary	the momentary to view
	 */
	public MomentaryView(E momentary) {
		super(momentary);
	}

	@Override
	public void keyPressed(KeyEvent ev) {

		Momentary momentary = getElement();

		Shortcut shortcut = momentary.getShortcut();
		if (shortcut != null && shortcut.match(ev)) {
			shortcutPressed();
		}
	}

	@Override
	public void keyReleased(KeyEvent ev) {

		Momentary momentary = getElement();

		Shortcut shortcut = momentary.getShortcut();
		if (shortcut != null && shortcut.match(ev)) {
			shortcutReleased();
		}
	}

	protected void shortcutPressed() {
	}

	protected void shortcutReleased() {
	}

	/**
	 * Is the {@link jorgan.skin.ButtonLayer} currently pressed.
	 * 
	 * @return 	pressed
	 */
	public abstract boolean isButtonPressed();

	/**
	 * The {@link jorgan.skin.ButtonLayer} was pressed.
	 */
	public abstract void buttonPressed();

	/**
	 * The {@link jorgan.skin.ButtonLayer} was released.
	 */
	public abstract void buttonReleased();

	@Override
	public void paint(Graphics2D g) {
		super.paint(g);

		paintShortcut(g);
	}

	protected void paintShortcut(Graphics2D g) {
		if (isShowShortcut()) {
			Shortcut shortcut = getElement().getShortcut();
			if (shortcut != null) {
				g.setFont(getShortcutFont());
				g.setColor(getShortcutColor());

				g.drawString(shortcut.toString(), getX(), getY() + getHeight());
			}
		}
	}
}