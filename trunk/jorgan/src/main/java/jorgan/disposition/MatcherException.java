package jorgan.disposition;

public class MatcherException extends Exception {

	public MatcherException(String pattern) {
		super(pattern);
	}
	
	public String getPattern() {
		return getMessage();
	}
}