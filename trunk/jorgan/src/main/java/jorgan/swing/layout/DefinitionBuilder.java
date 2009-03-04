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
	
	private int column = 0;

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
		Column column = new Column();
		this.column++;
		return column;
	}

	public class Column {

		private GridBagConstraints constraints = new GridBagConstraints();

		private boolean term;

		private Column() {
			constraints.gridx = column * 2;
			constraints.gridy = gridY;
			constraints.insets = new Insets(0, 0, 0, 0);
			if (column > 0) {
				constraints.insets.left = columnSpacing;
			}
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

			constraints.gridwidth = 2;
			constraints.gridheight = 1;
			constraints.weightx = 1.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			panel.add(new Group(component), constraints);

			nextRow(1);
		}

		public void box(JComponent component) {
			if (term) {
				nextRow(1);
			}

			constraints.gridwidth = 2;
			constraints.gridheight = 1;
			constraints.weightx = 1.0d;
			constraints.weighty = 1.0d;
			constraints.fill = GridBagConstraints.BOTH;
			panel.add(component, constraints);

			nextRow(1);
		}

		public void term(JComponent component) {
			if (term) {
				nextRow(1);
			}

			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.EAST;
			panel.add(component, constraints);

			term = true;
		}

		public Definition definition(JComponent component) {
			return definition(component, 1);
		}

		public Definition definition(JComponent component, int height) {
			GridBagConstraints constraints = (GridBagConstraints) this.constraints.clone();
			constraints.gridx += 1;
			constraints.gridwidth = 1;
			constraints.gridheight = height;
			constraints.weightx = 1.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;

			Definition definition = new Definition(component, constraints);

			nextRow(height);

			return definition;
		}

		public class Definition {
			private JComponent component;
			private GridBagConstraints constraints;

			private Definition(JComponent component,
					GridBagConstraints constraints) {
				this.component = component;
				this.constraints = constraints;

				panel.add(component, constraints);
			}

			public Definition fillHorizontal() {
				this.constraints.fill = GridBagConstraints.HORIZONTAL;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Definition fillVertical() {
				this.constraints.fill = GridBagConstraints.VERTICAL;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Definition fillBoth() {
				this.constraints.fill = GridBagConstraints.BOTH;

				layout.setConstraints(component, constraints);
				return this;
			}

			public Definition growVertical() {
				this.constraints.weighty = 1.0f;

				layout.setConstraints(component, constraints);
				return this;
			}
		}
	}
}