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
package jorgan.exporter.gui.exports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.Message;
import jorgan.exporter.exports.MessageListWriter;
import jorgan.exporter.gui.Export;
import jorgan.gui.construct.ElementsSelectionPanel;
import jorgan.gui.selection.ElementSelection;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.Page;
import bias.Configuration;

/**
 * An {@link Export} of {@link Message}s to a list.
 */
public class MessageListExport implements Export {

	private static Configuration config = Configuration.getRoot().get(
			MessageListExport.class);

	private ElementsSelectionPanel panel = new ElementsSelectionPanel();

	private OptionsPanel optionsPanel = new OptionsPanel();

	private String name;

	private String description;

	public MessageListExport(OrganSession session) {
		config.read(this);

		panel.setElements(session.getOrgan().getElements());

		panel.setSelectedElements(session.lookup(ElementSelection.class)
				.getSelectedElements());
	}

	@Override
	public List<Page> getPages() {
		return Arrays.<Page> asList(new OptionsPage(), new MessageListPage());
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getMimeType() {
		return "text";
	}

	@Override
	public void stream(OutputStream output) throws IOException {
		Writer writer = new OutputStreamWriter(output, Charset.forName("UTF-8"));

		MessageListWriter messageListWriter = new MessageListWriter(
				panel.getSelectedElements());
		optionsPanel.configure(messageListWriter);
		messageListWriter.write(writer);

		writer.flush();
	}

	private class OptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return MessageListExport.this.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			return optionsPanel;
		}
	}

	private class MessageListPage extends AbstractPage {

		@Override
		public String getDescription() {
			return MessageListExport.this.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			return panel;
		}

		@Override
		public boolean allowsNext() {
			return !panel.getSelectedElements().isEmpty();
		}
	}
}