package easyenterprise.lib.cloner;

public class CloneException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CloneException(String string) {
		super(string);
	}

	public CloneException(String string, Exception e) {
		super(string, e);
	}

}
