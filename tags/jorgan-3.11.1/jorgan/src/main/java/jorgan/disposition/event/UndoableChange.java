package jorgan.disposition.event;

public interface UndoableChange extends Change {
	
	public void undo();
	
	public void redo();
	
	public boolean replaces(UndoableChange change);
}
