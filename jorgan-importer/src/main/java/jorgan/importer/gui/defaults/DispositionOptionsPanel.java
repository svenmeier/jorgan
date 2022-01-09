/**
 * 
 */
package jorgan.importer.gui.defaults;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bias.Configuration;
import jorgan.session.History;
import jorgan.swing.FileSelector;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;

public class DispositionOptionsPanel extends JPanel {

	private static Configuration config = Configuration.getRoot()
			.get(DispositionOptionsPanel.class);

	private FileSelector fileSelector = new FileSelector(
			FileSelector.FILES_ONLY) {
		protected File toChooser(File file) {
			if (file == null) {
				file = new History().getRecentDirectory();
			}
			return file;
		}
	};

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