package jorgan.swing;

import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class BaseAction extends AbstractAction {

	public void setName(String name) {
		putValue(Action.NAME, name);
	}
	
	public String getName() {
		return (String)getValue(Action.NAME);
	}
	
	public void setShortDescription(String description) {
		putValue(Action.SHORT_DESCRIPTION, description);
	}
	
	public String getShortDescription() {
		return (String)getValue(Action.SHORT_DESCRIPTION);
	}
}
