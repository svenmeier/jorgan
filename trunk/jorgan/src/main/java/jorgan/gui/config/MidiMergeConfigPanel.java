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
package jorgan.gui.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import jorgan.midi.merge.Configuration;
import jorgan.midi.merge.MidiMergerProvider;
import jorgan.sound.midi.DevicePool;
import jorgan.sound.midi.merge.MergeInput;
import jorgan.swing.table.SpinnerCellEditor;
import jorgan.swing.table.TableUtils;
import jorgan.swing.text.MultiLineLabel;

/**
 * A panel for the {@link jorgan.midi.Configuration}.
 * 
 * @see jorgan.midi.merge.MergeInput
 * @see jorgan.midi.merge.MidiMerger
 */
public class MidiMergeConfigPanel extends ConfigurationPanel {

    /**
     * All available inputs.
     */
    private ArrayList allInputs = new ArrayList();

    /**
     * All currently selected inputs - this is a subset of {@link #allInputs}.
     */
    private ArrayList selectedInputs = new ArrayList();

    /*
     * The components.
     */
    private MultiLineLabel descriptionLabel = new MultiLineLabel();

    private JScrollPane scrollPane = new JScrollPane();

    private JTable table = new JTable();

    /**
     * The table model for the mergeInputs.
     */
    private MergeInputsModel mergeInputsModel = new MergeInputsModel();

    /**
     * Create this panel.
     */
    public MidiMergeConfigPanel() {
        setLayout(new BorderLayout(10, 10));

        setName(resources.getString("config.midi.merge.name"));

        descriptionLabel.setText(resources
                .getString("config.midi.merge.description"));
        add(descriptionLabel, BorderLayout.NORTH);

        scrollPane.setPreferredSize(new Dimension(0, 0));
        add(scrollPane, BorderLayout.CENTER);

        table.setModel(mergeInputsModel);
        table.setDefaultEditor(Integer.class, new SpinnerCellEditor(0, 16, 1));
        TableUtils.pleasantLookAndFeel(table);
        TableUtils.fixColumnWidth(table, 0, Boolean.TRUE);
        scrollPane.setViewportView(table);
    }

    /**
     * Read the configuration.
     */
    public void read() {
        Configuration config = (Configuration) getConfiguration();

        // create inputs for all devices (excluding MidiMerger)
        allInputs.clear();
        String[] devices = DevicePool.getMidiDeviceNames(false);
        for (int d = 0; d < devices.length; d++) {
            if (!MidiMergerProvider.DEVICE_NAME.equals(devices[d])) {
                allInputs.add(new MergeInput(devices[d]));
            }
        }

        // get all currently selected inputs
        selectedInputs.clear();
        selectedInputs.addAll(config.getInputs());
        for (int s = 0; s < selectedInputs.size(); s++) {
            MergeInput selectedInput = (MergeInput) selectedInputs.get(s);

            int index = indexOfMergeInput(selectedInput.getDevice());
            if (index == -1) {
                allInputs.add(selectedInput);
            } else {
                allInputs.set(index, selectedInput);
            }
        }

        mergeInputsModel.fireTableDataChanged();
    }

    /**
     * Get the index of the input with the given name.
     * 
     * @param device
     *            name of input to get
     */
    private int indexOfMergeInput(String device) {
        for (int i = 0; i < allInputs.size(); i++) {
            MergeInput input = (MergeInput) allInputs.get(i);
            if (device.equals(input.getDevice())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Write the configuration.
     */
    public void write() {
        TableCellEditor cellEditor = table.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }

        Configuration config = (Configuration) getConfiguration();

        config.setInputs(selectedInputs);
    }

    /**
     * The table model for handling of inputs to the Midi-Merger.
     */
    private class MergeInputsModel extends AbstractTableModel {

        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return " ";
            case 1:
                return resources.getString("config.midi.merge.device");
            case 2:
                return resources.getString("config.midi.merge.channel");
            }
            return null;
        }

        public Class getColumnClass(int column) {
            switch (column) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            }
            return null;
        }

        public int getColumnCount() {
            return 3;
        }

        public int getRowCount() {
            return allInputs.size();
        }

        public Object getValueAt(int row, int column) {
            MergeInput input = (MergeInput) allInputs.get(row);

            switch (column) {
            case 0:
                return selectedInputs.contains(input) ? Boolean.TRUE
                        : Boolean.FALSE;
            case 1:
                return input.getDevice();
            case 2:
                return new Integer(input.getChannel() + 1);
            }
            return null;
        }

        public boolean isCellEditable(int row, int column) {
            MergeInput input = (MergeInput) allInputs.get(row);

            return column == 0
                    || (selectedInputs.contains(input) && column == 2);
        }

        public void setValueAt(Object aValue, int row, int column) {
            MergeInput input = (MergeInput) allInputs.get(row);

            switch (column) {
            case 0:
                Boolean selected = (Boolean) aValue;
                if (selected.booleanValue()) {
                    if (!selectedInputs.contains(input)) {
                        selectedInputs.add(input);
                    }
                } else {
                    if (selectedInputs.contains(input)) {
                        input.setChannel(-1);
                        selectedInputs.remove(input);
                    }
                }
                break;
            case 2:
                Integer channel = (Integer) aValue;
                input.setChannel(channel.intValue() - 1);
                break;
            }

            this.fireTableRowsUpdated(row, row);
        }
    }
}