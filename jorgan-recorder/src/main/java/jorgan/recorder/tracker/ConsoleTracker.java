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

import jorgan.disposition.Combination;
import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.Switch;
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.MessageUtils;
import jorgan.recorder.Performance;
import jorgan.recorder.disposition.RecorderSwitch;
import jorgan.recorder.midi.MessageRecorder;
import bias.Configuration;

/**
 * Track all {@link Console}'s referenced {@link Switch}es and
 * {@link Continuous}. Does not track instances of {@link RecorderSwitch}.
 */
public class ConsoleTracker extends AbstractTracker {

	private static Configuration config = Configuration.getRoot().get(
			ConsoleTracker.class);

	private static final String PREFIX_ACTIVE = "+";

	private static final String PREFIX_INACTIVE = "-";

	private static final String PREFIX_CHANGE = "<";

	private Console console;

	private EventListener eventListener = new EventListener();

	private boolean recordCombinationRecalls;

	private boolean ignoreChanges;

	private Organ organ;

	public ConsoleTracker(int track, Console console) {
		super(track);

		config.read(this);

		this.console = console;
	}

	public void attach(Performance performance) {
		super.attach(performance);

		this.organ = performance.getPlay().getOrgan();
		organ.addOrganListener(eventListener);
	}

	@Override
	public void detach() {
		organ.removeOrganListener(eventListener);
		organ = null;

		super.detach();
	}

	public void setRecordCombinationRecalls(boolean recordCombinationRecalls) {
		this.recordCombinationRecalls = recordCombinationRecalls;
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

			Boolean active = readSequenceActive(aSwitch);
			if (active == null || active != aSwitch.isActive()) {
				record(createMessage(aSwitch));
			}
		}

		for (Continuous continuous : console.getReferenced(Continuous.class)) {

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
		builder.append(" ");
		builder.append(encode(continuous));

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

		builder.append(" ");
		builder.append(encode(aSwitch));

		return MessageUtils.newMetaMessage(MessageUtils.META_TEXT, builder
				.toString());
	}

	@Override
	public void onPlayed(MidiMessage message) {
		ignoreChanges = true;

		if (message instanceof MetaMessage) {
			MetaMessage metaMessage = (MetaMessage) message;

			if (metaMessage.getType() == MessageUtils.META_TEXT) {
				String text = MessageUtils.getText(metaMessage);

				try {
					if (text.startsWith(PREFIX_ACTIVE)) {
						Switch aSwitch = getReferenced(text, Switch.class);
						if (aSwitch != null) {
							aSwitch.setActive(true);
						}
					} else if (text.startsWith(PREFIX_INACTIVE)) {
						Switch aSwitch = getReferenced(text, Switch.class);
						if (aSwitch != null) {
							aSwitch.setActive(false);
						}
					} else if (text.startsWith(PREFIX_CHANGE)) {
						float value = Float.parseFloat(text.substring(1, text
								.indexOf(' ')));
						Continuous continuous = getReferenced(text,
								Continuous.class);
						if (continuous != null) {
							continuous.setValue(value);
						}
					}
				} catch (IllegalArgumentException invalidMessage) {
				}
			}
		}

		ignoreChanges = false;
	}

	@SuppressWarnings("unchecked")
	private <E extends Element> E getReferenced(String text, Class<E> clazz)
			throws IllegalArgumentException {
		Element element = decode(text);
		if (clazz.isInstance(element) && console.references(element)) {
			return (E) element;
		}

		return null;
	}

	/**
	 * Get the value for the given {@link Continuous} in the
	 * {@link MessageRecorder} or <code>null</code> if not known.
	 */
	private Float readSequenceValue(Continuous continuous) {
		Float value = null;

		for (MidiEvent event : messages()) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TEXT) {
					String text = MessageUtils.getText(message);

					try {
						if (text.startsWith(PREFIX_CHANGE)) {
							if (continuous == decode(text)) {
								value = Float.parseFloat(text.substring(1, text
										.indexOf(' ')));
							}
						}
					} catch (IllegalArgumentException invalidMessage) {
					}
				}
			}
		}

		return value;
	}

	/**
	 * Get the active state for the given {@link Switch} in the
	 * {@link MessageRecorder} or <code>null</code> if not known.
	 */
	private Boolean readSequenceActive(Switch aSwitch) {
		Boolean active = null;

		for (MidiEvent event : messages()) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TEXT) {
					String text = MessageUtils.getText(message);

					try {
						if (aSwitch == decode(text)) {
							if (text.startsWith(PREFIX_ACTIVE)) {
								active = true;
							} else if (text.startsWith(PREFIX_INACTIVE)) {
								active = false;
							}
						}
					} catch (IllegalArgumentException invalidMessage) {
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

			if (recordCombinationRecalls) {
				for (Combination combination : organ.getReferrer(element,
						Combination.class)) {
					if (combination.isRecalling()) {
						return;
					}
				}
			} else {
				if (element instanceof Combination) {
					return;
				}
			}

			if (element instanceof Switch && "active".equals(name)) {
				record(createMessage((Switch) element));
			} else if (element instanceof Continuous && "value".equals(name)) {
				record(createMessage((Continuous) element));
			}
		}
	}
}