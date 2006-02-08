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
package jorgan.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Memory;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.swing.table.StringCellEditor;
import jorgan.swing.table.TableUtils;
import spin.Spin;
import swingx.docking.DockedPanel;

/**
 * Panel for editing of a {@link jorgan.disposition.Memory}.
 */
public class MemoryPanel extends DockedPanel {

    private static ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.gui.resources");

    private JTable table = new JTable();

    private JButton clearButton = new JButton();

    private JButton previousButton = new JButton();

    private JButton nextButton = new JButton();

    private LevelsModel model = new LevelsModel();

    private OrganSession session;

    private Memory memory;

    public MemoryPanel() {

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setCellEditor(
                new StringCellEditor());
        TableUtils.hideHeader(table);
        TableUtils.fixColumnWidth(table, 0, "888");
        TableUtils.pleasantLookAndFeel(table);
        setScrollableBody(table, true, false);

        previousButton.setToolTipText(resources.getString("memory.previous"));
        previousButton.setIcon(new ImageIcon(getClass().getResource(
                "/jorgan/gui/img/previous.gif")));
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previous();
            }
        });
        addTool(previousButton);

        nextButton.setToolTipText(resources.getString("memory.next"));
        nextButton.setIcon(new ImageIcon(getClass().getResource(
                "/jorgan/gui/img/next.gif")));
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });
        addTool(nextButton);

        clearButton.setToolTipText(resources.getString("memory.clear"));
        clearButton.setIcon(new ImageIcon(getClass().getResource(
                "/jorgan/gui/img/clear.gif")));
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        addTool(clearButton);

        setMemory(null);
    }

    protected void previous() {
        memory.setPosition(memory.getPosition() - 1);
    }

    protected void next() {
        memory.setPosition(memory.getPosition() + 1);
    }

    protected void clear() {
        int[] rows = table.getSelectedRows();
        for (int r = 0; r < rows.length; r++) {
            memory.clear(rows[r]);
        }
    }

    public void setOrgan(OrganSession session) {
        if (this.session != null) {
            this.session.getOrgan().removeOrganListener(
                    (OrganListener) Spin.over(model));

            setMemory(null);
        }

        this.session = session;

        if (this.session != null) {
            this.session.getOrgan().addOrganListener(
                    (OrganListener) Spin.over(model));

            findMemory();
        }
    }

    private void findMemory() {
        List memories = this.session.getOrgan().getCandidates(Memory.class);
        if (memories.isEmpty()) {
            setMemory(null);
        } else {
            setMemory((Memory) memories.get(0));
        }
    }

    private void setMemory(Memory memory) {
        this.memory = memory;

        model.fireTableDataChanged();

        previousButton.setEnabled(memory != null);
        nextButton.setEnabled(memory != null);
        clearButton.setEnabled(memory != null);
        table.setVisible(memory != null);

        if (memory == null) {
            setMessage(resources.getString("memory.none"));
        } else {
            setMessage(null);

            updateSelection();
        }
    }

    private void updateSelection() {
        // remove listener to avoid infinite loop
        table.getSelectionModel().removeListSelectionListener(model);

        int level = memory.getPosition();
        if (level != table.getSelectedRow()) {
            if (table.getCellEditor() != null) {
                table.getCellEditor().cancelCellEditing();
                table.setCellEditor(null);
            }
            table.getSelectionModel().setSelectionInterval(level, level);
            table.scrollRectToVisible(table.getCellRect(level, 0, false));
        }
        table.setColumnSelectionInterval(0, 0);

        // re-add listener
        table.getSelectionModel().addListSelectionListener(model);
    }

    private class LevelsModel extends AbstractTableModel implements
            OrganListener, ListSelectionListener {

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            if (memory == null) {
                return 0;
            } else {
                return 128;
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return "" + rowIndex;
            }
            return memory.getTitle(rowIndex);
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 1);
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            memory.setTitle(rowIndex, (String) aValue);
        }

        public void elementAdded(OrganEvent event) {
            if (event.getElement() instanceof Memory) {
                setMemory((Memory) event.getElement());
            }
        }

        public void elementChanged(OrganEvent event) {
            if (event.getElement() == memory) {
                setMemory(memory);
            }
        }

        public void elementRemoved(OrganEvent event) {
            if (event.getElement() instanceof Memory) {
                findMemory();
            }
        }

        public void referenceAdded(OrganEvent event) {
        }

        public void referenceChanged(OrganEvent event) {
        }

        public void referenceRemoved(OrganEvent event) {
        }

        public void valueChanged(ListSelectionEvent e) {
            int row = table.getSelectedRow();
            if (row != -1 && row != memory.getPosition()) {
                memory.setPosition(row);
            }
        }
    }
}