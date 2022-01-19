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
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.MidiGate;
import jorgan.midi.ReceiverWrapper;
import jorgan.midi.TransmitterWrapper;
import jorgan.play.event.KeyListener;
import jorgan.play.event.PlayListener;
import jorgan.play.spi.PlayerRegistry;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.time.Clock;
import jorgan.time.WakeUp;

/**
 * A play of an organ.
 */
public abstract class OrganPlay {

	private final MidiGate gate = new MidiGate();

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

	private Organ organ;

	private ElementProblems problems;

	private Clock clock;

	/**
	 * Creates a new organ player.
	 * 
	 * @param organ the organ to play
	 */
	public OrganPlay(Organ organ, ElementProblems problems, Clock clock) {
		this.organ = organ;
		this.problems = problems;
		this.clock = clock;

		organ.addOrganListener(eventHandler);

		for (Element element : organ.getElements()) {
			createPlayer(element);
		}
	}

	public abstract File resolve(String name) throws IOException;

	public void destroy() {
		if (isOpen()) {
			close();
		}

		for (Player<? extends Element> player : players.values()) {
			player.setOrganPlay(null);
		}
		players.clear();

		organ.removeOrganListener(eventHandler);
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
		if (!playListeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
	}

	public void addKeyListener(KeyListener listener) {
		keyListeners.add(listener);
	}

	public void removeKeyListener(KeyListener listener) {
		if (!keyListeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
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

	protected void fireReceived(Element element, MidiMessage message) {
		if (playListeners != null) {
			for (int l = 0; l < playListeners.size(); l++) {
				PlayListener listener = playListeners.get(l);
				listener.received(element, message);
			}
		}
	}

	public void fireSent(Element element, MidiMessage message) {
		if (playListeners != null) {
			for (int l = 0; l < playListeners.size(); l++) {
				PlayListener listener = playListeners.get(l);
				listener.sent(element, message);
			}
		}
	}

	protected Player<?> getPlayer(Element element) {
		return players.get(element);
	}

	public void open() {
		openImpl();

		gate.open();
	}

	private synchronized void openImpl() {
		if (open) {
			throw new IllegalStateException("already open");
		}
		open = true;

		Iterator<Player<? extends Element>> toOpen = players.values().iterator();
		while (toOpen.hasNext()) {
			Player<? extends Element> player = toOpen.next();
			player.open();
		}

		Iterator<Player<? extends Element>> toUpdate = players.values().iterator();
		while (toUpdate.hasNext()) {
			Player<? extends Element> player = toUpdate.next();
			player.update();
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void close() {
		gate.close();

		closeImpl();
	}

	private synchronized void closeImpl() {
		if (!open) {
			throw new IllegalStateException("not open");
		}

		Iterator<Player<? extends Element>> iterator = players.values().iterator();
		while (iterator.hasNext()) {
			Player<? extends Element> player = iterator.next();
			player.close();
		}

		open = false;
	}

	@SuppressWarnings("unchecked")
	public synchronized void play(Element element, Playing playing) {
		if (!open) {
			return;
		}

		Player<?> player = getPlayer(element);
		if (player == null) {
			throw new IllegalArgumentException("unkown element");
		}

		playing.play(player);
	}

	private synchronized void createPlayer(Element element) {
		Player<? extends Element> player = PlayerRegistry.createPlayer(element);

		if (player != null) {
			player.setOrganPlay(this);
			players.put(element, player);

			player.update();
		}
	}

	private synchronized void updatePlayer(Element element) {
		Player<? extends Element> player = getPlayer(element);
		if (player != null) {
			player.update();
		}
	}

	private synchronized void dropPlayer(Element element) {
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

	private class EventHandler extends OrganAdapter {

		@Override
		public void propertyChanged(Element element, String name) {
			updatePlayer(element);
		}

		@Override
		public void elementAdded(Element element) {
			createPlayer(element);
		}

		@Override
		public void elementRemoved(Element element) {
			dropPlayer(element);
		}
	}

	/**
	 * Create a transmitter for the device with the given name. The returned
	 * transmitter will automatically close the device when
	 * {@link Transmitter#close()} is called on it.
	 * 
	 * @param deviceName the name of the device
	 * @return transmitter
	 * @throws MidiUnavailableException
	 */
	public Transmitter createTransmitter(String deviceName) throws MidiUnavailableException {
		final MidiDevice device = DevicePool.instance().getMidiDevice(deviceName, Direction.IN);
		device.open();

		final Transmitter transmitter = device.getTransmitter();

		return new TransmitterWrapper(transmitter) {
			public void close() {
				super.close();

				device.close();
			}

			public void setReceiver(final Receiver receiver) {
				super.setReceiver(gate.guard(new ReceiverWrapper(receiver) {
					public void send(MidiMessage message, long timestamp) {
						synchronized (OrganPlay.this) {
							super.send(message, timestamp);
						}
					}
				}));
			}
		};
	}

	/**
	 * Create a receiver for the device with the given name. The returned receiver
	 * will automatically close the device when {@link Receiver#close()} is called
	 * on it.
	 * 
	 * @param deviceName the name of the device
	 * @return transmitter
	 * @throws MidiUnavailableException
	 */
	public Receiver createReceiver(String deviceName) throws MidiUnavailableException {
		final MidiDevice device = DevicePool.instance().getMidiDevice(deviceName, Direction.OUT);
		device.open();

		return new ReceiverWrapper(device.getReceiver()) {
			@Override
			public void close() {
				super.close();

				device.close();
			}
		};
	}

	public interface Playing<T extends Player<?>> {
		public void play(T player);
	}

	public void alarm(final WakeUp wakeUp, long delta) {
		clock.alarm(new WakeUpWrapper(wakeUp), delta);
	}

	/**
	 * A wrapper for {@link WakeUp}s to be triggered synchronized to this
	 * {@link OrganPlay}.
	 * 
	 * @see OrganPlay#alarm(WakeUp, long)
	 */
	private final class WakeUpWrapper implements WakeUp {
		private final WakeUp wakeUp;

		private WakeUpWrapper(WakeUp wakeUp) {
			this.wakeUp = wakeUp;
		}

		@Override
		public boolean replaces(WakeUp wakeUp) {
			if (wakeUp instanceof WakeUpWrapper) {
				return this.wakeUp.replaces(((WakeUpWrapper) wakeUp).wakeUp);
			}
			return false;
		}

		@Override
		public void trigger() {
			synchronized (OrganPlay.this) {
				wakeUp.trigger();
			}
		}
	}
}