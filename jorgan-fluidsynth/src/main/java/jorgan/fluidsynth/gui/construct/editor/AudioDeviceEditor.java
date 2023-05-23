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
package jorgan.fluidsynth.gui.construct.editor;

import java.util.List;

import jorgan.disposition.Element;
import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.gui.construct.editor.ElementAwareEditor;
import jorgan.gui.construct.editor.TagEditor;
import jorgan.session.OrganSession;

public class AudioDeviceEditor extends TagEditor implements ElementAwareEditor {

	private FluidsynthSound sound;

	@Override
	public void setElement(OrganSession session, Element element) {
		if (element instanceof FluidsynthSound) {
			this.sound = (FluidsynthSound) element;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected String[] createTags() {
		String audioDriver = sound.getAudioDriver();
		if (audioDriver != null) {
			try {
				List<String> devices = Fluidsynth.getAudioDevices(audioDriver);

				return devices.toArray(new String[devices.size()]);
			} catch (Error fluidsynthFailure) {
			}
		}
		return new String[0];
	}
}