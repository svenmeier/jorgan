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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Connector;
import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.MessageTableCellRenderer;
import jorgan.gui.construct.CreateMessageWizard;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.midi.MessageRecorder;
import jorgan.midi.MessageUtils;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midi.mpl.Tuple;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.FormatterCellEditor;
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
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setTransferHandler(new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return COPY | MOVE;
			}

			@Override
			public boolean canImport(TransferSupport support) {
				return !sortByTypeButton.isSelected();
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				int[] rows = table.getSelectedRows();

				Message[] subMessages = new Message[rows.length];
				for (int t = 0; t < subMessages.length; t++) {
					subMessages[t] = messages.get(rows[t]);
				}

				return new ObjectTransferable(subMessages);
			}

			@Override
			protected void exportDone(JComponent source, Transferable t,
					int action) {
				try {
					if (action == MOVE) {
						Message[] subMessages = (Message[]) ObjectTransferable
								.getObject(t);
						for (Message message : subMessages) {
							element.removeMessage(message);
						}
					}
				} catch (Exception noExport) {
				}
			}

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
			public boolean importData(final TransferSupport support) {
				try {
					final Message[] subMessages = (Message[]) ObjectTransferable
							.getObject(support.getTransferable());

					session.lookup(UndoManager.class).compound(new Compound() {
						@Override
						public void run() {
							int index = TableUtils.importIndex(table, support);

							for (Message message : subMessages) {
								element.addMessage(message.clone(), index);
								index++;
							}
						}
					});
					return true;
				} catch (Exception noImport) {
					return false;
				}
			}
		});
		table.getSelectionModel().addListSelectionListener(selectionHandler);
		table.getColumnModel().getColumn(0).setCellRenderer(
				new MessageTableCellRenderer());
		table.getColumnModel().getColumn(1).setCellEditor(
				new FormatterCellEditor(new CommandsFormatter()));
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

			TableUtils.stopEdit(table);

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

				Object location = session.lookup(ElementSelection.class)
						.getLocation();
				if (location instanceof Message) {
					int row = messages.indexOf(location);
					if (row != -1) {
						table.getSelectionModel()
								.setSelectionInterval(row, row);
					}
				}
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
	public class MessagesModel extends BaseTableModel<Message> implements
			OrganListener {

		private void update() {
			fireTableDataChanged();
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return messages.size();
		}

		@Override
		protected boolean isEditable(Message row, int columnIndex) {
			return columnIndex > 0;
		}

		@Override
		protected Message getRow(int rowIndex) {
			return messages.get(rowIndex);
		}

		@Override
		protected Object getValue(Message message, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return message;
			case 1:
				return message.getTuple();
			default:
				throw new IllegalArgumentException("" + columnIndex);
			}
		}

		@Override
		protected void setValue(Message message, int columnIndex, Object value) {
			element.changeMessage(message, (Tuple) value);
		}

		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			if (Element.MESSAGE.equals(name)
					&& element == MessagesDockable.this.element) {
				updateMessages();
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
				updateMessages();
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
			CreateMessageWizard.showInDialog(table, session, element);
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
			TableUtils.stopEdit(table);

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

		private MessageBox messageBox = new MessageBox(MessageBox.OPTIONS_OK);

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
			Connector connector = null;

			if (element instanceof Connector) {
				connector = (Connector) element;
			} else if (element instanceof Displayable) {
				for (Connector referrer : session.getOrgan().getReferrer(
						element, Connector.class)) {
					connector = referrer;
					break;
				}
			}

			int row = table.getSelectedRow();
			TableUtils.stopEdit(table);
			table.getSelectionModel().setSelectionInterval(row, row);

			if (connector != null) {
				record(connector.getInput());
			}
		}

		private void record(String deviceName) {
			try {
				MessageRecorder recorder = new MessageRecorder(deviceName) {
					@Override
					public boolean messageRecorded(final MidiMessage message) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								recorded(MessageUtils.getDatas(message));

								messageBox.hide();
							}
						});
						return false;
					}
				};

				messageBox.show(table);

				recorder.close();
			} catch (MidiUnavailableException cannotRecord) {
				session.lookup(UndoManager.class).compound();
			}
		}

		private void recorded(byte[] datas) {
			session.lookup(UndoManager.class).compound();

			int index = table.getSelectedRow();
			if (index != -1) {
				Message message = messages.get(index);

				element.changeMessage(message, Tuple.equal(datas));
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

	private static class CommandsFormatter extends AbstractFormatter {

		@Override
		public Object stringToValue(String text) throws ParseException {
			try {
				return Tuple.fromString(text);
			} catch (ProcessingException ex) {
				throw new ParseException(text, 0);
			}
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value == null) {
				return "";
			}

			return ((Tuple) value).toString();
		}
	}
}