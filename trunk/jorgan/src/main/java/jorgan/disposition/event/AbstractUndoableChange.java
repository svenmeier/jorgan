package jorgan.disposition.event;

public abstract class AbstractUndoableChange implements UndoableChange {
	
	public boolean replaces(UndoableChange change) {
		return false;
	}
}
