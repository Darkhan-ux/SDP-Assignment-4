package hospital.subsystem;

public class NoDoctorAvailableException extends SubsystemException {

    public NoDoctorAvailableException(Specialty specialty) {
        super("No " + specialty.displayName() + " has a free slot today");
    }
}
