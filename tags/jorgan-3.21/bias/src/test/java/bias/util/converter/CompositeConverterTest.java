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
package bias.util.converter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.KeyStroke;

import junit.framework.TestCase;
import bias.util.TypeReference;
import bias.util.converter.CompositeConverter;

/**
 * Test for {@link CompositeConverter}s.
 */
public class CompositeConverterTest extends TestCase {

	public void testToString() throws Exception {
		CompositeConverter converter = new CompositeConverter();

		assertEquals("true", converter.toString(Boolean.TRUE, Boolean.TYPE));
		assertEquals("A", converter.toString(Character.valueOf('A'),
				Character.TYPE));
		assertEquals("1", converter.toString(Byte.valueOf((byte) 1), Byte.TYPE));
		assertEquals("2", converter.toString(Short.valueOf((short) 2),
				Short.TYPE));
		assertEquals("3", converter.toString(Integer.valueOf(3), Integer.TYPE));
		assertEquals("4", converter.toString(Long.valueOf(4), Long.TYPE));
		assertEquals("5.1", converter.toString(Float.valueOf(5.1f), Float.TYPE));
		assertEquals("6.2", converter.toString(Double.valueOf(6.2d),
				Double.TYPE));

		assertEquals("10,20,30,40", converter.toString(
				new Color(10, 20, 30, 40), Color.class));
		assertEquals("Arial,1,10", converter.toString(new Font("Arial", 1, 10),
				Font.class));
		assertEquals("10,20", converter
				.toString(new Point(10, 20), Point.class));
		assertEquals("10,20,30,40", converter.toString(new Rectangle(10, 20,
				30, 40), Rectangle.class));
		assertEquals("test", converter.toString(new File("test"), File.class));
		assertEquals("lorem ipsum", converter.toString("lorem ipsum",
				String.class));
		assertEquals("java.lang.Object", converter.toString(Object.class,
				Class.class));
		assertEquals("http://www.google.com", converter.toString(new URL(
				"http://www.google.com"), URL.class));
		assertEquals("de_DE", converter.toString(new Locale("de", "DE"),
				Locale.class));
		assertEquals("true", converter.toString(Boolean.TRUE, Boolean.class));
		assertEquals("A", converter.toString(new Character('A'),
				Character.class));
		assertEquals("1", converter
				.toString(Byte.valueOf((byte) 1), Byte.class));
		assertEquals("2", converter.toString(Short.valueOf((short) 2),
				Short.class));
		assertEquals("3", converter.toString(Integer.valueOf(3), Integer.class));
		assertEquals("4", converter.toString(Long.valueOf(4), Long.class));
		assertEquals("5.1", converter
				.toString(Float.valueOf(5.1f), Float.class));
		assertEquals("6.2", converter.toString(Double.valueOf(6.2d),
				Double.class));
		assertEquals("7", converter.toString(new BigInteger("7"),
				BigInteger.class));
		assertEquals("8.3", converter.toString(new BigDecimal("8.3"),
				BigDecimal.class));
		assertEquals("alt pressed LEFT", converter.toString(KeyStroke
				.getKeyStroke("alt pressed LEFT"), KeyStroke.class));

		List<String> list = new ArrayList<String>();
		list.add("AAA");
		list.add("BBB");
		list.add("CCC");
		assertEquals(" AAA BBB CCC", converter.toString(list,
				new TypeReference<List<String>>() {
				}.getType()));

		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(new Integer(1), "one");
		map.put(new Integer(2), "two");
		map.put(new Integer(3), "three");
		assertEquals(" 1 one 2 two 3 three", converter.toString(map,
				new TypeReference<Map<Integer, String>>() {
				}.getType()));

		String[] array = new String[] { "A", "B", "C" };
		assertEquals(" A B C", converter.toString(array, array.getClass()));

		assertEquals(CompositeConverter.NULL, converter.toString(null,
				Object.class));
	}

