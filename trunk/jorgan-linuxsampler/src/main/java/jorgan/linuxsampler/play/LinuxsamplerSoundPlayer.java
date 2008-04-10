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
package jorgan.linuxsampler.play;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import jorgan.disposition.event.OrganEvent;
import jorgan.linuxsampler.Linuxsampler;
import jorgan.linuxsampler.disposition.LinuxsamplerSound;
import jorgan.play.GenericSoundPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class LinuxsamplerSoundPlayer extends
		GenericSoundPlayer<LinuxsamplerSound> {

	private Linuxsampler linuxsampler;

	public LinuxsamplerSoundPlayer(LinuxsamplerSound sound) {
		super(sound);
	}

	@Override
	protected void setUp() {
		LinuxsamplerSound sound = getElement();

		removeProblem(Severity.ERROR, "host");
		removeProblem(Severity.ERROR, "lscp");

		if (sound.getHost() != null) {
			try {
				linuxsampler = new Linuxsampler(sound.getHost(), sound
						.getPort());
			} catch (UnknownHostException e) {
				addProblem(Severity.ERROR, "host", "unkownHost", sound
						.getHost());
				return;
			} catch (SocketTimeoutException e) {
				addProblem(Severity.ERROR, "host", "hostTimeout");
				return;
			} catch (IOException e) {
				addProblem(Severity.ERROR, "host", "hostUnavailable");
				return;
			}

			if (sound.getLscp() != null) {
				Reader reader;

				try {
					reader = new FileReader(sound.getLscp());
				} catch (FileNotFoundException e1) {
					addProblem(Severity.ERROR, "lscp", "lscpNotFound", sound
							.getLscp());
					return;
				}

				try {
					linuxsampler.send(reader);
				} catch (IOException e) {
					addProblem(Severity.ERROR, "host", "hostUnavailable");
					return;
				}
			}
		}
	}

	@Override
	protected void tearDown() {
		if (linuxsampler != null) {
			try {
				if (getElement().getReset()) {
					linuxsampler.sendReset();
				}
				linuxsampler.close();
			} catch (IOException ignore) {
			}

			linuxsampler = null;
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		LinuxsamplerSound sound = getElement();
		if (sound.getHost() == null) {
			addProblem(Severity.WARNING, "host", "noHost", sound.getHost());
		} else {
			removeProblem(Severity.WARNING, "host");
		}
	}
}