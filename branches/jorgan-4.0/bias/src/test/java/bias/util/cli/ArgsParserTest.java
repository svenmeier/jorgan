package bias.util.cli;

import junit.framework.TestCase;

public class ArgsParserTest extends TestCase {

	public void testDoubleHyphen() throws CLIException {
		ArgsParser parser = new ArgsParser("test");
		parser.writeUsage();

		String[] operands = parser.parse(new String[] { "--", "-d", "Hello",
				"World" });

		assertEquals(3, operands.length);
		assertEquals("-d", operands[0]);
		assertEquals("Hello", operands[1]);
		assertEquals("World", operands[2]);
	}

	public void testTrailingOptions() throws CLIException {
		ArgsParser parser = new ArgsParser("test");
		parser.writeUsage();

		String[] operands = parser
				.parse(new String[] { "Hello", "World", "-d" });

		assertEquals(3, operands.length);
		assertEquals("Hello", operands[0]);
		assertEquals("World", operands[1]);
		assertEquals("-d", operands[2]);
	}

	public void testOptions() throws CLIException {
		TestSwitch d = new TestSwitch('d');
		d.setLongName("dd");
		d.setDescription("d description");

		TestSwitch e = new TestSwitch('e');
		e.setLongName("ee");
		e.setDescription("e description");

		TestSwitch f = new TestSwitch((char) 0);
		f.setLongName("ff");
		f.setDescription("f description");

		TestSwitch m = new TestSwitch('m');
		m.setLongName("mm");
		m.setDescription("m description");

		TestInput n = new TestInput('n', false);
		n.setLongName("nn");
		n.setDescription("n description");

		TestInput o = new TestInput('o', true);
		o.setLongName("oo");
		o.setDescription("o description");

		ArgsParser parser = new ArgsParser("test");
		parser.addOption(d);
		parser.addOption(e);
		parser.addOption(f);
		parser.addOption(m);
		parser.addOption(n);
		parser.addOption(o);
		parser.writeUsage();

		String[] operands = parser.parse(new String[] { "-de", "--ff", "-m",
				"--oo=ooo", "Hello", "World" });

		assertEquals(true, d.switched);
		assertEquals(true, e.switched);
		assertEquals(true, f.switched);
		assertEquals(true, m.switched);
		assertEquals(null, n.input);
		assertEquals("ooo", o.input);

		assertEquals(2, operands.length);
		assertEquals("Hello", operands[0]);
		assertEquals("World", operands[1]);
	}

	public void testUnkownOption() throws CLIException {
		ArgsParser parser = new ArgsParser("test");
		parser.writeUsage();

		try {
			parser.parse(new String[] { "-m", "Hello", "World" });
			fail();
		} catch (CLIException expected) {
			System.out.println(expected.getMessage());
		}
	}

	public void testMissingArgument() throws CLIException {
		TestInput n = new TestInput('n', true);
		n.setLongName("nn");
		n.setDescription("n description");

		ArgsParser parser = new ArgsParser("test");
		parser.addOption(n);
		parser.writeUsage();

		try {
			parser.parse(new String[] { "-n", "Hello", "World" });
			fail();
		} catch (CLIException expected) {
			System.out.println(expected.getMessage());
		}
	}

	public void testNotAllowedArgument() throws CLIException {
		TestSwitch d = new TestSwitch('d');
		d.setLongName("dd");
		d.setDescription("d description");

		ArgsParser parser = new ArgsParser("test");
		parser.addOption(d);
		parser.writeUsage();

		try {
			parser.parse(new String[] { "--dd=arg", "Hello", "World" });
			fail();
		} catch (CLIException expected) {
			System.out.println(expected.getMessage());
		}
	}

	private class TestSwitch extends Switch {
		public boolean switched = false;

		public TestSwitch(char name) {
			super(name);
		}

		@Override
		protected void onSwitch() {
			switched = true;
		}
	}

	private class TestInput extends Input {
		public String input;

		public TestInput(char name, boolean required) {
			super(name, required);
		}

		@Override
		protected void onInput(String input) {
			this.input = input;
		}
	}
}
