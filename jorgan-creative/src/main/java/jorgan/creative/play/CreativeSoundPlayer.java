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

import jorgan.creative.GenericException;
import jorgan.creative.SoundFontManager;
import jorgan.creative.disposition.CreativeSound;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.GenericSoundPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link CreativeSound}.
 */
public class CreativeSoundPlayer extends GenericSoundPlayer<CreativeSound> {

	private CreativeSound clone;

	public CreativeSoundPlayer(CreativeSound output) {
		super(output);
	}

	@Override
	protected void setUp() {
		createManager();
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (event != null) {
			// TODO when necessary only 
			destroyManager();
			createManager();
		}
		
		CreativeSound sound = getElement();
		if (sound.getSoundfont() == null) {
			addProblem(Severity.WARNING, "soundfont", "noSoundfont");
		} else {
			removeProblem(Severity.WARNING, "soundfont");
		}
	}

	@Override
	protected void tearDown() {
		destroyManager();
	}

	private void createManager() {
		CreativeSound output = getElement();

		clone = null;

		removeProblem(Severity.ERROR, "device");
		removeProblem(Severity.ERROR, "bank");
		removeProblem(Severity.ERROR, "soundfont");
		if (output.getOutput() != null && output.getSoundfont() != null) {
			try {
				int index;

				try {
					index = new SoundFontManager().getDeviceIndex(output
							.getOutput());
				} catch (IllegalArgumentException ex) {
					addProblem(Severity.ERROR, "device", "noCreativeDevice",
							output.getOutput());
					return;
				}

				try {
					new SoundFontManager().clearBank(index, output.getBank());
				} catch (Exception ignore) {
				}

				try {
					new SoundFontManager().loadBank(index, output.getBank(),
							new File(output.getSoundfont()).getCanonicalPath());

					clone = (CreativeSound) output.clone();
				} catch (IllegalArgumentException ex) {
					addProblem(Severity.ERROR, "bank", "invalidBank", output
							.getBank());
					return;
				} catch (IOException ex) {
					addProblem(Severity.ERROR, "soundfont", "soundfontLoad",
							output.getSoundfont());
					return;
				}
			} catch (GenericException ex) {
				addProblem(Severity.ERROR, null, "genericFailure", ex
						.getMessage());
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
