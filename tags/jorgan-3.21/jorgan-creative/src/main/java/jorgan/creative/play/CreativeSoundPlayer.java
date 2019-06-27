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

import java.io.IOException;

import jorgan.creative.SoundFontManager;
import jorgan.creative.disposition.CreativeSound;
import jorgan.play.GenericSoundPlayer;
import jorgan.problem.Severity;
import jorgan.util.Null;

/**
 * A player for a {@link CreativeSound}.
 */
public class CreativeSoundPlayer extends GenericSoundPlayer<CreativeSound> {

	private CreativeSound clone;

	private SoundFontManager manager;

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
		removeProblem(Severity.ERROR, "soundfont");
		removeProblem(Severity.ERROR, null);

		String output = sound.getOutput();
		if (output != null) {
			try {
				manager = new SoundFontManager(output);

				clone = (CreativeSound) sound.clone();
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "output", "outputInvalid", sound
						.getOutput());
				return;
			} catch (NoClassDefFoundError failure) {
				addProblem(Severity.ERROR, "output", "creativeFailure", sound
						.getOutput());
				return;
			}

			try {
				manager.clear(sound.getBank());
			} catch (IOException ignore) {
			}

			if (sound.getSoundfont() != null) {
				try {
					manager
							.load(sound.getBank(),
									resolve(sound.getSoundfont()));
				} catch (IOException ex) {
					addProblem(Severity.ERROR, "soundfont", "soundfontLoad",
							sound.getSoundfont());
					return;
				}
			}
		}
	}

	private void destroyManager() {
		if (clone != null) {
			try {
				manager.clear(clone.getBank());
			} catch (Exception ignore) {
			}
			manager.destroy();
			manager = null;

			clone = null;
		}
	}
}
