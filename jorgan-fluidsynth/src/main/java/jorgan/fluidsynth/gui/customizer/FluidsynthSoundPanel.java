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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.disposition.Elements;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

public class FluidsynthSoundPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthSoundPanel.class);

	private JSpinner audioBuffersSpinner;

	private JSpinner audioBufferSizeSpinner;

	private FluidsynthSound sound;

	public FluidsynthSoundPanel(FluidsynthSound sound) {
		config.read(this);

		this.sound = sound;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.group(new JLabel(Elements.getDisplayName(sound)));

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
		audioBuffersSpinner.setValue(sound.getAudioBuffers());
		audioBufferSizeSpinner.setValue(sound.getAudioBufferSize());
	}

	public void apply() {
		sound.setAudioBuffers((Integer) audioBuffersSpinner.getValue());
		sound.setAudioBufferSize((Integer) audioBufferSizeSpinner.getValue());
	}
}