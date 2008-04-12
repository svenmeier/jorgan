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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import jorgan.disposition.event.OrganEvent;
import jorgan.linuxsampler.ConversationException;
import jorgan.linuxsampler.Linuxsampler;
import jorgan.linuxsampler.Linuxsampler.Conversation;
import jorgan.linuxsampler.disposition.LinuxsamplerSound;
import jorgan.linuxsampler.io.FileWatcher;
import jorgan.play.GenericSoundPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class LinuxsamplerSoundPlayer extends
		GenericSoundPlayer<LinuxsamplerSound> {

	private Linuxsampler linuxsampler;
	
	private FileWatcher watcher;
	
	public LinuxsamplerSoundPlayer(LinuxsamplerSound sound) {
		super(sound);
	}

	@Override
	protected synchronized void setUp() {
		LinuxsamplerSound sound = getElement();

		removeProblem(Severity.ERROR, "host");
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
		}

		loadLscp();
	}

	private synchronized void loadLscp() {
		removeProblem(Severity.WARNING, "lscp");
		removeProblem(Severity.ERROR, "lscp");

		if (linuxsampler == null) {
			return;
		}
		
		LinuxsamplerSound sound = getElement();
		if (sound.getLscp() != null) {
			File file = new File(sound.getLscp());

			Reader reader;
			try {
				reader = new FileReader(file);				
			} catch (FileNotFoundException e1) {
				addProblem(Severity.ERROR, "lscp", "lscpNotFound", sound
						.getLscp());
				return;
			}

			watcher = new FileWatcher(file) {
				@Override
				protected void onChange(File file) {
					watcher.cancel();
					loadLscp();
				}
			};
			
			try {
				Conversation conversation = linuxsampler.conversation();

				conversation.send(reader);
				
				if (conversation.hasWarnings()) {
					addProblem(Severity.WARNING, "lscp", "lscpWarnings",
							conversation.getWarnings());
				}
			} catch (IOException e) {
				addProblem(Severity.ERROR, "host", "hostUnavailable");
				return;
			} catch (ConversationException e) {
				addProblem(Severity.ERROR, "lscp", "lscpError", e
						.getMessage());
				return;
			} finally {
				try {
					reader.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	protected synchronized void tearDown() {
		if (linuxsampler != null) {
			try {
				linuxsampler.close();
			} catch (IOException ignore) {
			}
			linuxsampler = null;
		}
		
		if (watcher != null) {
			watcher.cancel();
			watcher = null;
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