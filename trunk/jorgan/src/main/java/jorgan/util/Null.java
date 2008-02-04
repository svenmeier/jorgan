package jorgan.util;

public class Null {

	private Null() {
	}

	public static boolean safeEquals(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		}
		
		if (o1 != null && o1.equals(o2)) {
			return true;
		}
		
		if (o2 != null && o2.equals(o1)) {
			return true;
		}
		
		return false;
	}	
}
