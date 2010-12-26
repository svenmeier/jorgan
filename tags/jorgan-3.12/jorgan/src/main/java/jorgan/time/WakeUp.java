/**
 * 
 */
package jorgan.time;

public interface WakeUp {
	public void trigger();

	public boolean replaces(WakeUp wakeUp);
}