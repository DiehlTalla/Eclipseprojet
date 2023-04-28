package jfox.exception;


@SuppressWarnings("serial")
public class ExceptionPermission extends ExceptionAnomaly {

	public ExceptionPermission() {
		super();
	}

	public ExceptionPermission(String message, Throwable cause) {
		super(message, cause);
	}

	public ExceptionPermission(String message) {
		super(message);
	}

	public ExceptionPermission(Throwable cause) {
		super(cause);
	}

}
