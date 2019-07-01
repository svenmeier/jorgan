/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.customizer.gui.connector;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import jorgan.disposition.Connector;
import jorgan.disposition.Continuous;
import jorgan.disposition.Switch;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.MessageRecorder;
import jorgan.swing.combobox.BaseComboBoxModel;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * A panel for a {@link Connector}.
 */
public class ConnectorPanel extends AbstractConnectorPanel {

	private static Configuration config = Configuration.getRoot().get(
			ConnectorPanel.class);

	private Connector connector;

	private JComboBox deviceComboBox;

	private MessageRecorder recorder;

	private ContinuousPanel continuousPanel;

	private SwitchesPanel switchesPanel;

	public ConnectorPanel(Connector connector) {
		this.connector = connector;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("device").read(new JLabel()));
		deviceComboBox = new JComboBox();
		deviceComboBox.setEditable(false);
		deviceComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				record((String) deviceComboBox.getSelectedItem());
			}
		});
		column.definition(deviceComboBox).fillHorizontal();

		JTabbedPane tabbedPane = new JTabbedPane();
		column.box(tabbedPane).growVertical().fillBoth();

		switchesPanel = new SwitchesPanel(connector.getReferenced(Switch.class)) {
			@Override
			protected String getDeviceName() {
				return ConnectorPanel.this.getDeviceName();
			}
		};
		tabbedPane.addTab(config.get("switches").read(new MessageBuilder())
				.build(), switchesPanel);

		continuousPanel = new ContinuousPanel(connector
				.getReferenced(Continuous.class)) {
			@Override
			protected String getDeviceName() {
				return ConnectorPanel.this.getDeviceName();
			}
		};
		tabbedPane.addTab(config.get("continuous").read(new MessageBuilder())
				.build(), continuousPanel);

		read();
	}

	@Override
	public void addNotify() {
		super.addNotify();

		record(getDeviceName());
	}

	@Override
	public void removeNotify() {
		record(null);

		super.removeNotify();
	}

	private void read() {
		this.deviceComboBox.setModel(new BaseComboBoxModel<String>(true,
				DevicePool.instance().getMidiDeviceNames(Direction.IN)));
		this.deviceComboBox.setSelectedItem(connector.getInput());
	}

	private void record(String device) {
		if (recorder != null) {
			recorder.close();
			recorder = null;
		}

		if (device != null) {
			try {
				recorder = new MessageRecorder(device) {
					@Override
					public boolean messageRecorded(final MidiMessage message) {
						if (isShowing()) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									continuousPanel.received(message);
									switchesPanel.received(message);
								}
							});
						}
						return true;
					}
				};
			} catch (MidiUnavailableException notAvailable) {
			}
		}
	}

	public void apply() {
		connector.setInput(getDeviceName());

		switchesPanel.apply();
		continuousPanel.apply();
	}

	private String getDeviceName() {
		return (String) deviceComboBox.getSelectedItem();
	}
}