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
package jorgan.fluidsynth.play;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.Chorus;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.disposition.Reverb;
import jorgan.fluidsynth.disposition.Tuning;
import jorgan.midi.MessageUtils;
import jorgan.play.SoundPlayer;
import jorgan.problem.Severity;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class FluidsynthSoundPlayer extends SoundPlayer<FluidsynthSound> {

	private FluidsynthSound clone;

	private Fluidsynth synth;

	public FluidsynthSoundPlayer(FluidsynthSound sound) {
		super(sound);
	}

	public void update() {
		FluidsynthSound sound = getElement();

		if (sound.getSoundfont() == null) {
			addProblem(Severity.WARNING, "soundfont", "noSoundfont", sound
					.getSoundfont());
		} else {
			removeProblem(Severity.WARNING, "soundfont");
		}

		if (synth == null) {
			createSynth();
		} else {
			if (!clone.equals(sound)) {
				destroySynth();
				createSynth();
			}
		}

		configureSynth();
	}

	@Override
	protected void destroy() {
		destroySynth();
	}

	@Override
	protected int getChannelCount() {
		FluidsynthSound sound = getElement();

		return sound.getChannels();
	}

	@Override
	protected void send(int channel, byte[] datas)
			throws InvalidMidiDataException {

		if (datas.length != 3 || !MessageUtils.isChannelStatus(datas[0])) {
			throw new InvalidMidiDataException("short messages supported only");
		}

		if (synth != null) {
			int command = datas[0] & 0xf0;
			int data1 = datas[1] & 0xff;
			int data2 = datas[2] & 0xff;

			fireSent(MessageUtils.createMessage(command | (channel & 0x0f),
					data1, data2));

			synth.send(channel, command, data1, data2);
		}
	}

	private void createSynth() {
		FluidsynthSound sound = getElement();

		removeProblem(Severity.ERROR, "audioDriver");
		try {
			synth = new Fluidsynth(name(sound.getName()), sound.getCores(),
					sound.getChannels(), sound.getPolyphony(), sound
							.getSampleRate(), sound.getAudioDriver(), sound
							.getAudioDevice(), sound.getAudioBuffers(), sound
							.getAudioBufferSize());

			clone = (FluidsynthSound) sound.clone();
		} catch (IOException e) {
			addProblem(Severity.ERROR, "audioDriver", "create");
			return;
		} catch (Error fluidsynthFailure) {
			addProblem(Severity.ERROR, "audioDriver", "fluidsynthFailure");
			return;
		}

		removeProblem(Severity.ERROR, "soundfont");
		if (sound.getSoundfont() != null) {
			try {
				synth.soundFontLoad(resolve(sound.getSoundfont()));
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "soundfont", "soundfontLoad", sound
						.getSoundfont());
			}
		}
	}

	private String name(String name) {
		StringBuffer buffer = new StringBuffer("jOrgan");

		name = name.trim();
		if (name.length() > 0) {
			buffer.append("-");
			buffer.append(name);
		}
		return buffer.toString();
	}

	private void configureSynth() {
		if (synth != null) {
			FluidsynthSound sound = getElement();

			synth.setGain((float) (sound.getGain() * 2));
			synth.setInterpolate(sound.getInterpolate().number());

			Reverb reverb = sound.getReverb();
			if (reverb == null) {
				synth.setReverbOn(false);
			} else {
				synth.setReverbOn(true);
				synth.setReverb(reverb.getRoom(), reverb.getDamping(), reverb
						.getWidth(), reverb.getLevel());
			}

			Chorus chorus = sound.getChorus();
			if (chorus == null) {
				synth.setChorusOn(false);
			} else {
				synth.setChorusOn(true);
				synth.setChorus((int) (chorus.getNr() * 100),
						chorus.getLevel() * 10, chorus.getSpeed() * 5, chorus
								.getDepth() * 10, chorus.getType().ordinal());
			}

			int tuningProgram = 0;
			for (Tuning tuning : sound.getTunings()) {
				synth.setTuning(0, tuningProgram, tuning.getName(), tuning
						.getDerivations());
				tuningProgram++;
			}
		}
	}

	/**
	 * FIXME in the following situation this method will block infinitely:
	 * <ul>
	 * <li>Jack is running</li>
	 * <li>a disposition with audioDriver 'jack' is opened</li>
	 * <li>the disposition is closed, destroying the synth</li>
	 * <li>a disposition with audioDriver 'alsa' is opened</li>
	 * <li>the disposition is closed, destroying the synth never returns!</li>
	 * </ul>
	 * Under certain circumstances (the audioDriver is edited instead of loading
	 * a different disposition) the system might lock-up completely.
	 */
	private void destroySynth() {
		if (synth != null) {
			System.out.println(">>> destroy " + synth.getAudioDriver() + "{");
			synth.destroy();
			System.out.println(">>> }");
			synth = null;

			clone = null;
		}
	}
}
