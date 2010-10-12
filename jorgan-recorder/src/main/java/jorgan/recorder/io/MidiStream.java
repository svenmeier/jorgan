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
package jorgan.recorder.io;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import bias.Configuration;

/**
 * A {@link Sequence} streamer.
 */
public class MidiStream {

	private static Configuration config = Configuration.getRoot().get(
			MidiStream.class);

	private File recentDirectory;

	public MidiStream() {
		config.read(this);
	}

	public Sequence read(File file) throws IOException,
			InvalidMidiDataException {
		Sequence sequence = MidiSystem.getSequence(file);

		changeRecentDirectory(file);

		return sequence;
	}

	public void write(Sequence sequence, File file) throws IOException {

		MidiSystem.write(sequence, 1, file);

		changeRecentDirectory(file);
	}

	private void changeRecentDirectory(File file) {
		try {
			setRecentDirectory(file.getCanonicalFile().getParentFile());
		} catch (IOException ignore) {
		}
		
		config.write(this);
	}

	public File getRecentDirectory() {
		return recentDirectory;
	}

	public void setRecentDirectory(File recentDirectory) {
		this.recentDirectory = recentDirectory;
	}
}