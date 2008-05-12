package jorgan.swing.layout;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import swingx.docking.layout.FloatingLayout;

/**
 * A builder of a list utilizing a {@link java.awt.GridLayout}.
 */
public class FlowBuilder {

	private JPanel panel;

	public FlowBuilder(JPanel panel) {
		this.panel = panel;
		panel.setLayout(new FloatingLayout());
	}

	public Flow flow() {
		return new Flow();
	}

	public class Flow {

		private JPanel panel;

		private Flow() {
			this.panel = new JPanel(new GridLayout());
			this.panel.setAlignmentX(1.0f);
			FlowBuilder.this.panel.add(this.panel);
		}

		public void add(JComponent component) {
			this.panel.add(component);
		}
	}
}