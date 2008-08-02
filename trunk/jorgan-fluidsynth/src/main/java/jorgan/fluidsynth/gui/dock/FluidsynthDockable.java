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
package jorgan.fluidsynth.gui.dock;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.fluidsynth.disposition.Chorus;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.disposition.Reverb;
import jorgan.fluidsynth.disposition.Chorus.Type;
import jorgan.gui.dock.OrganDockable;
import jorgan.session.OrganSession;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

public class FluidsynthDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthDockable.class);

	private JPanel panel;

	private OrganSession session;

	private EventHandler eventHandler = new EventHandler();

	private JSpinner gainSpinner;

	private JCheckBox chorusCheckBox;

	private JSpinner chorusNrSpinner;

	private JSpinner chorusLevelSpinner;

	private JSpinner chorusSpeedSpinner;

	private JSpinner chorusDepthSpinner;

	private JComboBox chorusTypeComboBox;

	private JCheckBox reverbCheckBox;

	private JSpinner reverbRoomSpinner;

	private JSpinner reverbDampingSpinner;

	private JSpinner reverbWidthSpinner;

	private JSpinner reverbLevelSpinner;

	private boolean readWrite = false;

	public FluidsynthDockable() {
		config.read(this);

		panel = new JPanel();
		panel.setVisible(false);

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.term(config.get("gain").read(new JLabel()));
		gainSpinner = createSpinner();
		column.definition(gainSpinner);

		chorusCheckBox = createCheckBox();
		column.group(config.get("chorus").read(chorusCheckBox));

		column.term(config.get("chorus/nr").read(new JLabel()));
		chorusNrSpinner = createSpinner();
		column.definition(chorusNrSpinner);

		column.term(config.get("chorus/level").read(new JLabel()));
		chorusLevelSpinner = createSpinner();
		column.definition(chorusLevelSpinner);

		column.term(config.get("chorus/speed").read(new JLabel()));
		chorusSpeedSpinner = createSpinner();
		column.definition(chorusSpeedSpinner);

		column.term(config.get("chorus/depth").read(new JLabel()));
		chorusDepthSpinner = createSpinner();
		column.definition(chorusDepthSpinner);

		column.term(config.get("chorus/type").read(new JLabel()));
		chorusTypeComboBox = new JComboBox(new Object[] { Type.SINE,
				Type.TRIANGLE });
		chorusTypeComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				write();
			}
		});
		column.definition(chorusTypeComboBox);

		reverbCheckBox = createCheckBox();
		column.group(config.get("reverb").read(reverbCheckBox));

		column.term(config.get("reverb/room").read(new JLabel()));
		reverbRoomSpinner = createSpinner();
		column.definition(reverbRoomSpinner);

		column.term(config.get("reverb/damping").read(new JLabel()));
		reverbDampingSpinner = createSpinner();
		column.definition(reverbDampingSpinner);

		column.term(config.get("reverb/width").read(new JLabel()));
		reverbWidthSpinner = createSpinner();
		column.definition(reverbWidthSpinner);

		column.term(config.get("reverb/level").read(new JLabel()));
		reverbLevelSpinner = createSpinner();
		column.definition(reverbLevelSpinner);

		setContent(new JScrollPane(panel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	}

	private JCheckBox createCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				write();
			}
		});
		return checkBox;
	}

	private JSpinner createSpinner() {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				write();
			}
		});
		return spinner;
	}

	@Override
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.removeOrganListener(eventHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(eventHandler);
		}

		read();
	}

	private FluidsynthSound findSound() {
		if (session != null) {
			Set<FluidsynthSound> sounds = this.session.getOrgan()
					.getElements(FluidsynthSound.class);
			if (!sounds.isEmpty()) {
				return sounds.iterator().next();
			}
		}
		return null;
	}
	
	private void read() {
		if (!readWrite) {
			readWrite = true;

			FluidsynthSound sound = findSound();
			if (sound != null) {
				read(sound);
			}
			panel.setVisible(sound != null);
			readWrite = false;
		}
	}

	private void read(FluidsynthSound sound) {
		setPercentage(gainSpinner, sound.getGain());

		readChorus(sound);
		readReverb(sound);
	}

	private void readReverb(FluidsynthSound sound) {
		Reverb reverb = sound.getReverb();

		reverbCheckBox.setSelected(reverb != null);
		reverbRoomSpinner.setEnabled(reverb != null);
		reverbDampingSpinner.setEnabled(reverb != null);
		reverbWidthSpinner.setEnabled(reverb != null);
		reverbLevelSpinner.setEnabled(reverb != null);

		if (reverb == null) {
			reverb = new Reverb();
		}
		setPercentage(reverbRoomSpinner, reverb.getRoom());
		setPercentage(reverbDampingSpinner, reverb.getDamping());
		setPercentage(reverbWidthSpinner, reverb.getWidth());
		setPercentage(reverbLevelSpinner, reverb.getLevel());
	}

	private void readChorus(FluidsynthSound sound) {
		Chorus chorus = sound.getChorus();

		chorusCheckBox.setSelected(chorus != null);
		chorusNrSpinner.setEnabled(chorus != null);
		chorusLevelSpinner.setEnabled(chorus != null);
		chorusSpeedSpinner.setEnabled(chorus != null);
		chorusDepthSpinner.setEnabled(chorus != null);
		chorusTypeComboBox.setEnabled(chorus != null);

		if (chorus == null) {
			chorus = new Chorus();
		}
		setPercentage(chorusNrSpinner, chorus.getNr());
		setPercentage(chorusLevelSpinner, chorus.getLevel());
		setPercentage(chorusSpeedSpinner, chorus.getSpeed());
		setPercentage(chorusDepthSpinner, chorus.getDepth());
		chorusTypeComboBox.setSelectedItem(chorus.getType());
	}

	private void write() {
		if (!readWrite) {
			readWrite = true;

			Set<FluidsynthSound> sounds = this.session.getOrgan().getElements(
					FluidsynthSound.class);
			for (FluidsynthSound sound : sounds) {
				write(sound);
				read(sound);
			}

			readWrite = false;
		}
	}

	private void write(FluidsynthSound sound) {
		sound.setGain(getPercentage(gainSpinner));

		writeReverb(sound);
		writeChorus(sound);
	}

	private void writeReverb(FluidsynthSound sound) {
		if (reverbCheckBox.isSelected()) {
			Reverb reverb = new Reverb();

			reverb.setRoom(getPercentage(reverbRoomSpinner));
			reverb.setDamping(getPercentage(reverbDampingSpinner));
			reverb.setWidth(getPercentage(reverbWidthSpinner));
			reverb.setLevel(getPercentage(reverbLevelSpinner));

			sound.setReverb(reverb);
		} else {
			sound.setReverb(null);
		}
	}

	private void writeChorus(FluidsynthSound sound) {
		if (chorusCheckBox.isSelected()) {
			Chorus chorus = new Chorus();

			chorus.setNr(getPercentage(chorusNrSpinner));
			chorus.setLevel(getPercentage(chorusLevelSpinner));
			chorus.setSpeed(getPercentage(chorusSpeedSpinner));
			chorus.setDepth(getPercentage(chorusDepthSpinner));
			chorus.setType((Type) chorusTypeComboBox.getSelectedItem());

			sound.setChorus(chorus);
		} else {
			sound.setChorus(null);
		}
	}

	private void setPercentage(JSpinner spinner, double value) {
		spinner.setValue((int)Math.round(value * 100));
	}

	private double getPercentage(JSpinner spinner) {
		return ((Number) spinner.getValue()).doubleValue() / 100.0d;
	}

	private class EventHandler implements OrganListener {

		public void added(OrganEvent event) {
			if (event.getElement() instanceof FluidsynthSound) {
				read();
			}
		}

		public void changed(OrganEvent event) {
			if (event.getElement() instanceof FluidsynthSound) {
				read();
			}
		}

		public void removed(OrganEvent event) {
			if (event.getElement() instanceof FluidsynthSound) {
				read();
			}
		}
	}
}
