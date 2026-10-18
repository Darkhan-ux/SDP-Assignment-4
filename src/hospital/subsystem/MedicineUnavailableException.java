package hospital.subsystem;

public class MedicineUnavailableException extends SubsystemException {

    public MedicineUnavailableException(String medicineName, String reason) {
        super(medicineName + " cannot be dispensed: " + reason);
    }
}
