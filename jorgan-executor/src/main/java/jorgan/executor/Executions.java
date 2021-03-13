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
package jorgan.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import bias.Configuration;
import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.executor.disposition.Executor;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import jorgan.session.OrganSession;

public class Executions extends OrganAdapter {

	private static final Configuration config = Configuration.getRoot()
			.get(Executions.class);

	private static final Logger log = Logger
			.getLogger(Executions.class.getName());

	private OrganSession session;

	private ElementProblems problems;

	private boolean allowed = false;

	private boolean poll = true;

	public Executions(OrganSession session) {
		config.read(this);

		this.session = session;
		session.getOrgan().addOrganListener(this);

		problems = session.lookup(ElementProblems.class);
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	@Override
	public void propertyChanged(Element element, String name) {
		if (Executor.class.isInstance(element)) {
			Executor executor = ((Executor) element);

			if ("engaged".equals(name) && !executor.isEngaged()) {
				execute(session, executor);
			} else if ("command".equals(name)) {
				removeProblem(executor);
			}
		}
	}

	private void execute(OrganSession session, Executor executor) {
		if (!allowed) {
			return;
		}

		removeProblem(executor);

		try {
			if (executor.getSave()) {
				session.save();
			}

			String command = executor.getCommand();
			if (command != null) {
				File file = session.resolve(command);

				Process process = Runtime.getRuntime().exec(
						file.getCanonicalPath(), null,
						session.getFile().getParentFile().getCanonicalFile());

				if (poll) {
					new Poller(process);
				}
			}
		} catch (IOException e) {
			addProblem(executor, e);
		}
	}

	private void addProblem(Executor executor, IOException e) {
		problems.addProblem(new Problem(Severity.ERROR, executor, "command",
				getMessage(e)));
	}

	private void removeProblem(Executor executor) {
		problems.removeProblem(
				new Problem(Severity.ERROR, executor, "command", null));
	}

	private String getMessage(IOException e) {
		while (e.getCause() != null && e.getCause() != e
				&& e.getCause() instanceof IOException) {
			e = (IOException) e.getCause();
		}

		String message = e.getMessage();
		message = message.replaceFirst(".*Exception:?\\s*", "");
		return message;
	}

	public static class Poller implements Runnable {

		private Process process;

		private BufferedReader reader;

		public Poller(Process process) {
			this.process = process;

			this.reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			new Thread(this).start();
		}

		@Override
		public void run() {
			try {
				while (process.isAlive()) {
					read();
					Thread.sleep(500);
				}

				read();

				log("finished");
			} catch (Exception ex) {
				log(ex.getMessage());
			}
		}

		private void read() throws IOException {
			String line = reader.readLine();
			if (line == null) {
				return;
			}
			log(line);
		}

		private void log(String text) {
			log.info(String.format("execution %s: %s", process.pid(), text));
		}
	}
}