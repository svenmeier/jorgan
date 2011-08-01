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
import java.util.ArrayList;
import java.util.Collection;

/**
 * A parser of program arguments.
 */
public class ArgsParser {

	private String programSyntax;

	private String operandsSyntax;

	private Collection<Option> options;

	public ArgsParser(String programSyntax) {
		this(programSyntax, "[operand]...");
	}

	public ArgsParser(String programSyntax, String operandsSyntax) {
		this(programSyntax, operandsSyntax, new ArrayList<Option>());
	}

	public ArgsParser(String programSyntax, String operandsSyntax,
			Collection<Option> options) {
		this.programSyntax = programSyntax;
		this.operandsSyntax = operandsSyntax;
		this.options = new ArrayList<Option>(options);
	}

	public void addOption(Option option) {
		this.options.add(option);
	}

	public Collection<Option> getOptions() {
		return options;
	}

	public void writeUsage() {
		try {
			writeUsage(new OutputStreamWriter(System.out));
		} catch (IOException ex) {
			throw new Error();
		}
	}

	public void writeUsage(Writer writer) throws IOException {

		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		bufferedWriter.write("Usage: ");
		bufferedWriter.write(programSyntax);
		for (Option option : options) {
			bufferedWriter.write(" ");
			String syntax = option.getSyntax();
			bufferedWriter.write(syntax);
		}
		if (options.size() > 0) {
			bufferedWriter.write(" [--]");
		}
		bufferedWriter.write(" ");
		bufferedWriter.write(operandsSyntax);
		bufferedWriter.newLine();

		int width = 0;
		for (Option option : options) {
			width = Math.max(width, option.getLongSyntax().length());
		}
		for (Option option : options) {
			bufferedWriter.write("    ");
			String syntax = option.getLongSyntax();
			bufferedWriter.write(syntax);
			for (int w = syntax.length(); w < width + 4; w++) {
				bufferedWriter.write(" ");
			}
			bufferedWriter.write(option.getDescription());
			bufferedWriter.newLine();
		}

		bufferedWriter.flush();
	}

	public String[] parse(String[] args) throws CLIException {
		int index = 0;

		while (index < args.length) {
			String arg = args[index];

			if ("--".equals(arg)) {
				index++;
				break;
			}

			if (arg.charAt(0) != '-') {
				break;
			}

			if (arg.length() == 1) {
				throw new CLIException("option must follow '-'");
			}

			if (arg.charAt(1) == '-') {
				parseLongOption(arg);
			} else {
				parseOptions(arg);
			}
			index++;
		}

		String[] operands = new String[args.length - index];
		System.arraycopy(args, index, operands, 0, operands.length);

		return operands;
	}

	private void parseLongOption(String arg) throws CLIException {
		int parseFrom = 2;
		for (Option option : options) {
			int parsedTo = option.parseLong(arg, parseFrom);
			if (parsedTo != parseFrom) {
				if (parsedTo != arg.length()) {
					throw new Error();
				}
				return;
			}
		}
		throw new CLIException("option '" + arg + "' is unkown");
	}

	private void parseOptions(String arg) throws CLIException {
		int parseFrom = 1;

		PARSING: while (parseFrom < arg.length()) {
			for (Option option : options) {
				int parsedTo = option.parse(arg, parseFrom);
				if (parsedTo != parseFrom) {
					parseFrom = parsedTo;
					continue PARSING;
				}
			}

			throw new CLIException("option '-" + arg.charAt(parseFrom)
					+ "' is unkown");
		}
	}

	public class HelpOption extends Switch {
		public HelpOption() {
			super('h');

			setLongName("help");
			setDescription("display this help and exit");
		}

		@Override
		public void onSwitch() {
			writeUsage();
			System.exit(0);
		}
	}
}
