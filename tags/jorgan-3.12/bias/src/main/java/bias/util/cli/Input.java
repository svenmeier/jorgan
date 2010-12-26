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

/**
 * An option that offers/requires input.
 */
public abstract class Input extends Option {

	private boolean required;

	/**
	 * Constructor.
	 * 
	 * @param required
	 *            it input required
	 */
	public Input(char name, boolean required) {
		super(name);

		this.required = required;
	}

	@Override
	protected int parsed(String arg, int to) throws CLIException {
		if (to == arg.length()) {
			if (required) {
				throw new CLIException("option '" + getDisplayName()
						+ "' requires an argument");
			}
		}

		onInput(arg.substring(to));
		to = arg.length();

		return to;
	}

	@Override
	public String getLongSyntax() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(super.getLongSyntax());

		if (required) {
			buffer.append("=");
			buffer.append(getArgumentDescription());
		} else {
			buffer.append("[=");
			buffer.append(getArgumentDescription());
			buffer.append("]");
		}

		return buffer.toString();
	}

	protected String getArgumentDescription() {
		return "arg";
	}

	protected abstract void onInput(String input) throws CLIException;
}