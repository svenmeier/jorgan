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

import java.io.File;
import java.io.IOException;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.executor.disposition.Executor;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import jorgan.session.OrganSession;
import bias.Configuration;

public class Executions extends OrganAdapter {

	private static final Configuration config = Configuration.getRoot().get(
			Executions.class);

	private OrganSession session;

	private ElementProblems problems;

	private boolean allowed = false;

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

			if ("active".equals(name)) {
				if (!executor.isActive() && allowed) {
					execute(session, executor);
				}
			} else if ("command".equals(name)) {
				removeProblem(executor);
			}
		}
	}

	private void execute(OrganSession session, Executor executor) {
		removeProblem(executor);

		try {
			if (executor.getSave()) {
				session.save();
			}

			String command = executor.getCommand();
			if (command != null) {
				File file = session.resolve(command);
				Runtime.getRuntime().exec(file.getCanonicalPath(), null,
						session.getFile().getParentFile().getCanonicalFile());
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
		problems.removeProblem(new Problem(Severity.ERROR, executor, "command",
				null));
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
}