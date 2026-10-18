package hospital.facade;

/**
 * Thrown when a patient could not be admitted. By the time the client sees it,
 * every step that had already succeeded has been rolled back.
 */
public class AdmissionFailedException extends RuntimeException {

    public AdmissionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
