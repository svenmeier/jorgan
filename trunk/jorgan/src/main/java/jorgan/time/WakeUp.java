/**
 * 
 */
package jorgan.time;

public interface WakeUp {
	public void trigger(long time);

	public boolean replaces(WakeUp wakeUp);
}