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
package jorgan.recorder.tracker;

import java.util.Collection;
import java.util.HashSet;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Switch;
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.MessageUtils;
import jorgan.recorder.SessionRecorder;

public class ConsoleTracker extends AbstractTracker {

	private static final String PREFIX_ACTIVE = "+";

	private static final String PREFIX_INACTIVE = "-";

	private Console console;

	private EventListener eventListener = new EventListener();

	private boolean ignoreChanges;

	public ConsoleTracker(SessionRecorder recorder, int track, Console console) {
		super(recorder, track);

		this.console = console;

		getOrgan().addOrganListener(eventListener);
	}

	@Override
	public void destroy() {
		getOrgan().removeOrganListener(eventListener);
	}

	@Override
	public Element getElement() {
		return console;
	}

	@Override
	protected boolean owns(MidiEvent event) {
		if (event.getMessage() instanceof MetaMessage) {
			MetaMessage message = (MetaMessage) event.getMessage();
			
			if (message.getType() == MessageUtils.META_TEXT) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onPlayStarting() {
		super.onPlayStarting();
		
		ignoreChanges = true;

		Collection<Element> active = getActive();

		for (Element element : getOrgan().getElements()) {
			if (element instanceof Switch) {
				Switch aSwitch = ((Switch) element);
				aSwitch.setActive(active.contains(element));
			}
		}

		ignoreChanges = false;
	}

	@Override
	public void onRecordStarting() {
		super.onRecordStarting();
		
		Collection<Element> active = getActive();

		for (Element element : getOrgan().getElements()) {
			if (element instanceof Switch) {
				Switch aSwitch = ((Switch) element);

				if (aSwitch.isActive() && !active.contains(aSwitch)) {
					String string = PREFIX_ACTIVE + aSwitch.getName();

					record(MessageUtils.newMetaMessage(MessageUtils.META_TEXT,
							string));
				} else if (!aSwitch.isActive() && active.contains(aSwitch)) {
					String string = PREFIX_INACTIVE + aSwitch.getName();

					record(MessageUtils.newMetaMessage(MessageUtils.META_TEXT,
							string));
				}
			}
		}
	}

	@Override
	public void onPlayed(MidiMessage message) {
		ignoreChanges = true;

		if (message instanceof MetaMessage) {
			MetaMessage metaMessage = (MetaMessage) message;

			if (metaMessage.getType() == MessageUtils.META_TEXT) {
				String string = MessageUtils.getText(metaMessage);
				if (string.startsWith(PREFIX_ACTIVE)) {
					Element element = getElement(string.substring(1));
					if (element != null) {
						if (element instanceof Switch) {
							((Switch) element).setActive(true);
						}
					}
				} else if (string.startsWith(PREFIX_INACTIVE)) {
					Element element = getElement(string.substring(1));
					if (element != null) {
						if (element instanceof Switch) {
							((Switch) element).setActive(false);
						}
					}
				}
			}
		}

		ignoreChanges = false;
	}

	private Element getElement(String name) {
		for (Element element : getOrgan().getElements()) {
			if (name.equals(element.getName())) {
				return element;
			}
		}
		return null;
	}

	private Collection<Element> getActive() {
		HashSet<Element> active = new HashSet<Element>();

		for (MidiEvent event : messages()) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TEXT) {
					String string = MessageUtils.getText(message);

					if (string.startsWith(PREFIX_ACTIVE)) {
						Element element = getElement(string.substring(1));
						if (element != null) {
							active.add(element);
						}
					} else if (string.startsWith(PREFIX_INACTIVE)) {
						Element element = getElement(string.substring(1));
						if (element != null) {
							active.remove(element);
						}
					}
				}
			}
		}

		return active;
	}

	private class EventListener extends OrganAdapter {

		@Override
		public void propertyChanged(Element element, String name) {
			if (ignoreChanges) {
				return;
			}

			if (element instanceof Switch && "active".equals(name)) {
				String string;

				if (((Switch) element).isActive()) {
					string = PREFIX_ACTIVE;
				} else {
					string = PREFIX_INACTIVE;
				}

				string += element.getName();

				record(MessageUtils.newMetaMessage(MessageUtils.META_TEXT,
						string));
			}
		}
	}
}