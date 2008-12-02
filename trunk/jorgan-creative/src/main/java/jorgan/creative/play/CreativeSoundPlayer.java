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
package jorgan.creative.play;

import java.io.File;
import java.io.IOException;

import jorgan.creative.SoundFontManager;
import jorgan.creative.disposition.CreativeSound;
import jorgan.play.GenericSoundPlayer;
import jorgan.session.event.Severity;
import jorgan.util.Null;

/**
 * A player for a {@link CreativeSound}.
 */
public class CreativeSoundPlayer extends GenericSoundPlayer<CreativeSound> {

	private CreativeSound clone;

	public CreativeSoundPlayer(CreativeSound output) {
		super(output);
	}

	@Override
	public void update() {
		super.update();

		CreativeSound sound = getElement();
		if (sound.getSoundfont() == null) {
			addProblem(Severity.WARNING, "soundfont", "noSoundfont");
		} else {
			removeProblem(Severity.WARNING, "soundfont");
		}

		if (clone == null) {
			createManager();
		} else {
			if (!Null.safeEquals(clone.getOutput(), sound.getOutput())
					|| !Null.safeEquals(clone.getBank(), sound.getBank())
					|| !Null.safeEquals(clone.getSoundfont(), sound
							.getSoundfont())) {
				destroyManager();
				createManager();
			}
		}
	}

	@Override
	protected void destroy() {
		destroyManager();
	}

	private void createManager() {
		CreativeSound sound = getElement();

		removeProblem(Severity.ERROR, "output");
		removeProblem(Severity.ERROR, "bank");
		removeProblem(Severity.ERROR, "soundfont");
		if (sound.getOutput() != null && sound.getSoundfont() != null) {
			int index;

			try {
				index = new SoundFontManager()
						.getDeviceIndex(sound.getOutput());
			} catch (IllegalArgumentException ex) {
				addProblem(Severity.ERROR, "output", "noCreativeDevice", sound
						.getOutput());
				return;
			}

			try {
				new SoundFontManager().clearBank(index, sound.getBank());
			} catch (Exception ignore) {
			}

			try {
				new SoundFontManager().loadBank(index, sound.getBank(),
						new File(sound.getSoundfont()).getCanonicalPath());

				clone = (CreativeSound) sound.clone();
			} catch (IllegalArgumentException ex) {
				addProblem(Severity.ERROR, "bank", "invalidBank", sound
						.getBank());
				return;
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "soundfont", "soundfontLoad", sound
						.getSoundfont());
				return;
			}
		}
	}

	private void destroyManager() {
		if (clone != null) {
			try {
				int index = new SoundFontManager().getDeviceIndex(clone
						.getOutput());

				new SoundFontManager().clearBank(index, clone.getBank());
			} catch (Exception ignore) {
			}

			clone = null;
		}
	}
}
