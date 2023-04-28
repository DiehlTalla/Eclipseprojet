package jfox.exception;

@SuppressWarnings("serial")
public class ExceptionValidation extends RuntimeException {

	public ExceptionValidation() {
		super();
	}

	public ExceptionValidation(String message, Throwable cause) {
		super(message, cause);
	}

	public ExceptionValidation(String message) {
		super(message);
	}

	public ExceptionValidation(Throwable cause) {
		super(cause);
	}

}
