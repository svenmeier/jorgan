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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import jorgan.disposition.IndexedContinuous;
import jorgan.skin.TextLayer;

/**
 * A view that shows an {@link IndexedContinuous}.
 */
public class IndexedContinuousView<E extends IndexedContinuous> extends
		ContinuousView<E> {

	/**
	 * The key of the {@link IndexedContinuous#getIndex()} text for
	 * {@link TextLayer}s.
	 */
	public static final String BINDING_INDEX = "index";

	/**
	 * The key of the {@link IndexedContinuous#getTitle()} text for
	 * {@link TextLayer}s.
	 */
	public static final String BINDING_TITLE = "title";

	private NumberFormat indexFormat = new DecimalFormat("000");

	/**
	 * Constructor.
	 * 
	 * @param memory
	 *            memory to view
	 */
	public IndexedContinuousView(E element) {
		super(element);
	}

	@Override
	protected void initBindings() {
		super.initBindings();

		setBinding(BINDING_INDEX, new TextLayer.Binding() {
			public boolean isPressable() {
				return false;
			}

			public String getText() {
				return indexFormat.format(getElement().getIndex() + 1);
			}
		});

		setBinding(BINDING_TITLE, new TextLayer.Binding() {
			public boolean isPressable() {
				return false;
			}

			public String getText() {
				return getElement().getTitle();
			}
		});
	}
}