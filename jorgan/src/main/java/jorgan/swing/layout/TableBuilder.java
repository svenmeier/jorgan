package jorgan.swing.layout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A builder of a table.
 */
public class TableBuilder {

	private JPanel panel;
	
	private int spacing = 2;

	private int row = -1;

	public TableBuilder(JPanel panel) {
		this.panel = panel;
		panel.setLayout(new GridBagLayout());
	}

	public Row row() {
		return new Row(false);
	}

	public Row row(boolean grow) {
		return new Row(grow);
	}

	public class Row {

		private int column = -1;

		private boolean grow;

		private Row(boolean grow) {
			row++;

			this.grow = grow;
		}

		public void data(JComponent component) {
			data(component, false);
		}

		public void data(JComponent component, boolean grow) {
			column++;

			GridBagConstraints constraints = new GridBagConstraints();

			constraints.gridx = column;
			constraints.gridy = row;
			constraints.gridwidth = 1;
			constraints.weightx = grow ? 1.0d : 0.0d;
			constraints.weighty = this.grow ? 1.0d : 0.0d;
			constraints.fill = this.grow ? GridBagConstraints.BOTH
					: GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets(row > 0 ? spacing : 0,
					column > 0 ? spacing : 0, 0, 0);

			panel.add(component, constraints);
		}
	}
}