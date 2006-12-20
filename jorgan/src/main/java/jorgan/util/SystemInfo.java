package jorgan.util;

/**
 * Information about the system.
 */
public class SystemInfo {

	/**
	 * Get the Java version.
	 * 
	 * @return the java version
	 */
	public String getJavaVersion() {
		return System.getProperty("java.version");
	}

	/**
	 * Get the name of the os.
	 * 
	 * @return os name
	 */
	public String getOsName() {
		return System.getProperty("os.name");
	}
}
