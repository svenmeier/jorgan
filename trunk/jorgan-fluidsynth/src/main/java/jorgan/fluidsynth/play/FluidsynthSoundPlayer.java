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

		configureTunings();
		configureReverb();
		configureChorus();
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

		synth.setGain(sound.getGain() * 10.0f);
		synth.setInterpolate(sound.getInterpolate().number());
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

	public void configureTunings() {
		if (synth != null) {
			FluidsynthSound sound = getElement();
			int tuningProgram = 0;
			for (Tuning tuning : sound.getTunings()) {
				synth.setTuning(0, tuningProgram, tuning.getName(), tuning
						.getDerivations());
				tuningProgram++;
			}
		}
	}

	public void configureReverb() {
		if (synth != null) {
			FluidsynthSound sound = getElement();

			boolean on = false;
			double room = 0.2d; // 0.0 - 1.2
			double damping = 0.0d; // 0.0 - 1.0
			double width = 0.5d; // 0.0 - 100.0
			double level = 0.9d; // 0.0 - 1.0

			for (Reverb reverb : sound.getReferenced(Reverb.class)) {
				on = true;

				switch (reverb.getParameter()) {
				case ROOM:
					room = reverb.getValue() * 1.2d;
					break;
				case DAMPING:
					damping = reverb.getValue() * 1.0d;
					break;
				case WIDTH:
					width = reverb.getValue() * 100d;
					break;
				case LEVEL:
					level = reverb.getValue() * 1.0d;
					break;
				}
			}

			if (on) {
				synth.setReverbOn(true);
				synth.setReverb(room, damping, width, level);
			} else {
				synth.setReverbOn(false);
			}
		}
	}

	public void configureChorus() {
		if (synth != null) {
			FluidsynthSound sound = getElement();

			boolean on = false;
			int nr = 3; // 0 - 99
			double level = 2.0d; // 0.0 - 1.0
			double speed = 0.30d; // 0.30 - 5.0
			double depth = 8.0d; // 0.0 - 21.0

			for (Chorus chorus : sound.getReferenced(Chorus.class)) {
				on = true;

				switch (chorus.getParameter()) {
				case NR:
					nr = Math.round(chorus.getValue() * 99);
					break;
				case LEVEL:
					level = chorus.getValue() * 1.0d;
					break;
				case SPEED:
					speed = 0.30d + (chorus.getValue() * (5.0d - 0.30d));
					break;
				case DEPTH:
					depth = chorus.getValue() * 21.0d;
					break;
				}
			}

			if (on) {
				synth.setChorusOn(true);
				synth.setChorus(nr, level, speed, depth, 0);
			} else {
				synth.setChorusOn(false);
			}
		}
	}

	/**
	 * FIXME in the following situation this method will block infinitely:
	 * <ul>
	 * <li>Jack is running</li>
	 * <li>a disposition with audioDriver 'alsa' is opened</li>
	 * <li>fluidsynth's audio thread blocks on writing to pcm</li>
	 * <li>the disposition is closed, destroying the synth never returns!</li>
	 * </ul>
	 * Under certain circumstances (the audioDriver is edited instead of loading
	 * a disposition) the system might lock-up completely.
	 */
	private void destroySynth() {
		if (synth != null) {
			synth.destroy();
			synth = null;

			clone = null;
		}
	}
}
