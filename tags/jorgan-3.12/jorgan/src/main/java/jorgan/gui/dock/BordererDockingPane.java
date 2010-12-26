/**
 * 
 */
package jorgan.gui.dock;

import swingx.docking.Dock;
import swingx.docking.DockingPane;
import swingx.docking.border.Eclipse3Border;

/**
 * Beautify with a border in style of <em>Eclipse 3</em>
 */
public class BordererDockingPane extends DockingPane {

	@Override
	protected Dock createDockImpl() {
		Dock dock = super.createDockImpl();
		dock.setBorder(new Eclipse3Border());
		return dock;
	}
}