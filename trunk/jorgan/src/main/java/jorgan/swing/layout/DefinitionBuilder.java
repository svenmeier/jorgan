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

	private JPanel panel;

	private int spacing = 2;

	private int column = -1;

	public DefinitionBuilder(JPanel panel) {
		this.panel = panel;
		panel.setLayout(new GridBagLayout());
	}

	public Column column() {
		return new Column();
	}

	public class Column {

		private int row = -1;

		private boolean term;

		private Column() {
			DefinitionBuilder.this.column++;
		}

		public void skip() {
			row++;

			term = false;
		}

		public void header(JComponent component) {
			row++;

			GridBagConstraints constraints = new GridBagConstraints();

			constraints.gridx = column * 2;
			constraints.gridy = row;
			constraints.gridwidth = 2;
			constraints.weightx = 1.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets(row > 0 ? spacing : 0, 0, 0, 0);
			panel.add(component, constraints);

			term = false;
		}

		public void term(JComponent component) {
			row++;

			GridBagConstraints constraints = new GridBagConstraints();

			constraints.gridx = column * 2;
			constraints.gridy = row;
			constraints.gridwidth = 1;
			constraints.weightx = 0.0d;
			constraints.weighty = 0.0d;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.EAST;
			constraints.insets = new Insets(row > 0 ? spacing : 0, 0, 0, spacing);
			panel.add(component, constraints);

			term = true;
		}

		public void definition(JComponent component) {
			definition(component, 1, false);
		}

		public void definition(JComponent component, boolean grow) {
			definition(component, 1, grow);
		}

		public void definition(JComponent component, int height) {
			definition(component, height, false);
		}

		public void definition(JComponent component, int height, boolean grow) {
			if (!term) {
				row++;
			}

			GridBagConstraints constraints = new GridBagConstraints();

			constraints.gridx = column * 2 + 1;
			constraints.gridy = row;
			constraints.gridwidth = 1;
			constraints.gridheight = height;
			constraints.weightx = 1.0d;
			constraints.weighty = grow ? 1.0d : 0.0d;
			constraints.fill = grow ? GridBagConstraints.BOTH
					: GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets(row > 0 ? spacing : 0, 0, 0, 0);
			panel.add(component, constraints);

			term = false;
			row += height - 1;
		}
	}
}