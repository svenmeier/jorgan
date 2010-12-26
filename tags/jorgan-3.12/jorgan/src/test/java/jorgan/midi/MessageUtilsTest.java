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

import java.util.Arrays;

import javax.sound.midi.MetaMessage;

import junit.framework.TestCase;

/**
 * Test for {@link MessageUtils}.
 */
public class MessageUtilsTest extends TestCase {

	private static final String STRING = "Text-öäüß";

	private static final byte[] BYTES = new byte[] { -1, 1, 13, 84, 101, 120,
			116, 45, -61, -74, -61, -92, -61, -68, -61, -97 };

	public void testIsChannelStatus() throws Exception {
		assertTrue(MessageUtils.isChannelStatus((byte) 176));
		assertFalse(MessageUtils.isChannelStatus((byte) 255));
	}

	public void test() throws Exception {
		assertTrue(Arrays.equals(BYTES, MessageUtils.newMetaMessage(
				MessageUtils.META_TEXT, STRING).getMessage()));

		MetaMessage message = new MetaMessage(BYTES) {
		};
		assertEquals(STRING, MessageUtils.getText(message));
	}
}
