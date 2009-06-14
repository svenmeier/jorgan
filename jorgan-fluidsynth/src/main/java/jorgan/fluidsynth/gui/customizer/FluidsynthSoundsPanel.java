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
package jorgan.fluidsynth.gui.customizer;

import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

public class FluidsynthSoundsPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthSoundsPanel.class);

	private JSpinner audioBuffersSpinner;

	private JSpinner audioBufferSizeSpinner;

	private Set<FluidsynthSound> sounds;

	public FluidsynthSoundsPanel(Set<FluidsynthSound> sounds) {
		config.read(this);

		this.sounds = sounds;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.group(config.get("audioBuffer").read(new JLabel()));

		column.term(config.get("audioBuffers").read(new JLabel()));
		audioBuffersSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 16, 1));
		column.definition(audioBuffersSpinner);

		column.term(config.get("audioBufferSize").read(new JLabel()));
		audioBufferSizeSpinner = new JSpinner(new SpinnerNumberModel(64, 64,
				8192, 16));
		column.definition(audioBufferSizeSpinner);

		init();
	}

	private void init() {
		int audioBuffers = -1;
		int audioBufferSize = -1;

		for (FluidsynthSound sound : sounds) {
			audioBuffers = Math.max(audioBuffers, sound.getAudioBuffers());
			audioBufferSize = Math
					.max(audioBuffers, sound.getAudioBufferSize());
		}

		if (audioBuffers == -1) {
			audioBuffersSpinner.setEnabled(false);
			audioBufferSizeSpinner.setEnabled(false);
		} else {
			audioBuffersSpinner.setValue(audioBuffers);
			audioBufferSizeSpinner.setValue(audioBufferSize);
		}
	}

	public void apply() {
		if (audioBufferSizeSpinner.isEnabled()) {
			int audioBuffers = (Integer) audioBuffersSpinner.getValue();
			int audioBufferSize = (Integer) audioBufferSizeSpinner.getValue();

			for (FluidsynthSound sound : sounds) {
				sound.setAudioBuffers(audioBuffers);
				sound.setAudioBufferSize(audioBufferSize);
			}
		}
	}
}