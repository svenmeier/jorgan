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
package jorgan.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

/**
 * Utils for messages.
 */
public class MessageUtils {

	public static final int META_TEXT = 1;

	public static final int META_TRACK_NAME = 3;

	public static final int META_CUE_POINT = 7;

	public static final int META_END_OF_TRACK = 47;

	public static boolean isChannelStatus(int status) {
		status = status & 0xff;

		if (status >= 0x80 && status < 0xF0) {
			return true;
		}

		return false;
	}

	public static ShortMessage createMessage(int status, int data1, int data2)
			throws InvalidMidiDataException {

		ShortMessage shortMessage = new ShortMessage();

		shortMessage.setMessage(status, data1, data2);

		return shortMessage;
	}

	public static MidiMessage createMessage(byte[] datas)
			throws InvalidMidiDataException {
		return createMessage(datas, datas.length);
	}

	public static MidiMessage createMessage(byte[] datas, int length)
			throws InvalidMidiDataException {

		int status = datas[0] & 0xff;
		if (status == SysexMessage.SYSTEM_EXCLUSIVE) {
			return createSysexMessage(datas, length);
		} else {
			int data1 = length > 1 ? (datas[1] & 0xff) : 0;
			int data2 = length > 2 ? (datas[2] & 0xff) : 0;

			return createMessage(status, data1, data2);
		}
	}

	private static MidiMessage createSysexMessage(byte[] datas, int length)
			throws InvalidMidiDataException {
		SysexMessage sysexMessage = new SysexMessage();
		sysexMessage.setMessage(datas, length);
		return sysexMessage;
	}

	public static MetaMessage createMetaMessage(int type, String text) {

		MetaMessage message = new MetaMessage();

		try {
			byte[] bytes = text.getBytes("UTF-8");
			message.setMessage(type, bytes, bytes.length);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return message;
	}

	public static String getText(MetaMessage message) {
		byte[] bytes = message.getData();
		try {
			return new String(bytes, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] defaultDatas = new byte[3];

	public static byte[] getDatas(MidiMessage midiMessage) {
		byte[] datas;

		if (midiMessage instanceof ShortMessage) {
			// small optimization for short messages
			ShortMessage shortMessage = (ShortMessage) midiMessage;

			datas = defaultDatas;
			datas[0] = (byte) shortMessage.getStatus();
			datas[1] = (byte) shortMessage.getData1();
			datas[2] = (byte) shortMessage.getData2();
		} else {
			datas = midiMessage.getMessage();
		}

		return datas;
	}
}