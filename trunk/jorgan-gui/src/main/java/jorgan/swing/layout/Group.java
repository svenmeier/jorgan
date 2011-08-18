/**
 * 
 */
package jorgan.swing.layout;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.swing.border.RuleBorder;

public class Group extends JPanel {

	public Group(JComponent component) {
		super(new BorderLayout(10, 0));

		add(component, BorderLayout.WEST);

		JLabel rule = new JLabel();
		rule.setBorder(new RuleBorder(RuleBorder.CENTER));
		add(rule, BorderLayout.CENTER);
	}
}