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

import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.IndexedContinuous;
import jorgan.skin.ButtonLayer;
import jorgan.skin.Fill;
import jorgan.skin.Layer;
import jorgan.skin.TextLayer;
import jorgan.swing.list.FilterList;

/**
 * A view that shows an {@link IndexedContinuous}.
 */
public abstract class IndexedContinuousView<E extends IndexedContinuous>
		extends ContinuousView<E> implements TitledView {

	/**
	 * The key of the {@link IndexedContinuous#getIndex()} text for
	 * {@link TextLayer}s.
	 */
	public static final String BINDING_INDEX = "index";

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
	public void update(String name) {
		if ("index".equals(name)) {
			// index changes displayed title
			super.update();
		} else {
			super.update(name);
		}
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
				return getTitle(getElement().getIndex());
			}
		});
	}

	protected Layer createNumberLayer() {
		TextLayer layer = new TextLayer();
		layer.setBinding(BINDING_INDEX);
		layer.setPadding(new Insets(4 + getDefaultFont().getSize(), 4, 4, 4));
		layer.setFont(getDefaultFont());
		layer.setColor(getDefaultColor());

		return layer;
	}

	protected Layer createPressableLayer() {
		ButtonLayer layer = new ButtonLayer();
		layer.setBinding(BINDING_POPUP);
		layer.setFill(Fill.BOTH);
		layer.setPadding(new Insets(4, 4, 4, 4));

		return layer;
	}

	@Override
	protected JComponent createPopupContents() {

		FilterList<Integer> filterList = new FilterList<Integer>() {
			@Override
			protected List<Integer> getItems(String filter) {
				String title = filter.toLowerCase();
				int index = -1;
				try {
					index = Integer.parseInt(filter) - 1;
				} catch (NumberFormatException noIndex) {
				}

				List<Integer> items = new ArrayList<Integer>();
				for (int i = 0; i < getElement().getSize(); i++) {
					if (i == index || getTitle(i).toLowerCase().contains(title)) {
						items.add(i);
					}
				}
				return items;
			}

			@Override
			protected String toString(Integer item) {
				return (item.intValue() + 1) + " - " + getTitle(item);
			}

			@Override
			protected void onSelectedItem(Integer item) {
				getElement().setIndex(item.intValue());

				closePopup();
			}
		};
		filterList.setOpaque(true);

		if (getElement().getIndex() != -1) {
			filterList.setSelectedItem(getElement().getIndex());
		}

		return filterList;
	}

	protected abstract String getTitle(int index);
}