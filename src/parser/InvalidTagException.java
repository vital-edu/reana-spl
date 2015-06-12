package parser;

public class InvalidTagException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String tagName;

	public InvalidTagException(String message, String tagName) {
		super(message);
		this.tagName = tagName;
	}

	public String getMissingTagName() {
		return this.tagName;
	}
}
