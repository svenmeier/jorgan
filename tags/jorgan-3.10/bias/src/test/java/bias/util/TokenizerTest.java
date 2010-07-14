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
package bias.util;

import junit.framework.TestCase;

/**
 * Test for {@link Tokenizer}.
 */
public class TokenizerTest extends TestCase {

	public void test() {
		Tokenizer tokens = new Tokenizer(new String[] { "A", "B", "C" });

		assertEquals(" A B C", tokens.toString());
	}

	public void testSeparatorClash() {
		Tokenizer tokens = new Tokenizer(new String[] { " ", "!", "\"", "#", "$" });

		assertEquals("% %!%\"%#%$", tokens.toString());
	}

	public void testTokenizer() {
		Tokenizer tokens = new Tokenizer(",1,2,3");

		assertEquals(3, tokens.getTokens().length);
	}

	public void testTokenizerWithEmptyToken() {
		Tokenizer tokens = new Tokenizer(",,1,,2,3,");

		assertEquals(6, tokens.getTokens().length);
	}
}
