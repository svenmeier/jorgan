package jorgan.swing.layout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * A builder of a definition list.
 */
public class DefinitionBuilder {

	private GridBagLayout layout;

	private JPanel panel;

	private final int rowSpacing = 2;

	private final int columnSpacing = 20;
	
	private int gridY;
	
	private int columnIndex = 0;

	public DefinitionBuilder(JPanel panel) {
		this(panel, 0);
	}

	public DefinitionBuilder(JPanel panel, int gridY) {
		this.layout = new GridBagLayout();
		this.panel = panel;
		this.gridY = gridY;
		
		panel.setLayout(layout);
	}

	public Column column() {
		Column column = new Column(this.columnIndex);
		this.columnIndex++;
		return column;
	}

	public class Column {

		private GridBagConstraints constraints = new GridBagConstraints();

		private int columnIndex;
		
		private boolean term;

		private Column(int columnIndex) {
			this.columnIndex = columnIndex;
			
			constraints.gridy = gridY;
			constraints.insets = new Insets(0, 2, 0, 2);
		}

		private void nextRow(int height) {
			term = false;
			constraints.gridy += height;
			constraints.insets.top = rowSpacing;
		}
		
		public void skip() {
			nextRow(1);
		}

		public void group(JComponent component) {
			if (term) {
				nextRow(1);
			}

			constraints.gridx = columnIndex * 2;
			constraints.gridwidth = 2;
			constraints.gridheight = 1;
			constraints.weightx = 1.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			if (columnIndex > 0) {
				constraints.insets.left = columnSpacing;
			}
			panel.add(new Group(component), constraints);

			nextRow(1);
		}

		public Space box(JComponent component) {
			if (term) {
				nextRow(1);
			}

			constraints.gridx = columnIndex * 2;
			constraints.gridwidth = 2;
			constraints.gridheight = 1;
			constraints.weightx = 1.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.BOTH;
			if (columnIndex > 0) {
				constraints.insets.left = columnSpacing;
			}
			Space space = new Space(component, constraints);

			nextRow(1);
			
			return space;
		}

		public Space term(JComponent component) {
			if (term) {
				nextRow(1);
			}

			constraints.gridx = columnIndex * 2;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.EAST;
			if (columnIndex > 0) {
				constraints.insets.left = columnSpacing;
			}

			return new Space(component, constraints);
		}

		public Space definition(JComponent component) {
			return definition(component, 1);
		}

		public Space definition(JComponent component, int height) {
			GridBagConstraints constraints = (GridBagConstraints) this.constraints.clone();
			constraints.gridx = columnIndex * 2 + 1;
			constraints.gridwidth = 1;
			constraints.gridheight = height;
			constraints.weightx = 1.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets.left = 2;

			Space space = new Space(component, constraints);

			nextRow(height);

			return space;
		}

		public class Space {
			private JComponent component;
			private GridBagConstraints constraints;

			private Space(JComponent component,
					GridBagConstraints constraints) {
				this.component = component;
				this.constraints = constraints;

				panel.add(component, constraints);
			}

			public Space fillHorizontal() {
				this.constraints.fill = GridBagConstraints.HORIZONTAL;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Space fillVertical() {
				this.constraints.fill = GridBagConstraints.VERTICAL;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Space fillBoth() {
				this.constraints.fill = GridBagConstraints.BOTH;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Space growVertical() {
				this.constraints.weighty = 1.0f;

				layout.setConstraints(component, constraints);
				return this;
			}
		}
	}
}