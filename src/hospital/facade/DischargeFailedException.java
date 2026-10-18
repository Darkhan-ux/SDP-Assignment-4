package hospital.facade;

/**
 * Thrown when a patient could not be discharged. The patient stays admitted,
 * so discharge can be retried (for example with a different prescription).
 */
public class DischargeFailedException extends RuntimeException {

    public DischargeFailedException(String message) {
        super(message);
    }

    public DischargeFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
