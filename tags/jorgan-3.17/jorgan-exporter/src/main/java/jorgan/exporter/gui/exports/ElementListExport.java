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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.Element;
import jorgan.exporter.exports.ElementListWriter;
import jorgan.exporter.gui.Export;
import jorgan.gui.construct.ElementsSelectionPanel;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.Page;
import bias.Configuration;

/**
 * An {@link Export} of {@link Element}s to a list.
 */
public class ElementListExport implements Export {

	private static Configuration config = Configuration.getRoot().get(
			ElementListExport.class);

	private ElementsSelectionPanel panel = new ElementsSelectionPanel();

	private String name;

	private String description;

	public ElementListExport(OrganSession session) {
		config.read(this);

		panel.setElements(new ArrayList<Element>(session.getOrgan()
				.getElements()));
	}

	@Override
	public List<Page> getPages() {
		return Collections.<Page> singletonList(new OptionsPage());
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

		new ElementListWriter(panel.getSelectedElements()).write(writer);

		writer.flush();
	}

	private class OptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return ElementListExport.this.getDescription();
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