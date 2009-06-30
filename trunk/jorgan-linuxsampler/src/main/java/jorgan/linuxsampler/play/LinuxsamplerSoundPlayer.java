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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import jorgan.linuxsampler.ConversationException;
import jorgan.linuxsampler.Linuxsampler;
import jorgan.linuxsampler.Linuxsampler.Conversation;
import jorgan.linuxsampler.disposition.LinuxsamplerSound;
import jorgan.linuxsampler.io.FileWatcher;
import jorgan.play.GenericSoundPlayer;
import jorgan.problem.Severity;
import jorgan.util.IOUtils;
import jorgan.util.Null;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class LinuxsamplerSoundPlayer extends
		GenericSoundPlayer<LinuxsamplerSound> {

	private Linuxsampler linuxsampler;

	private LinuxsamplerSound clone;

	private FileWatcher watcher;

	public LinuxsamplerSoundPlayer(LinuxsamplerSound sound) {
		super(sound);
	}

	@Override
	protected synchronized void destroy() {
		destroyLinuxsampler();
	}

	@Override
	public void update() {
		super.update();

		LinuxsamplerSound sound = getElement();

		if (sound.getHost() == null) {
			addProblem(Severity.WARNING, "host", "noHost", sound.getHost());
		} else {
			removeProblem(Severity.WARNING, "host");
		}

		if (linuxsampler == null) {
			createLinuxsampler();
		} else {
			if (!Null.safeEquals(clone.getOutput(), sound.getOutput())
					|| !Null.safeEquals(clone.getPort(), sound.getPort())
					|| !Null.safeEquals(clone.getLscp(), sound.getLscp())) {
				destroyLinuxsampler();
				createLinuxsampler();
			}
		}
	}

	private void createLinuxsampler() {
		LinuxsamplerSound sound = getElement();

		removeProblem(Severity.ERROR, "host");
		if (sound.getHost() != null) {
			try {
				linuxsampler = new Linuxsampler(sound.getHost(), sound
						.getPort());

				clone = (LinuxsamplerSound) sound.clone();
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

			loadLscp();
		}
	}

	private synchronized void loadLscp() {
		removeProblem(Severity.WARNING, "lscp");
		removeProblem(Severity.ERROR, "lscp");

		LinuxsamplerSound sound = getElement();
		if (sound.getLscp() != null) {
			Reader reader;
			try {
				File file = resolve(sound.getLscp());

				reader = new FileReader(file);

				watcher = new FileWatcher(file) {
					@Override
					protected void onChange(File file) {
						watcher.cancel();
						loadLscp();
					}
				};
			} catch (IOException e1) {
				addProblem(Severity.ERROR, "lscp", "lscpNotFound", sound
						.getLscp());
				return;
			}

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
				addProblem(Severity.ERROR, "lscp", "lscpError", e.getMessage());
				return;
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}

	private void destroyLinuxsampler() {
		if (linuxsampler != null) {
			try {
				linuxsampler.close();
			} catch (IOException ignore) {
			}
			linuxsampler = null;

			clone = null;

			if (watcher != null) {
				watcher.cancel();
				watcher = null;
			}
		}
	}
}