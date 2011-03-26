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

import javax.swing.JComponent;

import jorgan.customizer.gui.Customizer;
import jorgan.disposition.Connector;
import jorgan.disposition.Elements;
import jorgan.session.OrganSession;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Customizer of a {@link Connector}.
 */
public class ConnectorCustomizer implements Customizer {

	private static Configuration config = Configuration.getRoot().get(
			ConnectorCustomizer.class);

	private String description;

	private ConnectorPanel panel;

	public ConnectorCustomizer(OrganSession session, Connector connector) {
		description = config.get("description").read(new MessageBuilder())
				.build(Elements.getDisplayName(connector));

		panel = new ConnectorPanel(connector);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JComponent getComponent() {
		return panel;
	}

	public void apply() {
		panel.apply();
	}

	public static boolean customizes(OrganSession session, Connector connector) {
		return connector.getReferenceCount() > 1;
	}
}