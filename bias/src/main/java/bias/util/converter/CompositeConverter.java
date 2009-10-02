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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * A composite converter that supports primitives, <code>null</code> and Java
 * core objects.
 */
public class CompositeConverter implements Converter {

	public static final String NULL = "[[!!!!!!!!NULL!!!!!!!!]]";

	private Map<Type, Converter> converters = new HashMap<Type, Converter>();

	public CompositeConverter() {
		initConverters();
	}

	private Converter getConverter(Type type) {
		Converter converter = converters.get(type);
		if (converter == null) {
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;

				converter = getConverter(parameterizedType.getRawType());
			} else if (type instanceof Class) {
				Class clazz = (Class) type;

				if (clazz.isArray()) {
					converter = getConverter(Array.class);
				} else if (type != Object.class) {
					converter = getConverter(clazz.getSuperclass());
				}
			}
		}

		if (converter == null) {
			converter = new DefaultConverter();
		}

		return converter;
	}

	public Object fromString(String string, Type type) throws Exception {
		if (CompositeConverter.NULL.equals(string)) {
			return null;
		} else {
			return getConverter(type).fromString(string, type);
		}
	}

	public String toString(Object object, Type type) throws Exception {
		if (object == null) {
			return CompositeConverter.NULL;
		} else {
			return getConverter(type).toString(object, type);
		}
	}

	protected void putConverter(Type type, Converter converter) {
		converters.put(type, converter);
	};

	protected void initConverters() {
		// primitives
		putConverter(Boolean.TYPE,
				new ConstantConverter(new BooleanConverter()));
		putConverter(Byte.TYPE, new ConstantConverter(new ByteConverter()));
		putConverter(Character.TYPE, new ConstantConverter(
				new CharacterConverter()));
		putConverter(Double.TYPE, new ConstantConverter(new DoubleConverter()));
		putConverter(Float.TYPE, new ConstantConverter(new FloatConverter()));
		putConverter(Integer.TYPE,
				new ConstantConverter(new IntegerConverter()));
		putConverter(Long.TYPE, new ConstantConverter(new LongConverter()));
		putConverter(Short.TYPE, new ConstantConverter(new ShortConverter()));

		// wrappers
		putConverter(Boolean.class, new ConstantConverter(
				new BooleanConverter()));
		putConverter(Byte.class, new ConstantConverter(new ByteConverter()));
		putConverter(Character.class, new ConstantConverter(
				new CharacterConverter()));
		putConverter(Double.class, new ConstantConverter(new DoubleConverter()));
		putConverter(Float.class, new ConstantConverter(new FloatConverter()));
		putConverter(Integer.class, new ConstantConverter(
				new IntegerConverter()));
		putConverter(Long.class, new ConstantConverter(new LongConverter()));
		putConverter(Short.class, new ShortConverter());

		// objects
		putConverter(BigDecimal.class, new BigDecimalConverter());
		putConverter(BigInteger.class, new BigIntegerConverter());
		putConverter(Class.class, new ClassConverter());
		putConverter(Color.class, new ColorConverter());
		putConverter(File.class, new FileConverter());
		putConverter(Font.class, new FontConverter());
		putConverter(Locale.class, new LocaleConverter());
		putConverter(Point.class, new PointConverter());
		putConverter(Dimension.class, new DimensionConverter());
		putConverter(Rectangle.class, new RectangleConverter());
		putConverter(Insets.class, new InsetsConverter());
		putConverter(String.class, new StringConverter());
		putConverter(URL.class, new URLConverter());
		putConverter(Image.class, new ImageConverter());
		putConverter(Icon.class, new IconConverter());
		putConverter(KeyStroke.class, new KeyStrokeConverter());
		putConverter(Enum.class, new EnumConverter());

		// arrays
		putConverter(Array.class, new ArrayConverter(this));

		// collections
		putConverter(List.class, new ListConverter(this));
		putConverter(Map.class, new MapConverter(this));
	}
}
