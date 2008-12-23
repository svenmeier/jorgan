package jorgan.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpinnerSlider extends JPanel {

	private JSpinner spinner;

	private JSlider slider;

	private boolean updating = false;
	
	public SpinnerSlider(int min, int value, int max) {
		super(new BorderLayout());

		setOpaque(false);

		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					updating = true;
					
					slider.setValue((Integer)spinner.getValue());
					
					updating = false;
				}
			}
		});
		add(spinner, BorderLayout.WEST);

		// never paint values (as default in GTK)
		UIManager.put("Slider.paintValue", Boolean.FALSE);
		slider = new JSlider(min, max, value);
		slider.setPaintLabels(false);
		slider.setPaintTicks(false);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					updating = true;
					
					spinner.setValue((Integer)slider.getValue());
					
					updating = false;
				}
			}
		});
		add(slider, BorderLayout.CENTER);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		spinner.setEnabled(enabled);
		slider.setEnabled(enabled);
	}
	
	public int getValue() {
		return (Integer)spinner.getValue();
	}
	
	public void addChangeListener(ChangeListener listener) {
		spinner.addChangeListener(listener);
	}
}
