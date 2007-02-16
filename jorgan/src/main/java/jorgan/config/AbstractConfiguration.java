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
package jorgan.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for configurations.
 */
public abstract class AbstractConfiguration implements Cloneable {

	/**
	 * The parental configuration.
	 */
	private AbstractConfiguration parent;

	/**
	 * The children configurations.
	 */
	private List<AbstractConfiguration> children = new ArrayList<AbstractConfiguration>();

	/**
	 * The listener to changes of this configuration.
	 */
	private List<ConfigurationListener> listeners = new ArrayList<ConfigurationListener>();

	/**
	 * Add a listener to this configuration.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addConfigurationListener(ConfigurationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener from this configuration.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeConfigurationListener(ConfigurationListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Inform listeners of a change.
	 */
	protected void fireConfigurationChanged() {
		for (int l = 0; l < listeners.size(); l++) {
			ConfigurationListener listener = listeners.get(l);
			listener.configurationChanged(new ConfigurationEvent(this));
		}
	}

	/**
	 * Inform listeners of a backup.
	 */
	protected void fireConfigurationBackup() {
		for (int l = 0; l < listeners.size(); l++) {
			ConfigurationListener listener = listeners.get(l);
			listener.configurationBackup(new ConfigurationEvent(this));
		}
	}

	/**
	 * Get the parental configuration.
	 * 
	 * @return the parental configuration
	 */
	public AbstractConfiguration getParent() {
		return parent;
	}

	/**
	 * Get count of childs.
	 * 
	 * @return the child count
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * Get child for the given index.
	 * 
	 * @param index
	 *            index of child to get
	 * @return child at given index
	 */
	public AbstractConfiguration getChild(int index) {
		return children.get(index);
	}

	/**
	 * Get the index of a child.
	 * 
	 * @param child
	 *            child to get index for
	 * @return index of child
	 */
	public int getChildIndex(AbstractConfiguration child) {
		return children.indexOf(child);
	}

	/**
	 * Set the parental configuration.
	 * 
	 * @param parent
	 */
	protected void setParent(AbstractConfiguration parent) {
		this.parent = parent;
	}

	/**
	 * Add a child.
	 * 
	 * @param child
	 *            child to add
	 */
	protected void addChild(AbstractConfiguration child) {
		children.add(child);

		child.setParent(this);
	}

	/**
	 * Remove a child.
	 * 
	 * @param child
	 *            child to remove
	 */
	protected void removeChild(AbstractConfiguration child) {
		children.remove(child);

		child.setParent(null);
	}

	/**
	 * Backup this configuration and all its children.
	 */
	public abstract void backup();

	/**
	 * Restore this configuration and all its children.
	 */
	public abstract void restore();

	/**
	 * Reset this configuration. <br>
	 * Children of this configuration are not changed!
	 */
	public abstract void reset();

	public Object clone() throws CloneNotSupportedException {
		AbstractConfiguration clone = (AbstractConfiguration) super.clone();

		clone.parent = null;
		clone.listeners = new ArrayList<ConfigurationListener>();
		clone.children = new ArrayList<AbstractConfiguration>();
		for (int c = 0; c < children.size(); c++) {
			clone.children.add((AbstractConfiguration) children.get(c).clone());
		}
		return clone;
	}
}