package hospital.subsystem;

/**
 * Base type for every failure raised by a hospital subsystem.
 * Lets a coordinator catch "any subsystem problem" in one place
 * without depending on each concrete exception.
 */
public class SubsystemException extends RuntimeException {

    public SubsystemException(String message) {
        super(message);
    }
}
