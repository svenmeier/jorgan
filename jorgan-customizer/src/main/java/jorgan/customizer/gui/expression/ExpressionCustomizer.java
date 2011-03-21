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
package jorgan.customizer.gui.expression;

import java.util.List;

import javax.swing.JComponent;

import jorgan.customizer.gui.Customizer;
import jorgan.disposition.Continuous;
import jorgan.disposition.Controller;
import jorgan.disposition.Elements;
import jorgan.session.OrganSession;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Customizer of a single {@link Continuous}.
 */
public class ExpressionCustomizer implements Customizer {

	private static Configuration config = Configuration.getRoot().get(
			ExpressionCustomizer.class);

	private String description;

	private ExpressionPanel panel;

	public ExpressionCustomizer(OrganSession session, Controller controller) {
		description = config.get("description").read(new MessageBuilder())
				.build(Elements.getDisplayName(controller));

		panel = new ExpressionPanel(controller, get(controller));
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

	public static boolean customizes(OrganSession session, Controller controller) {
		return get(controller) != null;
	}

	private static Continuous get(Controller controller) {
		if (controller.getReferenceCount() != 1) {
			return null;
		}

		List<Continuous> continuous = controller
				.getReferenced(Continuous.class);
		if (continuous.size() != 1) {
			return null;
		}

		return continuous.get(0);
	}
}