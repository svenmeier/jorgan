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
package jorgan.gui.dock;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.swing.CellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Console;
import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Message;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.OrganPanel;
import jorgan.gui.construct.CreateMessageWizard;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.midi.ShortMessageRecorder;
import jorgan.midi.mpl.Equal;
import jorgan.midi.mpl.NoOp;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.StringCellEditor;
import jorgan.swing.table.TableUtils;
import spin.Spin;
import swingx.dnd.ObjectTransferable;
import swingx.docking.Docked;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Panel shows the messages of elements.
 */
public class MessagesDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			MessagesDockable.class);

	private static final Icon inputIcon = new ImageIcon(OrganPanel.class
			.getResource("img/input.gif"));

	private static final Icon interceptIcon = new ImageIcon(OrganPanel.class
			.getResource("img/intercept.gif"));

	private static final Icon outputIcon = new ImageIcon(OrganPanel.class
			.getResource("img/output.gif"));

	/**
	 * The edited organ.
	 */
	private OrganSession session;

	private Element element;

	private ObjectTransferable transferable;

	private List<Message> messages = new ArrayList<Message>();

	/**
	 * The listener to selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	private RecordAction recordAction = new RecordAction();

	private JTable table = new JTable();

	private JToggleButton sortByTypeButton = new JToggleButton();

	private MessagesModel tableModel = new MessagesModel();

	/**
	 * Create a tree panel.
	 */
	public MessagesDockable() {
		config.read(this);

		config.get("sortByType").read(sortByTypeButton);
		sortByTypeButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateMessages();
			}
		});

		config.get("table").read(tableModel);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(tableModel);
		table.setTransferHandler(new TransferHandler() {

			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) throws IllegalStateException {
				int[] rows = table.getSelectedRows();
				if (rows.length > 0) {
					Message[] subMessages = new Message[rows.length];
					for (int m = 0; m < subMessages.length; m++) {
						subMessages[m] = messages.get(rows[m]);
					}

					for (Message message : subMessages) {
						if (action == DnDConstants.ACTION_MOVE) {
							element.removeMessage(message);
						}
					}

					transferable = new ObjectTransferable(subMessages);

					clip.setContents(transferable, null);
				}
			}

			@Override
			public boolean importData(JComponent comp, Transferable t) {
				try {
					Message[] subMessages = (Message[]) ObjectTransferable
							.getObject(t);
					for (Message message : subMessages) {
						element.addMessage(message.clone());
					}

					return true;
				} catch (Exception noImport) {
					return false;
				}
			}
		});
		table.getSelectionModel().addListSelectionListener(selectionHandler);
		new IconTableCellRenderer() {
			@Override
			protected Icon getIcon(Object value) {
				if (value instanceof InputMessage) {
					return inputIcon;
				} else if (value instanceof OutputMessage) {
					return outputIcon;
				} else {
					return interceptIcon;
				}
			}
		}.configureTableColumn(table, 0);
		table.getColumnModel().getColumn(2).setCellEditor(
				new StringCellEditor());
		table.getColumnModel().getColumn(3).setCellEditor(
				new StringCellEditor());
		table.getColumnModel().getColumn(4).setCellEditor(
				new StringCellEditor());
		TableUtils.pleasantLookAndFeel(table);

		setContent(new JScrollPane(table));
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	@Override
	protected void addTools(Docked docked) {

		docked.addTool(addAction);
		docked.addTool(removeAction);
		docked.addToolSeparator();
		docked.addTool(sortByTypeButton);
		docked.addToolSeparator();
		docked.addTool(recordAction);
	}

	private void commitEdit() {
		CellEditor editor = table.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}

	/**
	 * Set the organ to be edited.
	 * 
	 * @param session
	 *            session to be edited
	 */
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(tableModel));
			this.session.lookup(ElementSelection.class).removeListener(
					selectionHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(tableModel));
			this.session.lookup(ElementSelection.class).addListener(
					selectionHandler);
		}

		if (transferable != null) {
			transferable.clear();
			transferable = null;
		}

		updateMessages();
	}

	private boolean updating = false;

	private void updateMessages() {
		if (updating) {
			return;
		}

		try {
			updating = true;
			commitEdit();

			element = null;
			messages.clear();
			tableModel.update();
			table.setVisible(false);

			if (session != null
					&& session.lookup(ElementSelection.class)
							.getSelectionCount() == 1) {

				element = session.lookup(ElementSelection.class)
						.getSelectedElement();

				for (Message message : element.getMessages()) {
					messages.add(message);
				}

				if (sortByTypeButton.isSelected()) {
					Collections.sort(messages, new MessageComparator());
				}

				tableModel.update();
				table.setVisible(true);
			}

			addAction.update();
		} finally {
			updating = false;
		}
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler implements SelectionListener,
			ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			// TODO should change ElementSelection ?
			if (session != null) {
				session.lookup(UndoManager.class).compound();
			}

			removeAction.update();
			recordAction.update();
		}

		public void selectionChanged() {
			updateMessages();
		}
	}

	/**
	 * Note that <em>Spin</em> ensures that the methods of this listeners are
	 * called on the EDT, although a change in the organ might be triggered by a
	 * change on a MIDI thread.
	 */
	public class MessagesModel extends AbstractTableModel implements
			OrganListener {

		private String[] columnNames = new String[5];

		private void update() {
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}

		public int getColumnCount() {
			return 5;
		}

		public int getRowCount() {
			return messages.size();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex >= 2;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Message message = messages.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return message;
			case 1:
				return Elements.getDisplayName(message.getClass());
			case 2:
				return message.getStatus();
			case 3:
				return message.getData1();
			case 4:
				return message.getData2();
			default:
				throw new IllegalArgumentException("" + columnIndex);
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Message message = messages.get(rowIndex);

			String status = message.getStatus();
			String data1 = message.getData1();
			String data2 = message.getData2();
			switch (columnIndex) {
			case 2:
				status = (String) aValue;
				break;
			case 3:
				data1 = (String) aValue;
				break;
			case 4:
				data2 = (String) aValue;
				break;
			}

			element.changeMessage(message, status, data1, data2);
		}

		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			if (Element.MESSAGE.equals(name)
					&& element == MessagesDockable.this.element) {
				Message message = (Message) value;

				updateMessages();

				int index = messages.indexOf(message);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		}

		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			if (Element.MESSAGE.equals(name)
					&& element == MessagesDockable.this.element) {
				updateMessages();
			}
		}

		public void indexedPropertyChanged(Element element, String name,
				Object value) {
			if (Element.MESSAGE.equals(name)
					&& element == MessagesDockable.this.element) {
				Message message = (Message) value;

				updateMessages();

				int index = messages.indexOf(message);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		}

		public void elementAdded(Element element) {
		}

		public void elementRemoved(Element element) {
		}

		public void propertyChanged(Element element, String name) {
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			CreateMessageWizard
					.showInDialog(table, session.getOrgan(), element);
		}

		public void update() {
			setEnabled(element != null);
		}
	}

	private class RemoveAction extends BaseAction implements Compound {

		private RemoveAction() {
			config.get("remove").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(UndoManager.class).compound(this);
		}

		public void update() {
			setEnabled(table.getSelectedRow() != -1);
		}

		public void run() {
			int[] indices = table.getSelectedRows();
			if (indices != null) {
				for (int i = indices.length - 1; i >= 0; i--) {
					Message message = messages.get(indices[i]);

					element.removeMessage(message);
				}
			}
		}
	}

	private class RecordAction extends BaseAction {

		private MessageBox messageBox = new MessageBox(
				MessageBox.OPTIONS_OK_CANCEL);

		private RecordAction() {
			config.get("record").read(this);

			config.get("record/message").read(messageBox);

			setEnabled(false);
		}

		public void update() {
			int[] indices = table.getSelectedRows();
			setEnabled(indices.length == 1
					&& messages.get(indices[0]) instanceof InputMessage);
		}

		public void actionPerformed(ActionEvent ev) {
			Console console = null;

			if (element instanceof Displayable) {
				for (Console referrer : session.getOrgan().getReferrer(element,
						Console.class)) {
					console = referrer;
					break;
				}
			}

			if (console != null) {
				record(console.getInput());
			}
		}

		private void record(String deviceName) {
			try {
				ShortMessageRecorder recorder = new ShortMessageRecorder(
						deviceName) {
					@Override
					public boolean messageRecorded(final ShortMessage message) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								recorded(message);

								messageBox.hide();
							}
						});
						return false;
					}
				};

				if (messageBox.show(table) == MessageBox.OPTION_OK) {

				}

				recorder.close();
			} catch (MidiUnavailableException cannotRecord) {
				session.lookup(UndoManager.class).compound();

				int index = table.getSelectedRow();
				if (index != -1) {
					Message message = messages.get(index);

					element.changeMessage(message, new NoOp().toString(),
							new NoOp().toString(), new NoOp().toString());
				}
			}
		}

		private void recorded(ShortMessage shortMessage) {
			session.lookup(UndoManager.class).compound();

			int index = table.getSelectedRow();
			if (index != -1) {
				Message message = messages.get(index);

				element.changeMessage(message, new Equal(shortMessage
						.getStatus()).toString(), new Equal(shortMessage
						.getData1()).toString(), new Equal(shortMessage
						.getData2()).toString());
			}
		}
	}

	private class MessageComparator implements Comparator<Message> {
		public int compare(Message m1, Message m2) {
			int order = m1.getClass().getName().compareTo(
					m2.getClass().getName());

			if (m1 instanceof InputMessage) {
				order -= 100;
			}
			if (m2 instanceof InputMessage) {
				order += 100;
			}

			if (m1 instanceof OutputMessage) {
				order += 100;
			}
			if (m2 instanceof OutputMessage) {
				order -= 100;
			}

			return order;
		}
	}
}