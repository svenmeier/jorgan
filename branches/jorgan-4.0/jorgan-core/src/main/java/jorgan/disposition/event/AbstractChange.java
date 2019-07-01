package jorgan.disposition.event;

public abstract class AbstractChange implements UndoableChange {
	
	public boolean replaces(UndoableChange change) {
		return false;
	}
}
