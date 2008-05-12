package jorgan.swing;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A builder of a grid in a {@link java.awt.GridBagLayout}.
 */
public class GridBuilder {

	private List<Column> columns = new ArrayList<Column>();

	private int row = -1;

	private JPanel panel;

	public GridBuilder(JPanel panel) {
		this.panel = panel;
		panel.setLayout(new GridBagLayout());
	}

	public Column column() {
		Column column = new Column(columns.size());
		columns.add(column);
		return column;
	}

	public Row row() {
		row++;

		return new Row(row);
	}

	public void row(JComponent component) {
		Row row = row();

		row.cell().span().fillHorizontal().set(component);
	}

	public class Column {
		private double weight = 0.0d;

		private boolean fillx = false;

		private int gapx;

		private int anchorx = GridBagConstraints.WEST;

		private Column(int column) {
			if (column > 0) {
				gapx = 8;
			}
		}

		public Column grow() {
			weight = 1.0d;

			return this;
		}

		public Column fill() {
			fillx = true;

			return this;
		}

		public Column left() {
			anchorx = GridBagConstraints.WEST;

			return this;
		}

		public Column right() {
			anchorx = GridBagConstraints.EAST;

			return this;
		}
	}

	public class Row {
		private int row;

		private int column = -1;

		private double weighty = 0.0d;

		private boolean filly;

		private int gapy;

		private int anchory;

		private Row(int row) {
			this.row = row;

			if (row > 0) {
				gapy = 4;
			}
		}

		public Row grow() {
			this.weighty = 1.0d;

			return this;
		}

		public Row fill() {
			filly = true;

			return this;
		}

		public Row top() {
			anchory = GridBagConstraints.NORTH;

			return this;
		}

		public Row bottom() {
			anchory = GridBagConstraints.SOUTH;

			return this;
		}

		public Row skip() {
			cell().set(new JComponent() {
				@Override
				public void paint(Graphics g) {
				}
			});
			return this;
		}

		public Row cell(JComponent component) {
			cell().set(component);
			return this;
		}

		public Cell cell() {
			column++;
			return new Cell(column);
		}

		public class Cell {
			private int column;

			private int span;

			private int fill;

			private double weightx;

			private int gapx;

			private int anchor;

			public Cell(int column) {
				this.column = column;
				this.span = 1;

				Column c = columns.get(column);
				if (c.fillx && filly) {
					fill = GridBagConstraints.BOTH;
				} else if (c.fillx) {
					fill = GridBagConstraints.HORIZONTAL;
				} else if (filly) {
					fill = GridBagConstraints.VERTICAL;
				} else {
					fill = GridBagConstraints.NONE;
				}
				weightx = c.weight;
				gapx = c.gapx;
				// TODO take row#anchory into account
				anchor = c.anchorx;
			}

			public void set(JComponent component) {

				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = column;
				constraints.gridy = row;
				constraints.gridwidth = span;
				constraints.weightx = weightx;
				constraints.weighty = weighty;
				constraints.fill = fill;
				constraints.insets = new Insets(gapy, gapx, 0, 0);
				constraints.anchor = anchor;
				panel.add(component, constraints);
			}

			public Cell fillHorizontal() {
				fill = GridBagConstraints.HORIZONTAL;

				return this;
			}

			public Cell fillVertical() {
				fill = GridBagConstraints.VERTICAL;

				return this;
			}

			public Cell fillBoth() {
				fill = GridBagConstraints.BOTH;

				return this;
			}

			public Cell span() {
				this.span = GridBagConstraints.REMAINDER;

				return this;
			}
		}
	}
}