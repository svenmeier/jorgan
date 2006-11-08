package jorgan.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * A builder of a grid in a {@link java.awt.GridBagLayout}.
 */
public class GridBuilder {

	private GridBagConstraints defaults = new GridBagConstraints();

	private Constraints constraints = new Constraints();

	private double[] weightxs;

	private double weighty;

	private int row = -1;

	private int column = -1;

	/**
	 * Create a builder.
	 */
	public GridBuilder() {
		this(new double[0]);
	}

	/**
	 * Create a builder.
	 * 
	 * @param weightxs
	 *            the weights of the columns
	 */
	public GridBuilder(double[] weightxs) {
		this.weightxs = weightxs;

		defaults.gridwidth = 1;
		defaults.gridheight = 1;
		defaults.insets = new Insets(2, 2, 2, 2);
		defaults.fill = GridBagConstraints.NONE;
		defaults.anchor = GridBagConstraints.WEST;
		defaults.weighty = 0.0d;
		defaults.weightx = 0.0d;
	}

	/**
	 * Get the default constraints.
	 * 
	 * @return the default constraints
	 */
	public GridBagConstraints getDefaults() {
		return defaults;
	}

	/**
	 * Set the default constraints.
	 * 
	 * @param defaults
	 *            default constraints
	 */
	public void setDefaults(GridBagConstraints defaults) {
		this.defaults = defaults;
	}

	/**
	 * Begin the next row.
	 */
	public void nextRow() {
		nextRow(defaults.weighty);
	}

	/**
	 * Begin the next row.
	 * 
	 * @param weighty
	 *            the weight of the row, i.e. how much additional vertical space
	 *            this row should occupcy
	 */
	public void nextRow(double weighty) {
		column = -1;
		if (row == -1) {
			row = 0;
		} else {
			row += constraints.gridheight;
		}

		this.weighty = weighty;
	}

	/**
	 * Proceed to the next column.
	 * 
	 * @return the constraints for the next column
	 */
	public Constraints nextColumn() {
		if (column == -1) {
			column = 0;
		} else {
			column += constraints.gridwidth;
		}

		constraints.gridx = column;
		constraints.gridy = row;
		constraints.weightx = getWeightx(constraints.gridx);
		constraints.weighty = weighty;

		constraints.gridwidth = defaults.gridheight;
		constraints.gridheight = defaults.gridwidth;
		constraints.anchor = defaults.anchor;
		constraints.fill = defaults.fill;
		constraints.insets = defaults.insets;
		constraints.ipadx = defaults.ipadx;
		constraints.ipady = defaults.ipady;

		return constraints;
	}

	private double getWeightx(int column) {
		if (column < weightxs.length) {
			return weightxs[column];
		} else {
			return defaults.weightx;
		}
	}

	/**
	 * Convenience constraints subclass.
	 */
	public class Constraints extends GridBagConstraints {

		/**
		 * Set the width of the current cell.
		 * 
		 * @param gridwidth
		 *            the new gridwidth
		 * @return itself
		 */
		public Constraints gridWidth(int gridwidth) {
			constraints.gridwidth = gridwidth;
			return this;
		}

		/**
		 * Set the width of the current cell to all remaining columns.
		 * 
		 * @return itself
		 */
		public Constraints gridWidthRemainder() {
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			return this;
		}

		/**
		 * Set the height of the current cell.
		 * 
		 * @param gridheight
		 *            the new gridheight
		 * @return itself
		 */
		public Constraints gridHeight(int gridheight) {
			this.gridheight = gridheight;
			return this;
		}

		/**
		 * Fill horizontal.
		 * 
		 * @return itself
		 */
		public Constraints fillHorizontal() {
			this.fill = GridBagConstraints.HORIZONTAL;
			return this;
		}

		/**
		 * Fill vertical.
		 * 
		 * @return itself
		 */
		public Constraints fillVertical() {
			this.fill = GridBagConstraints.VERTICAL;
			return this;
		}

		/**
		 * Fill both, i.e. horizontal and vertical.
		 * 
		 * @return itself
		 */
		public Constraints fillBoth() {
			this.fill = GridBagConstraints.BOTH;
			return this;
		}

		/**
		 * Align west.
		 * 
		 * @return itself
		 */
		public Constraints alignWest() {
			this.anchor = GridBagConstraints.WEST;
			return this;
		}

		/**
		 * Align north west.
		 * 
		 * @return itself
		 */
		public Constraints alignNorthWest() {
			this.anchor = GridBagConstraints.NORTHWEST;
			return this;
		}

		/**
		 * Align north.
		 * 
		 * @return itself
		 */
		public Constraints alignNorth() {
			this.anchor = GridBagConstraints.NORTH;
			return this;
		}

		/**
		 * Align north east.
		 * 
		 * @return itself
		 */
		public Constraints alignNorthEast() {
			this.anchor = GridBagConstraints.NORTHEAST;
			return this;
		}

		/**
		 * Align east.
		 * 
		 * @return itself
		 */
		public Constraints alignEast() {

			this.anchor = GridBagConstraints.EAST;
			return this;
		}

		/**
		 * Align south east.
		 * 
		 * @return itself
		 */
		public Constraints alignSouthEast() {
			this.anchor = GridBagConstraints.SOUTHEAST;
			return this;
		}

		/**
		 * Align south.
		 * 
		 * @return itself
		 */
		public Constraints alignSouth() {
			this.anchor = GridBagConstraints.SOUTH;
			return this;
		}

		/**
		 * Align south west.
		 * 
		 * @return itself
		 */
		public Constraints alignSouthWest() {
			this.anchor = GridBagConstraints.SOUTHWEST;
			return this;
		}

		/**
		 * Pad the current cell.
		 * 
		 * @param ipadx
		 *            the internal x padding
		 * @return itself
		 */
		public Constraints padX(int ipadx) {
			this.ipadx = ipadx;
			return this;
		}

		/**
		 * Pad the current cell.
		 * 
		 * @param ipady
		 *            the internal y padding
		 * @return itself
		 */
		public Constraints padY(int ipady) {
			this.ipady = ipady;
			return this;
		}
	}
}