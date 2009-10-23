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
package jorgan.recorder.gui.console;

import jorgan.gui.console.TitledView;
import jorgan.gui.console.View;
import jorgan.recorder.disposition.Recorder;
import jorgan.session.OrganSession;
import jorgan.skin.TextLayer;

/**
 * A view that shows a {@link Recorder}.
 */
public class RecorderView extends View<Recorder> implements TitledView {

	/**
	 * Constructor.
	 * 
	 * @param session
	 * 
	 * @param memory
	 *            memory to view
	 */
	public RecorderView(OrganSession session, Recorder element) {
		super(element);
	}

	@Override
	protected void initBindings() {
		super.initBindings();

		setBinding(BINDING_TITLE, new TextLayer.Binding() {
			public boolean isPressable() {
				return false;
			}

			public String getText() {
				String performance = getElement().getPerformance();
				if (performance == null) {
					performance = "-";
				}
				return performance;
			}
		});
	}
}