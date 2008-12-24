package jorgan.swing;

import java.awt.BorderLayout;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PercentSlider extends JPanel {

	private JTextField textField;

	private NumberFormat format;

	private JSlider slider;

	private boolean updating = false;

	public PercentSlider(double min, double value, double max) {
		super(new BorderLayout());

		this.format = NumberFormat.getPercentInstance();

		setOpaque(false);

		textField = new JTextField();
		textField.setColumns(format.format(max).length());
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!updating) {
					toTextField();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!updating) {
					toTextField();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!updating) {
					toTextField();
				}
			}
		});
		add(textField, BorderLayout.WEST);

		// never paint values (as default in GTK)
		UIManager.put("Slider.paintValue", Boolean.FALSE);
		slider = new JSlider(new DefaultBoundedRangeModel(toInt(value),
				0, toInt(min), toInt(max)));
		slider.setOpaque(false);
		slider.setPaintLabels(false);
		slider.setPaintTicks(false);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					toTextField();
				}
			}
		});
		add(slider, BorderLayout.CENTER);

		toTextField();
	}

	private int toInt(double d) {
		return (int) Math.round(d * 100);
	}

	private double toDouble(int i) {
		return i / 100.0d;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		textField.setEnabled(enabled);
		slider.setEnabled(enabled);
	}

	public double getValue() {
		return toDouble(slider.getValue());
	}

	public void addChangeListener(ChangeListener listener) {
		slider.addChangeListener(listener);
	}

	private void toTextField() {
		updating = true;

		textField.setText(format.format(toDouble((Integer) slider.getValue())));

		updating = false;
	}

	public void toSlider() {
		updating = true;

		try {
			slider.setValue(toInt(format.parse(textField.getText())
					.doubleValue()));
		} catch (ParseException ignore) {
		}

		updating = false;
	}
}
