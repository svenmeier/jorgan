package jorgan.soundfont.imports;

import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
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

	/**
	 * Constructor.
	 */
	public OptionsPanel() {
		super(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow(1.0d);

		add(config.get("file").read(new JLabel()), builder.nextColumn());

		fileSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				firePropertyChange("ranks", null, null);
			}
		});
		add(fileSelector, builder.nextColumn().fillHorizontal());
	}

	public File getSelectedFile() {
		return fileSelector.getSelectedFile();
	}
}