package jorgan.soundfont.imports;

import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.swing.FileSelector;
import jorgan.swing.GridBuilder;
import bias.Configuration;

/**
 * A panel for options.
 */
public class OptionsPanel extends JPanel {

	static Configuration config = Configuration.getRoot().get(
			OptionsPanel.class);

	private FileSelector fileSelector = new FileSelector();

	private JCheckBox stopsCheckBox = new JCheckBox();

	private JSpinner bankSpinner = new JSpinner(new SpinnerNumberModel(0, 0,
			128, 1));

	/**
	 * Constructor.
	 */
	public OptionsPanel() {
		super(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow(0.0d);

		add(config.get("file").read(new JLabel()), builder.nextColumn());

		fileSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				firePropertyChange("elements", null, null);
			}
		});
		add(fileSelector, builder.nextColumn().fillHorizontal());

		builder.nextRow(0.0d);

		add(config.get("bank").read(new JLabel()), builder.nextColumn());

		add(bankSpinner, builder.nextColumn());

		builder.nextRow(1.0d);

		add(config.get("stops").read(stopsCheckBox), builder.nextColumn().alignNorth());
	}

	public File getSelectedFile() {
		return fileSelector.getSelectedFile();
	}

	public boolean getCreateStops() {
		return stopsCheckBox.isSelected();
	}

	public int getBank() {
		return ((Integer) bankSpinner.getValue());
	}
}