package hospital.subsystem;

public class RecordAlreadyOpenException extends SubsystemException {

    public RecordAlreadyOpenException(String patientId) {
        super("Patient " + patientId + " already has an open medical record");
    }
}
