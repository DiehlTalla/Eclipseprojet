package jfox.exception;

@SuppressWarnings("serial")
public class ExceptionAnomaly extends RuntimeException {

	public ExceptionAnomaly() {
		super();
	}

	public ExceptionAnomaly(String message, Throwable cause) {
		super(message, cause);
	}

	public ExceptionAnomaly(String message) {
		super(message);
	}

	public ExceptionAnomaly(Throwable cause) {
		super(cause);
	}

}
