package jorgan.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class LocaleUtils {

	private static Locale defaultLocale;

	public static void setLocale(Locale locale) {
		// keep default before changing it
		getDefault();

		Locale.setDefault(locale);
	}

	public static Locale getLocale() {
		return Locale.getDefault();
	}

	public static Locale getDefault() {
		if (defaultLocale == null) {
			defaultLocale = Locale.getDefault();
		}

		return defaultLocale;
	}

	public static Locale[] getLocales() {
		Locale[] locales = Locale.getAvailableLocales();

		Arrays.sort(locales, new LocaleComparator());

		return locales;
	}

	public static class LocaleComparator implements Comparator<Locale> {
		@Override
		public int compare(Locale o1, Locale o2) {
			return o1.toString().compareTo(o2.toString());
		}
	}
}
