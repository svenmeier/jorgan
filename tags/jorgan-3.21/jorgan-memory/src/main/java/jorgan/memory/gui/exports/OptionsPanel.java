package jorgan.memory.gui.exports;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.memory.exports.MemoryWriter;
import jorgan.memory.exports.Range;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

/**
 * A panel for options.
 */
public class OptionsPanel extends JPanel {

	static Configuration config = Configuration.getRoot().get(
			OptionsPanel.class);

	private JRadioButton allRadioButton = new JRadioButton();

	private JRadioButton rangeRadioButton = new JRadioButton();

	private JSpinner fromSpinner;

	private JSpinner toSpinner;

	private JCheckBox useDescriptionNameCheckBox;

	private JCheckBox activeSwitchesOnlyCheckBox;

	/**
	 * Constructor.
	 */
	public OptionsPanel(int levels) {
		DefinitionBuilder builder = new DefinitionBuilder(this);
		Column column = builder.column();

		column.term(config.get("content").read(new JLabel()));

		useDescriptionNameCheckBox = new JCheckBox();
		useDescriptionNameCheckBox.setSelected(true);
		column.definition(config.get("useDescriptionName").read(
				useDescriptionNameCheckBox));

		activeSwitchesOnlyCheckBox = new JCheckBox();
		activeSwitchesOnlyCheckBox.setSelected(true);
		column.definition(config.get("activeSwitchesOnly").read(
				activeSwitchesOnlyCheckBox));

		ButtonGroup group = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				fromSpinner.setEnabled(rangeRadioButton.isSelected());
				toSpinner.setEnabled(rangeRadioButton.isSelected());
			}
		};

		column.term(config.get("level").read(new JLabel()));

		group.add(allRadioButton);
		column.definition(config.get("all").read(allRadioButton));

		group.add(rangeRadioButton);
		column.definition(config.get("range").read(rangeRadioButton));

		column.term(config.get("from").read(new JLabel()));
		fromSpinner = new JSpinner(new SpinnerNumberModel(1, 1, levels, 1));
		fromSpinner.setEnabled(false);
		column.definition(fromSpinner);

		column.term(config.get("to").read(new JLabel()));
		toSpinner = new JSpinner(new SpinnerNumberModel(levels, 1, levels, 1));
		toSpinner.setEnabled(false);
		column.definition(toSpinner);
	}

	public void configure(MemoryWriter memoryWriter) {
		memoryWriter
				.setRange(new Range(((Number) fromSpinner.getValue())
						.intValue() - 1, ((Number) toSpinner.getValue())
						.intValue() - 1));
		memoryWriter.setUseDescriptionName(useDescriptionNameCheckBox
				.isSelected());
		memoryWriter.setActiveSwitchesOnly(activeSwitchesOnlyCheckBox
				.isSelected());
	}
}