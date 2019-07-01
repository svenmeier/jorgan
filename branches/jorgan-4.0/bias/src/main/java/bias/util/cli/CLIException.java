/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util.cli;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class CLIException extends Exception {

	public CLIException() {
		super();
	}

	public CLIException(String message) {
		super(message);
	}
	
	public void write() {
		try {
			write(new OutputStreamWriter(System.out));
		} catch (IOException ex) {
			throw new Error();
		}
	}
	
	public void write(Writer writer) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
		bufferedWriter.write(getMessage());
		bufferedWriter.flush();
	}
}
