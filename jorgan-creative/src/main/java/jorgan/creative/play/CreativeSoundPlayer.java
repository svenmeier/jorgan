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
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.creative.SoundFontManager;
import jorgan.creative.disposition.CreativeSound;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.GenericSoundPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link CreativeSound}.
 */
public class CreativeSoundPlayer extends GenericSoundPlayer<CreativeSound> {

	private Logger logger = Logger.getLogger(CreativeSoundPlayer.class.getName());
	
	private CreativeSound clone;

	public CreativeSoundPlayer(CreativeSound output) {
		super(output);
	}

	@Override
	protected void setUp() {
		CreativeSound output = getElement();

		clone = null;

		removeProblem(Severity.ERROR, "device");
		removeProblem(Severity.ERROR, "bank");
		removeProblem(Severity.ERROR, "soundfont");
		if (output.getOutput() != null && output.getSoundfont() != null) {
			int index = getDeviceIndex(output.getOutput());

			if (index == -1) {
				addProblem(Severity.ERROR, "device", "noCreativeDevice", output
						.getOutput());
			} else {
				try {
					new SoundFontManager().clearBank(index, output.getBank());
				} catch (Exception exception) {
				}

				try {
					new SoundFontManager().loadBank(index, output.getBank(),
							new File(output.getSoundfont()).getCanonicalPath());

					clone = (CreativeSound) output.clone();
				} catch (IllegalArgumentException ex) {
					addProblem(Severity.ERROR, "bank", "invalidBank", output
							.getBank());
				} catch (IOException ex) {
					addProblem(Severity.ERROR, "soundfont", "soundfontLoad",
							output.getSoundfont());
				} catch (Error err) {
					logger.log(Level.WARNING, "unable to use SoundFontManager", err);
					addProblem(Severity.ERROR, null, "native");
				}
			}
		}
	}

	@Override
	protected void tearDown() {
		if (clone != null) {
			try {
				int index = getDeviceIndex(clone.getOutput());
				new SoundFontManager().clearBank(index, clone.getBank());
			} catch (Exception ex) {
			}

			clone = null;
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);
		
		CreativeSound sound = getElement();
		if (sound.getSoundfont() == null) {
			addProblem(Severity.WARNING, "soundfont", "noSoundfont");
		} else {
			removeProblem(Severity.WARNING, "soundfont");
		}
	}

	private int getDeviceIndex(String device) {
		SoundFontManager manager = new SoundFontManager();

		for (int d = manager.getNumDevices() - 1; d >= 0; d--) {
			if (manager.getDeviceName(d).equals(device)) {
				return d;
			}
		}

		return -1;
	}
}
