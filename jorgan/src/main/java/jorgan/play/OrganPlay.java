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
package jorgan.play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Organ;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganObserver;
import jorgan.disposition.event.UndoableChange;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.ReceiverWrapper;
import jorgan.midi.TransmitterWrapper;
import jorgan.play.event.KeyListener;
import jorgan.play.event.PlayListener;
import jorgan.play.spi.PlayerRegistry;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;

/**
 * A play of an organ.
 */
public abstract class OrganPlay {

	/**
	 * Only one thread is allowed to change players at a time.
	 */
	private final Object CHANGE_LOCK = new Object();

	/**
	 * Only one receiver is allowed to send a message into players at a time.
	 */
	private final Object RECEIVER_LOCK = new Object();

	private boolean open;

	/**
	 * Element to player mapping.
	 */
	private Map<Element, Player<? extends Element>> players = new HashMap<Element, Player<? extends Element>>();

	/**
	 * The handler of organ and configuration events.
	 */
	private EventHandler eventHandler = new EventHandler();

	/**
	 * All registered {@link PlayListener}s.
	 */
	private List<PlayListener> playListeners = new ArrayList<PlayListener>();

	/**
	 * All registered {@link KeyListener}s.
	 */
	private List<KeyListener> keyListeners = new ArrayList<KeyListener>();

	private ElementProblems problems = new ElementProblems();

	private Organ organ;

	/**
	 * Creates a new organ player.
	 * 
	 * @param organ
	 *            the organ to play
	 */
	public OrganPlay(Organ organ, ElementProblems problems) {
		this.organ = organ;
		this.problems = problems;

		organ.addOrganListener(eventHandler);
		organ.addOrganObserver(eventHandler);

		for (Element element : organ.getElements()) {
			createPlayer(element);
		}
	}

	public void destroy() {
		if (isOpen()) {
			close();
		}

		for (Player<? extends Element> player : players.values()) {
			player.setOrganPlay(null);
		}
		players.clear();
	}

	public Organ getOrgan() {
		return organ;
	}

	public void closed(Closed closed) {
		boolean wasOpen = isOpen();

		if (wasOpen) {
			close();
		}

		closed.run();

		if (wasOpen) {
			open();
		}
	}

	public void addPlayerListener(PlayListener listener) {
		playListeners.add(listener);
	}

	public void removePlayerListener(PlayListener listener) {
		playListeners.remove(listener);
	}

	public void addKeyListener(KeyListener listener) {
		keyListeners.add(listener);
	}

	public void removeKeyListener(KeyListener listener) {
		keyListeners.remove(listener);
	}

	protected void fireKeyPressed(Keyboard keyboard, int pitch, int velocity) {
		if (keyListeners != null) {
			for (int l = 0; l < keyListeners.size(); l++) {
				KeyListener listener = keyListeners.get(l);
				listener.keyPressed(keyboard, pitch, velocity);
			}
		}
	}

	protected void fireKeyReleased(Keyboard keyboard, int pitch) {
		if (keyListeners != null) {
			for (int l = 0; l < keyListeners.size(); l++) {
				KeyListener listener = keyListeners.get(l);
				listener.keyReleased(keyboard, pitch);
			}
		}
	}

	protected void fireReceived(MidiMessage message) {
		if (playListeners != null) {
			for (int l = 0; l < playListeners.size(); l++) {
				PlayListener listener = playListeners.get(l);
				listener.received(message);
			}
		}
	}

	public void fireSent(MidiMessage message) {
		if (playListeners != null) {
			for (int l = 0; l < playListeners.size(); l++) {
				PlayListener listener = playListeners.get(l);
				listener.sent(message);
			}
		}
	}

	protected Player<?> getPlayer(Element element) {
		return players.get(element);
	}

