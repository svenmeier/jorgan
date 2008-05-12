package jorgan.soundfont.imports;

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
import jorgan.swing.GridBuilder.Row;
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
		GridBuilder builder = new GridBuilder(this);
		builder.column();
		builder.column().grow().fill();

		Row row = builder.row();

		row.cell(config.get("file").read(new JLabel()));

		fileSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				firePropertyChange("elements", null, null);
			}
		});
		row.cell(fileSelector);

		row = builder.row();

		row.cell(config.get("bank").read(new JLabel()));

		row.cell(bankSpinner);

		row = builder.row();

		row.skip();
		row.cell(config.get("stops").read(stopsCheckBox));
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