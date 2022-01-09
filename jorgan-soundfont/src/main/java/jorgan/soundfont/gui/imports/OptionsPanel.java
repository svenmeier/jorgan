package jorgan.soundfont.gui.imports;

import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bias.Configuration;
import jorgan.session.History;
import jorgan.swing.FileSelector;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;

/**
 * A panel for options.
 */
public class OptionsPanel extends JPanel {

	static Configuration config = Configuration.getRoot()
			.get(OptionsPanel.class);

	private FileSelector fileSelector = new FileSelector(
			FileSelector.FILES_ONLY) {
		protected File toChooser(File file) {
			if (file == null) {
				file = new History().getRecentDirectory();
			}
			return file;
		}
	};

	private JCheckBox stopsCheckBox = new JCheckBox();

	private JCheckBox touchSensitiveCheckBox = new JCheckBox();

	private JSpinner bankSpinner;

	/**
	 * Constructor.
	 */
	public OptionsPanel() {
		DefinitionBuilder builder = new DefinitionBuilder(this);
		Column column = builder.column();

		column.term(config.get("file").read(new JLabel()));

		fileSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				firePropertyChange("elements", null, null);
			}
		});
		column.definition(fileSelector).fillHorizontal();

		column.term(config.get("bank").read(new JLabel()));
		bankSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 128, 1));
		column.definition(bankSpinner);

		column.definition(config.get("stops").read(stopsCheckBox));

		column.definition(
				config.get("touchSensitive").read(touchSensitiveCheckBox));
	}

	public File getSelectedFile() {
		return fileSelector.getSelectedFile();
	}

	public boolean getCreateStops() {
		return stopsCheckBox.isSelected();
	}

	public boolean getTouchSensitive() {
		return touchSensitiveCheckBox.isSelected();
	}

	public int getBank() {
		return ((Integer) bankSpinner.getValue());
	}
}