	public void open() {
		if (open) {
			throw new IllegalStateException("already open");
		}

		synchronized (CHANGE_LOCK) {
			Iterator<Player<? extends Element>> iterator = players.values()
					.iterator();
			while (iterator.hasNext()) {
				Player<? extends Element> player = iterator.next();
				player.open();
			}
		}

		synchronized (RECEIVER_LOCK) {
			open = true;
		}

		synchronized (CHANGE_LOCK) {
			Iterator<Player<? extends Element>> iterator = players.values()
					.iterator();
			while (iterator.hasNext()) {
				Player<? extends Element> player = iterator.next();
				player.update();
			}
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void close() {
		if (!open) {
			throw new IllegalStateException("not open");
		}

		// lock out receivers before trying to aquire change lock
		synchronized (RECEIVER_LOCK) {
			open = false;
		}

		synchronized (CHANGE_LOCK) {
			Iterator<Player<? extends Element>> iterator = players.values()
					.iterator();
			while (iterator.hasNext()) {
				Player<? extends Element> player = iterator.next();
				player.close();
			}
		}
	}

	public void play(Element element, Playing playing) {
		Player<?> player = getPlayer(element);
		if (player == null) {
			throw new IllegalArgumentException("unkown element");
		}

		synchronized (RECEIVER_LOCK) {
			if (open) {
				synchronized (CHANGE_LOCK) {
					playing.play(player);
				}
			}
		}
	}

	protected void createPlayer(Element element) {
		Player<? extends Element> player = PlayerRegistry.createPlayer(element);

		if (player != null) {
			player.setOrganPlay(this);
			players.put(element, player);

			player.update();
		}
	}

	protected void dropPlayer(Element element) {
		Player<? extends Element> player = players.get(element);
		if (player != null) {
			players.remove(element);

			player.setOrganPlay(null);
		}
	}

	protected void addProblem(Problem problem) {
		problems.addProblem(problem);
	}

	protected void removeProblem(Problem problem) {
		problems.removeProblem(problem);
	}

	private class EventHandler extends OrganAdapter implements OrganObserver {

		private boolean changedClosed = false;

		@Override
		public void propertyChanged(Element element, String name) {
			synchronized (CHANGE_LOCK) {
				Player<? extends Element> player = getPlayer(element);
				if (player != null) {
					player.update();
				}
			}
		}

		@Override
		public void elementAdded(Element element) {
			synchronized (CHANGE_LOCK) {
				createPlayer(element);
			}
		}

		@Override
		public void elementRemoved(Element element) {
			synchronized (CHANGE_LOCK) {
				dropPlayer(element);
			}
		}

		public void beforeChange(Change change) {
			if (open && change instanceof UndoableChange) {
				close();
				changedClosed = true;
			}
		}

		public void afterChange(Change change) {
			if (changedClosed) {
				changedClosed = false;
				open();
			}
		}
	}

	/**
	 * Create a transmitter for the device with the given name. The returned
	 * transmitter will automatically close the device when
	 * {@link Transmitter#close()} is called on it.<br>
	 * Note: The receiver set on the created transmitter (
	 * {@link Transmitter#setReceiver(Receiver)}) will be synchronized, see
	 * {@link #RECEIVER_LOCK} and {@link #CHANGE_LOCK}.
	 * 
	 * @param deviceName
	 *            the name of the device
	 * @return transmitter
	 * @throws MidiUnavailableException
	 */
	public Transmitter createTransmitter(String deviceName)
			throws MidiUnavailableException {
		final MidiDevice device = DevicePool.instance().getMidiDevice(
				deviceName, Direction.IN);
		device.open();

		final Transmitter transmitter = device.getTransmitter();

		return new TransmitterWrapper(transmitter) {
			public void close() {
				super.close();

				device.close();
			}

			public void setReceiver(final Receiver receiver) {
				super.setReceiver(new ReceiverWrapper(receiver) {
					public void send(MidiMessage message, long timestamp) {
						synchronized (RECEIVER_LOCK) {
							if (open) {
								synchronized (CHANGE_LOCK) {
									super.send(message, timestamp);
								}
							}
						}
					}
				});
			}
		};
	}

	/**
	 * Create a receiver for the device with the given name. The returned
	 * receiver will automatically close the device when
	 * {@link Receiver#close()} is called on it.
	 * 
	 * @param deviceName
	 *            the name of the device
	 * @return transmitter
	 * @throws MidiUnavailableException
	 */
	public Receiver createReceiver(String deviceName)
			throws MidiUnavailableException {
		final MidiDevice device = DevicePool.instance().getMidiDevice(
				deviceName, Direction.OUT);
		device.open();

		return new ReceiverWrapper(device.getReceiver()) {
			@Override
			public void close() {
				super.close();

				device.close();
			}
		};
	}

	public abstract File resolve(String name) throws IOException;

	public interface Playing {
		public void play(Player<?> player);
	}
}