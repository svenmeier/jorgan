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
package jorgan.gui.dock;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.gui.midi.KeyboardPanel;
import jorgan.midi.Loopback;
import swingx.docking.DefaultDockable;
import bias.Configuration;

/**
 * A virtual keyboard.
 */
public class KeyboardDockable extends DefaultDockable {

	private static final Configuration config = Configuration.getRoot().get(
			KeyboardDockable.class);

	private KeyboardPanel keyboard = new KeyboardPanel();

	/**
	 * Constructor.
	 */
	public KeyboardDockable() {
		config.read(this);

		keyboard.setReceiver(new Receiver() {
			public void send(MidiMessage message, long timeStamp) {
				Provider.getKeyboard().loopbackMessage(message, timeStamp);
			}

			public void close() {
				// ignore
			}
		});
		setScrollable(false);
		setContent(keyboard);
	}
	
	/**
	 * The provider for the virtual keyboard.
	 */
	public static class Provider extends MidiDeviceProvider {

		/**
		 * The device info for this providers device.
		 */
		public static final Info INFO = new Info("jOrgan Keyboard", "jOrgan",
				"Keyboard of jOrgan", "1.0") {
		};

		/**
		 * The device.
		 */
		private static Keyboard keyboard;

		@Override
		public MidiDevice.Info[] getDeviceInfo() {

			return new MidiDevice.Info[] { INFO };
		}

		@Override
		public MidiDevice getDevice(MidiDevice.Info info) {
			if (INFO == info) {
				return getKeyboard();
			}

			return null;
		}

		/**
		 * Get the loopback for this device.
		 * 
		 * @return the lookback
		 */
		public static Keyboard getKeyboard() {
			if (keyboard == null) {
				keyboard = new Keyboard();
			}
			return keyboard;
		}

		private static class Keyboard extends Loopback {
			private Keyboard() {
				super(Provider.INFO, false, true);
			}

			@Override
			protected synchronized void loopbackMessage(MidiMessage message,
					long timestamp) {
				super.loopbackMessage(message, timestamp);
			}
		}
	}
}