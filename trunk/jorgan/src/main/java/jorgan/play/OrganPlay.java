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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.ReceiverWrapper;
import jorgan.play.event.PlayListener;
import jorgan.play.spi.ProviderRegistry;
import jorgan.session.ElementProblems;

/**
 * A play of an organ.
 */
public class OrganPlay {

	private final Object CHANGE_LOCK = new Object();

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
	 * All registered playerListeners.
	 */
	private List<PlayListener> listeners = new ArrayList<PlayListener>();

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
	}

	public Organ getOrgan() {
		return organ;
	}

	public void addPlayerListener(PlayListener listener) {
		listeners.add(listener);
	}

	public void removePlayerListener(PlayListener listener) {
		listeners.remove(listener);
	}

	protected void fireClosed() {
		if (listeners != null) {
			for (int l = 0; l < listeners.size(); l++) {
				PlayListener listener = listeners.get(l);
				listener.closed();
			}
		}
	}

	protected void fireOpened() {
		if (listeners != null) {
			for (int l = 0; l < listeners.size(); l++) {
				PlayListener listener = listeners.get(l);
				listener.opened();
			}
		}
	}

	/**
	 * Fire input.
	 */
	protected void fireInputAccepted() {
		if (listeners != null) {
			for (int l = 0; l < listeners.size(); l++) {
				PlayListener listener = listeners.get(l);
				listener.inputAccepted();
			}
		}
	}

	/**
	 * Fire output.
	 */
	protected void fireOutputProduced() {
		if (listeners != null) {
			for (int l = 0; l < listeners.size(); l++) {
				PlayListener listener = listeners.get(l);
				listener.outputProduced();
			}
		}
	}

	protected Player<? extends Element> getPlayer(Element element) {
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

			iterator = players.values().iterator();
			while (iterator.hasNext()) {
				Player<? extends Element> player = iterator.next();
				player.elementChanged(null);
			}
		}

		synchronized (RECEIVER_LOCK) {
			open = true;
		}

		fireOpened();
	}

	public boolean isOpen() {
		return open;
	}

	public void close() {
		if (!open) {
			throw new IllegalStateException("not open");
		}

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

		fireClosed();
	}

	protected void createPlayer(Element element) {
		Player<? extends Element> player = ProviderRegistry
				.createPlayer(element);

		if (player != null) {
			player.setOrganPlay(this);
			players.put(element, player);

			player.elementChanged(null);
		}
	}

	protected void dropPlayer(Element element) {
		Player<? extends Element> player = players.get(element);
		if (player != null) {
			players.remove(element);
			
			player.setOrganPlay(null);
		}
	}

	private class EventHandler extends OrganAdapter {

		@Override
		public void changed(OrganEvent event) {
			synchronized (CHANGE_LOCK) {
				if (event.self()) {
					Player<? extends Element> player = getPlayer(event.getElement());
					if (player != null) {
						player.elementChanged(event);
					}
				}
			}
		}

		@Override
		public void added(OrganEvent event) {
			synchronized (CHANGE_LOCK) {
				if (event.self()) {
					createPlayer(event.getElement());
				}
			}
		}

		@Override
		public void removed(OrganEvent event) {
			synchronized (CHANGE_LOCK) {
				if (event.self()) {
					dropPlayer(event.getElement());
				}
			}
		}
	}

	protected ElementProblems getProblems() {
		return problems;
	}

	/**
	 * A synchronized receiver, to avoid race conditions.
	 * 
	 * @param receiver
	 *            the receicer to synchronize
	 */
	public class SynchronizedReceiver extends ReceiverWrapper {

		public SynchronizedReceiver(Receiver receiver) {
			super(receiver);
		}

		public void send(MidiMessage message, long timestamp) {
			synchronized (RECEIVER_LOCK) {
				if (open) {
					synchronized (CHANGE_LOCK) {
						super.send(message, timestamp);
					}
				}
			}
		}
	}
}