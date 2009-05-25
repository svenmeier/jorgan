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

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Switch;
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.MessageUtils;
import jorgan.midi.Text;
import jorgan.recorder.SessionRecorder;

public class ConsoleTracker extends AbstractTracker {

	private static final String PREFIX_ACTIVE = "+";

	private static final String PREFIX_INACTIVE = "-";

	private Console console;

	private EventListener eventListener = new EventListener();

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
	public void played(MidiMessage message) {
		if (message instanceof MetaMessage) {
			MetaMessage metaMessage = (MetaMessage) message;

			if (metaMessage.getType() == Text.TYPE_TEXT) {
				Text text = new Text(metaMessage.getData());

				String string = text.toString();
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
	}

	private Element getElement(String name) {
		for (Element element : getOrgan().getElements()) {
			if (name.equals(element.getName())) {
				return element;
			}
		}
		return null;
	}

	private class EventListener extends OrganAdapter {

		@Override
		public void propertyChanged(Element element, String name) {
			if (element instanceof Switch && "active".equals(name)) {
				String string = element.getName();

				if (((Switch) element).isActive()) {
					string = PREFIX_ACTIVE + string;
				} else {
					string = PREFIX_INACTIVE + string;
				}

				Text text = new Text(string);

				record(MessageUtils.newMetaMessage(Text.TYPE_TEXT, text.getBytes()));
			}
		}
	}
}