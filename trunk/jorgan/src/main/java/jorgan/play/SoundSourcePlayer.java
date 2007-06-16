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

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiUnavailableException;

import jorgan.disposition.SoundSource;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.sound.DelayedSound;
import jorgan.play.sound.Sound;
import jorgan.play.sound.SoundFactory;
import jorgan.play.sound.SoundFactoryException;
import jorgan.play.sound.SoundFactoryParameterException;
import jorgan.play.sound.SoundWrapper;

/**
 * A player of a {@link jorgan.disposition.SoundSource}.
 */
public class SoundSourcePlayer extends Player<SoundSource> {

	private static final Problem warningDevice = new Problem(Problem.WARNING,
			"device");

	private static final Problem errorDevice = new Problem(Problem.ERROR,
			"device");

	private static final Problem errorType = new Problem(Problem.ERROR, "type");

	private static final Problem errorTypeParameter = new Problem(
			Problem.ERROR, "type.parameter");

	/**
	 * The factory for sounds.
	 */
	private SoundFactory factory;

	private Map<Integer, SoundSourceSound> sounds = new HashMap<Integer, SoundSourceSound>();

	public SoundSourcePlayer(SoundSource soundSource) {
		super(soundSource);
	}

	/**
	 * Aquire soundFactory.
	 */
	protected void openImpl() {
		SoundSource soundSource = getElement();

		removeProblem(errorDevice);
		removeProblem(errorType);
		removeProblem(errorTypeParameter);

		if (soundSource.getDevice() != null) {
			try {
				factory = SoundFactory.instance(soundSource.getDevice(),
						soundSource.getType());
				factory.init(soundSource.getBank(), soundSource.getSamples());
			} catch (MidiUnavailableException ex) {
				addProblem(errorDevice.value(soundSource.getDevice()));
			} catch (SoundFactoryParameterException ex) {
				addProblem(errorTypeParameter.value(ex.getValue()));
			} catch (SoundFactoryException ex) {
				addProblem(errorType.value(soundSource.getType()));
			}
		}
	}

	/**
	 * Release soundFactory.
	 */
	protected void closeImpl() {
		if (factory != null) {
			factory.close();
			factory = null;

			sounds.clear();
		}
	}

	public void elementChanged(OrganEvent event) {
		SoundSource soundSource = getElement();

		if (soundSource.getDevice() == null && getWarnDevice()) {
			removeProblem(errorDevice);
			addProblem(warningDevice.value(null));
		} else {
			removeProblem(warningDevice);
		}
	}

	/**
	 * Create a sound from the aquired soundFactory.
	 * 
	 * @param program
	 *            the program to use for the sound
	 */
	public Sound createSound(int program) {

		SoundSource soundSource = getElement();

		Sound sound = null;
		if (factory != null) {
			SoundSourceSound soundSourceSound = sounds
					.get(new Integer(program));
			if (soundSourceSound == null) {
				sound = factory.createSound();
				if (sound != null) {
					soundSourceSound = new SoundSourceSound(program, sound);
				}
			}

			if (soundSourceSound != null) {
				soundSourceSound.init();
			}
			sound = soundSourceSound;

			if (sound != null && soundSource.getDelay() != 0) {
				sound = new DelayedSound(sound, soundSource.getDelay());
			}
		}
		return sound;
	}

	private class SoundSourceSound extends SoundWrapper {

		private int program;

		private int initCount = 0;

		public SoundSourceSound(int program, Sound sound) {
			super(sound);

			this.program = program;

			sounds.put(new Integer(program), this);
		}

		public void init() {
			initCount++;
		}

		public void stop() {
			initCount--;

			if (initCount == 0) {
				super.stop();

				sounds.remove(new Integer(program));
			}
		}
	}
}