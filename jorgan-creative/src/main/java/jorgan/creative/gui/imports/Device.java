/**
 * 
 */
package jorgan.creative.gui.imports;

import java.util.ArrayList;
import java.util.List;

public class Device {

	public String name;

	public List<Bank> banks = new ArrayList<Bank>();

	public Device(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}