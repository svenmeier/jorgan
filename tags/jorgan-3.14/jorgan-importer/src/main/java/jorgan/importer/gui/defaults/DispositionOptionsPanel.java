/**
 * 
 */
package jorgan.importer.gui.defaults;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.swing.FileSelector;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

public class DispositionOptionsPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			DispositionOptionsPanel.class);

	private FileSelector fileSelector = new FileSelector(
			FileSelector.FILES_ONLY);

	public DispositionOptionsPanel() {
		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("file").read(new JLabel()));

		fileSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				firePropertyChange("ranks", null, null);
			}
		});
		column.definition(fileSelector).fillHorizontal();
	}

	public File getDisposition() {
		return fileSelector.getSelectedFile();
	}
}