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
package jorgan.gui.construct;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;

import jorgan.disposition.Matcher;
import jorgan.swing.GridBuilder;
import bias.Configuration;

/**
 * A panel for a message.
 */
public class MessageCreationPanel extends JPanel {

	private Configuration config = Configuration.getRoot().get(
			MessageCreationPanel.class);

	private List<Class<? extends Matcher>> messageClasses;

	/**
	 * Constructor.
	 */
	public MessageCreationPanel() {
		super(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();
	}

	/**
	 * Set the classes to choose from.
	 * 
	 * @param messageClasses
	 *            the classes for the message to create
	 */
	public void setMessageClasses(List<Class<? extends Matcher>> messageClasses) {
		this.messageClasses = messageClasses;
	}

	/**
	 * Get the matcher.
	 * 
	 * @return the matcher
	 */
	public Matcher getMatcher() {
		return null;
	}
}