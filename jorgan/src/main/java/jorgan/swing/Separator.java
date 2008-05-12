package jorgan.swing;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.swing.border.RuleBorder;

public class Separator<T extends JComponent> extends JPanel {

	private T t;

	public Separator(T t) {
		super(new BorderLayout(10, 0));

		this.t = t;
		add(t, BorderLayout.WEST);

		JLabel rule = new JLabel();
		rule.setBorder(new RuleBorder(RuleBorder.CENTER));
		add(rule, BorderLayout.CENTER);
	}

	public T getComponent() {
		return t;
	}

	public static class Label extends Separator<JLabel> {
		public Label() {
			super(new JLabel());
		}

		public void setText(String text) {
			getComponent().setText(text);
		}

		public String getText() {
			return getComponent().getText();
		}
	}

	public static class CheckBox extends Separator<JCheckBox> {
		public CheckBox() {
			super(new JCheckBox());
		}

		public void setText(String text) {
			getComponent().setText(text);
		}

		public String getText() {
			return getComponent().getText();
		}
	}

}