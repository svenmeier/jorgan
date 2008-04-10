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
package jorgan.linuxsampler.disposition;

import jorgan.disposition.GenericSound;
import jorgan.util.Null;

public class LinuxsamplerSound extends GenericSound {

	private String host = "localhost";

	private int port = 8888;

	private String lscp;
	
	private boolean reset;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if (!Null.safeEquals(this.host, host)) {
			this.host = host;

			fireChanged(true);
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if (this.port != port) {
			this.port = port;

			fireChanged(true);
		}
	}

	public String getLscp() {
		return lscp;
	}

	public void setLscp(String lscp) {
		if (!Null.safeEquals(this.lscp, lscp)) {
			this.lscp = lscp;

			fireChanged(true);
		}
	}

	public boolean getReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}
}
