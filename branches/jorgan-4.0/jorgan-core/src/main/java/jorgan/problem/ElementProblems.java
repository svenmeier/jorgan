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
package jorgan.problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jorgan.disposition.Element;

/**
 * The problems.
 */
public class ElementProblems {

	/**
	 * The problems.
	 */
	private List<Problem> problems = new ArrayList<Problem>();

	/**
	 * All registered listeners for problems.
	 */
	private List<ProblemListener> listeners = new ArrayList<ProblemListener>();

	private int errorCount = 0;

	private int warningCount = 0;

	public void addProblem(Problem problem) {
		if (problem == null) {
			throw new IllegalArgumentException("problem must not be null");
		}
		if (!problems.contains(problem)) {
			problems.add(problem);
			if (problem.getSeverity() == Severity.WARNING) {
				warningCount++;
			}
			if (problem.getSeverity() == Severity.ERROR) {
				errorCount++;
			}
			fireProblemAdded(problem);
		}
	}

	public void removeProblem(Problem problem) {
		if (problem == null) {
			throw new IllegalArgumentException("problem must not be null");
		}
		if (problems.contains(problem)) {
			problems.remove(problem);
			if (problem.getSeverity() == Severity.WARNING) {
				warningCount--;
			}
			if (problem.getSeverity() == Severity.ERROR) {
				errorCount--;
			}
			fireProblemRemoved(problem);
		}
	}

	protected void fireProblemAdded(Problem problem) {
		if (listeners != null) {
			for (int l = 0; l < listeners.size(); l++) {
				ProblemListener listener = listeners.get(l);
				listener.problemAdded(problem);
			}
		}
	}

	protected void fireProblemRemoved(Problem problem) {
		if (listeners != null) {
			for (int l = 0; l < listeners.size(); l++) {
				ProblemListener listener = listeners.get(l);
				listener.problemRemoved(problem);
			}
		}
	}

	public boolean hasWarnings() {
		return warningCount > 0;
	}

	public boolean hasErrors() {
		return errorCount > 0;
	}

	public boolean hasErrors(Element element) {
		for (Problem problem : problems) {
			if (problem.getSeverity() == Severity.ERROR
					&& problem.getElement() == element) {
				return true;
			}
		}
		return false;
	}

	public boolean hasWarnings(Element element) {
		for (Problem problem : problems) {
			if (problem.getSeverity() == Severity.WARNING
					&& problem.getElement() == element) {
				return true;
			}
		}
		return false;
	}

	public List<Problem> getProblems(Element element) {
		List<Problem> filter = new ArrayList<Problem>();

		for (Problem problem : problems) {
			if (problem.getElement() == element) {
				filter.add(problem);
			}
		}

		return filter;
	}

	public List<Problem> getProblems() {
		return Collections.unmodifiableList(problems);
	}

	public void addListener(ProblemListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ProblemListener listener) {
		if (!listeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
	}

	public void removeProblems(Element element) {
		for (Problem problem : new ArrayList<Problem>(problems)) {
			if (problem.getElement() == element) {
				removeProblem(problem);
			}
		}
	}
}