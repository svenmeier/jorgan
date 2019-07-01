package jorgan.exporter.gui.exports;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.exporter.exports.NamingWriter;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

/**
 * A panel for options.
 */
public class OptionsPanel extends JPanel {

	static Configuration config = Configuration.getRoot().get(
			OptionsPanel.class);

	private JCheckBox useDescriptionNameCheckBox;

	/**
	 * Constructor.
	 */
	public OptionsPanel() {
		DefinitionBuilder builder = new DefinitionBuilder(this);
		Column column = builder.column();

		column.term(config.get("content").read(new JLabel()));

		useDescriptionNameCheckBox = new JCheckBox();
		useDescriptionNameCheckBox.setSelected(true);
		column.definition(config.get("useDescriptionName").read(
				useDescriptionNameCheckBox));
	}

	public void configure(NamingWriter writer) {
		writer.setUseDescriptionName(useDescriptionNameCheckBox.isSelected());
	}
}