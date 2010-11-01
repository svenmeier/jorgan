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

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.session.OrganSession;
import jorgan.session.spi.SessionProvider;

public class ElementProblemsSessionProvider implements SessionProvider {

	/**
	 * {@link ElementProblems} is optional. 
	 */
	public void init(OrganSession session) {
	}
	
	public Object create(OrganSession session, Class<?> clazz) {
		if (clazz == ElementProblems.class) {
			final ElementProblems problems = new ElementProblems();
			
			session.getOrgan().addOrganListener(new OrganAdapter() {
				@Override
				public void elementRemoved(Element element) {
					problems.removeProblems(element);
				}
			});
			
			return problems;
		}
		return null;
	}
}