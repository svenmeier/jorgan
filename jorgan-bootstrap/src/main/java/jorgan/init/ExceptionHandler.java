package jorgan.init;

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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class ExceptionHandler implements UncaughtExceptionHandler {

	private Logger logger;

	public ExceptionHandler(Logger logger) {
		this.logger = logger;
		
		Thread.currentThread().setUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (e instanceof ThreadDeath) {
			return;
		}
		logger.log(Level.WARNING, "uncaught exception", e);
	}
}