	public void testFromString() throws Exception {
		CompositeConverter converter = new CompositeConverter();

		assertEquals(Boolean.TRUE, converter.fromString("true", Boolean.TYPE));
		assertEquals(Character.valueOf('A'), converter.fromString("A",
				Character.TYPE));
		assertEquals(Character.valueOf('A'), converter.fromString("\\u0041",
				Character.TYPE));
		assertEquals(Byte.valueOf((byte) 1), converter.fromString("1",
				Byte.TYPE));
		assertEquals(Short.valueOf((short) 2), converter.fromString("2",
				Short.TYPE));
		assertEquals(Integer.valueOf(3), converter
				.fromString("3", Integer.TYPE));
		assertEquals(Long.valueOf(4), converter.fromString("4", Long.TYPE));
		assertEquals(Float.valueOf(5.1f), converter.fromString("5.1",
				Float.TYPE));
		assertEquals(Double.valueOf(6.2d), converter.fromString("6.2",
				Double.TYPE));

		assertEquals(Thread.MIN_PRIORITY, converter.fromString(
				"java.lang.Thread#MIN_PRIORITY", Integer.TYPE));

		assertEquals(new Color(10, 20, 30, 40), converter.fromString(
				"10,20,30,40", Color.class));
		assertEquals(new Font("Arial", 1, 10), converter.fromString(
				"Arial,1,10", Font.class));
		assertEquals(new Point(10, 20), converter.fromString("10,20",
				Point.class));
		assertEquals(new Rectangle(10, 20, 30, 40), converter.fromString(
				"10,20,30,40", Rectangle.class));
		assertEquals(new File("test"), converter.fromString("test", File.class));
		assertEquals("lorem ipsum", converter.fromString("lorem ipsum",
				String.class));
		assertEquals(Object.class, converter.fromString("java.lang.Object",
				Class.class));
		assertEquals(new URL("http://www.google.com"), converter.fromString(
				"http://www.google.com", URL.class));
		assertEquals(new Locale("de", "DE"), converter.fromString("de_DE",
				Locale.class));
		assertEquals(Boolean.TRUE, converter.fromString("true", Boolean.class));
		assertEquals(new Character('A'), converter.fromString("A",
				Character.class));
		assertEquals(new Byte((byte) 1), converter.fromString("1", Byte.class));
		assertEquals(new Short((short) 2), converter.fromString("2",
				Short.class));
		assertEquals(new Integer(3), converter.fromString("3", Integer.class));
		assertEquals(new Long(4), converter.fromString("4", Long.class));
		assertEquals(new Float(5.1f), converter.fromString("5.1", Float.class));
		assertEquals(new Double(6.2d), converter
				.fromString("6.2", Double.class));
		assertEquals(new BigInteger("7"), converter.fromString("7",
				BigInteger.class));
		assertEquals(new BigDecimal("8.3"), converter.fromString("8.3",
				BigDecimal.class));
		assertEquals(KeyStroke.getKeyStroke("alt pressed LEFT"), converter
				.fromString("alt pressed LEFT", KeyStroke.class));

		List<String> list = new ArrayList<String>();
		list.add("AAA");
		list.add("BBB");
		list.add("CCC");
		assertEquals(list, converter.fromString(",AAA,BBB,CCC",
				new TypeReference<List<String>>() {
				}.getType()));

		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(new Integer(1), "one");
		map.put(new Integer(2), "two");
		map.put(new Integer(3), "three");
		assertEquals(map, converter.fromString(",1,one,2,two,3,three",
				new TypeReference<Map<Integer, String>>() {
				}.getType()));

		String[] array = new String[] { "A", "B", "C" };
		assertTrue(Arrays.equals(array, (String[]) converter.fromString(
				" A B C", array.getClass())));

		assertNull(converter.fromString(CompositeConverter.NULL, Object.class));
	}
}
