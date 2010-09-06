package jorgan.swing.layout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A builder of flows.
 */
public class FlowBuilder {

	public static final int TOP = SwingConstants.TOP;
	public static final int LEFT = SwingConstants.LEFT;
	public static final int BOTTOM = SwingConstants.BOTTOM;
	public static final int RIGHT = SwingConstants.RIGHT;

	private JPanel panel;

	private int anchor;

	private GridBagConstraints constraints = new GridBagConstraints();

	public FlowBuilder(JPanel panel, int anchor) {
		this.panel = panel;
		this.anchor = anchor;

		this.panel.setLayout(new GridBagLayout());

		GridBagConstraints fillerConstraints = new GridBagConstraints();
		fillerConstraints.weightx = 1.0d;
		fillerConstraints.weighty = 1.0d;

		switch (anchor) {
		case TOP:
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 0;

			fillerConstraints.gridy = 512;
			break;
		case BOTTOM:
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 512;

			fillerConstraints.gridy = 0;
			break;
		case LEFT:
			constraints.fill = GridBagConstraints.VERTICAL;
			constraints.gridx = 0;
			constraints.gridy = 0;

			fillerConstraints.gridx = 512;
			break;
		case RIGHT:
			constraints.fill = GridBagConstraints.VERTICAL;
			constraints.gridx = 512;
			constraints.gridy = 0;

			fillerConstraints.gridx = 0;
			break;
		}

		this.panel.add(new JPanel(), fillerConstraints);
	}

	public Flow flow() {
		return new Flow();
	}

	public class Flow {

		private Flow() {
		}

		public void add(JComponent component) {
			switch (anchor) {
			case TOP:
				constraints.gridy++;
				break;
			case BOTTOM:
				constraints.gridy--;
				break;
			case LEFT:
				constraints.gridx++;
				break;
			case RIGHT:
				constraints.gridx--;
				break;
			}
			panel.add(component, constraints);
		}
	}
}