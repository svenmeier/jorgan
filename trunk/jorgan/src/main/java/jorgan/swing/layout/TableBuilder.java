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

	private GridBagLayout layout;

	private JPanel panel;

	private int spacing = 4;

	private int row = 0;

	private GridBagConstraints constraints = new GridBagConstraints();

	public TableBuilder(JPanel panel) {
		this.layout = new GridBagLayout();
		this.panel = panel;
		panel.setLayout(layout);
		
		constraints.weightx = 0.0d;
		constraints.weighty = 0.0d;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);

	}

	public Row row() {
		Row row = new Row((GridBagConstraints)constraints.clone());
		
		this.row++;
		constraints.insets.top = spacing;
		
		return row;
	}

	public class Row {

		private GridBagConstraints constraints;

		private Row(GridBagConstraints constraints) {
			this.constraints = constraints;
			
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.gridwidth = 1;
		}

		public Row growVertical() {
			constraints.weighty = 1.0d;
			
			return this;
		}
		
		public Data data(JComponent component) {
			GridBagConstraints constraints = (GridBagConstraints) this.constraints
					.clone();

			Data data = new Data(component, constraints);

			nextData();

			return data;
		}

		private void nextData() {
			constraints.gridx++;
			constraints.insets.left = spacing;
		}

		public class Data {
			private JComponent component;
			private GridBagConstraints constraints;

			private Data(JComponent component, GridBagConstraints constraints) {
				this.component = component;
				this.constraints = constraints;
				
				panel.add(component, constraints);
			}

			public Data fillHorizontal() {
				this.constraints.fill = GridBagConstraints.HORIZONTAL;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Data fillVertical() {
				this.constraints.fill = GridBagConstraints.VERTICAL;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Data fillBoth() {
				this.constraints.fill = GridBagConstraints.BOTH;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Data growHorizontal() {
				this.constraints.weightx = 1.0f;

				layout.setConstraints(component, constraints);
				return this;
			}
		}
	}
}