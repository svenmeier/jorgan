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
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.Element;
import jorgan.disposition.Switch;
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.MessageUtils;
import jorgan.recorder.Performance;
import jorgan.recorder.disposition.RecorderSwitch;
import jorgan.recorder.midi.MessageRecorder;

/**
 * Track all {@link Console}'s referenced {@link Switch}es and
 * {@link Continuous}. Does not track instances of {@link RecorderSwitch}.
 */
public class ConsoleTracker extends AbstractTracker {

	private static final String PREFIX_ACTIVE = "+";

	private static final String PREFIX_INACTIVE = "-";

	private static final String PREFIX_CHANGE = "<";

	private static final String SEPARATOR_CHANGE = ":";

	private Console console;

	private EventListener eventListener = new EventListener();

	private boolean ignoreChanges;

	public ConsoleTracker(Performance performance, int track, Console console) {
		super(performance, track);

		// enable play only
		setPlayEnabled(true);

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

		for (Switch aSwitch : console.getReferenced(Switch.class)) {
			if (RecorderSwitch.class.isInstance(aSwitch)) {
				continue;
			}

			Boolean active = readSequenceActive(aSwitch);
			if (active != null) {
				aSwitch.setActive(active);
			}
		}

		for (Continuous continuous : console.getReferenced(Continuous.class)) {
			Float value = readSequenceValue(continuous);
			if (value != null) {
				continuous.setValue(value);
			}
		}

		ignoreChanges = false;
	}

	@Override
	public void onRecordStarting() {
		super.onRecordStarting();

		for (Switch aSwitch : console.getReferenced(Switch.class)) {
			if (RecorderSwitch.class.isInstance(aSwitch)) {
				continue;
			}

			if ("".equals(aSwitch.getName())) {
				continue;
			}

			Boolean active = readSequenceActive(aSwitch);
			if (active == null || active != aSwitch.isActive()) {
				record(createMessage(aSwitch));
			}
		}

		for (Continuous continuous : console.getReferenced(Continuous.class)) {
			if ("".equals(continuous.getName())) {
				continue;
			}

			Float value = readSequenceValue(continuous);
			if (value == null || value != continuous.getValue()) {
				record(createMessage(continuous));
			}
		}
	}

	private MidiMessage createMessage(Continuous continuous) {
		StringBuilder builder = new StringBuilder();

		builder.append(PREFIX_CHANGE);
		builder.append(continuous.getValue());
		builder.append(SEPARATOR_CHANGE);
		builder.append(continuous.getName());

		return MessageUtils.newMetaMessage(MessageUtils.META_TEXT, builder
				.toString());
	}

	private MidiMessage createMessage(Switch aSwitch) {
		StringBuilder builder = new StringBuilder();

		if (aSwitch.isActive()) {
			builder.append(PREFIX_ACTIVE);
		} else {
			builder.append(PREFIX_INACTIVE);
		}

		builder.append(aSwitch.getName());

		return MessageUtils.newMetaMessage(MessageUtils.META_TEXT, builder
				.toString());
	}

	@Override
	public void onPlayed(MidiMessage message) {
		ignoreChanges = true;

		if (message instanceof MetaMessage) {
			MetaMessage metaMessage = (MetaMessage) message;

			if (metaMessage.getType() == MessageUtils.META_TEXT) {
				String string = MessageUtils.getText(metaMessage);

				if (string.startsWith(PREFIX_ACTIVE)) {
					Switch aSwitch = getReferenced(string.substring(1),
							Switch.class);
					if (aSwitch != null) {
						aSwitch.setActive(true);
					}
				} else if (string.startsWith(PREFIX_INACTIVE)) {
					Switch aSwitch = getReferenced(string.substring(1),
							Switch.class);
					if (aSwitch != null) {
						aSwitch.setActive(false);
					}
				} else if (string.startsWith(PREFIX_CHANGE)) {
					int separator = string.indexOf(SEPARATOR_CHANGE);
					if (separator != -1) {
						try {
							float value = Float.parseFloat(string.substring(1,
									separator));
							Continuous continuous = getReferenced(string
									.substring(separator + 1), Continuous.class);
							if (continuous != null) {
								continuous.setValue(value);
							}
						} catch (NumberFormatException noNumber) {
						}
					}
				}
			}
		}

		ignoreChanges = false;
	}

	private <E extends Element> E getReferenced(String string, Class<E> clazz) {
		for (E element : console.getReferenced(clazz)) {
			if (string.equals(element.getName())) {
				return element;
			}
		}

		return null;
	}

	/**
	 * Get the value for the given {@link Continuous} in the {@link MessageRecorder} or
	 * <code>null</code> if not known.
	 */
	private Float readSequenceValue(Continuous continuous) {
		Float value = null;

		for (MidiEvent event : messages()) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TEXT) {
					String string = MessageUtils.getText(message);

					if (string.startsWith(PREFIX_CHANGE)) {
						int separator = string.indexOf(SEPARATOR_CHANGE);
						if (separator != -1) {
							String name = string.substring(separator + 1);
							if (name.equals(continuous.getName())) {
								try {
									value = Float.parseFloat(string.substring(
											1, separator));
								} catch (NumberFormatException noNumber) {
								}
							}
						}
					}
				}
			}
		}

		return value;
	}

	/**
	 * Get the active state for the given {@link Switch} in the {@link MessageRecorder}
	 * or <code>null</code> if not known.
	 */
	private Boolean readSequenceActive(Switch aSwitch) {
		Boolean active = null;

		for (MidiEvent event : messages()) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TEXT) {
					String string = MessageUtils.getText(message);

					if (string.startsWith(PREFIX_ACTIVE)) {
						String name = string.substring(1);
						if (name.equals(aSwitch.getName())) {
							active = true;
						}
					} else if (string.startsWith(PREFIX_INACTIVE)) {
						String name = string.substring(1);
						if (name.equals(aSwitch.getName())) {
							active = false;
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

			if (!console.references(element)) {
				return;
			}

			if (RecorderSwitch.class.isInstance(element)) {
				return;
			}

			String elementName = element.getName();
			if ("".equals(elementName)) {
				return;
			}

			if (element instanceof Switch && "active".equals(name)) {
				record(createMessage((Switch) element));
			} else if (element instanceof Continuous && "value".equals(name)) {
				record(createMessage((Continuous) element));
			}
		}
	}
}