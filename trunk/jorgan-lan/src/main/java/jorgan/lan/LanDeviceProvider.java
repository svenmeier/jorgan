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
package jorgan.lan;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import bias.Configuration;

/**
 * The provider for the LAN device.
 */
public class LanDeviceProvider extends MidiDeviceProvider {

	private static Configuration config = Configuration.getRoot().get(
			LanDeviceProvider.class);

	private static final List<SendDevice> senders = new ArrayList<SendDevice>();

	private static final List<ReceiveDevice> receivers = new ArrayList<ReceiveDevice>();

	private int senderCount;

	private int receiverCount;

	public LanDeviceProvider() {
		config.read(this);
	}

	public void setSenderCount(int count) {
		this.senderCount = count;

		ensureSenders(count);
	}

	public void setReceiverCount(int count) {
		this.receiverCount = count;

		ensureReceivers(count);
	}

	@Override
	public MidiDevice.Info[] getDeviceInfo() {

		ArrayList<Info> infos = new ArrayList<Info>();

		for (int i = 0; i < senderCount; i++) {
			infos.add(senders.get(i).getDeviceInfo());
		}

		for (int i = 0; i < receiverCount; i++) {
			infos.add(receivers.get(i).getDeviceInfo());
		}

		return infos.toArray(new Info[infos.size()]);
	}

	@Override
	public MidiDevice getDevice(MidiDevice.Info info) {
		for (SendDevice sender : senders) {
			if (sender.getDeviceInfo() == info) {
				return sender;
			}
		}

		for (ReceiveDevice receiver : receivers) {
			if (receiver.getDeviceInfo() == info) {
				return receiver;
			}
		}

		return null;
	}

	private static synchronized void ensureSenders(int count) {
		while (senders.size() < count) {
			senders.add(createSender(senders.size()));
		}
	}

	private static synchronized void ensureReceivers(int count) {
		while (receivers.size() < count) {
			receivers.add(createReceiver(receivers.size()));
		}
	}

	private static SendDevice createSender(int index) {
		return new SendDevice(index, new Info("jOrgan LAN " + (index + 1),
				"jOrgan", "jOrgan Midi over LAN", "1.0") {
		});
	}

	private static ReceiveDevice createReceiver(int index) {
		return new ReceiveDevice(index, new Info("jOrgan LAN " + (index + 1),
				"jOrgan", "jOrgan Midi over LAN", "1.0") {
		});
	}
}