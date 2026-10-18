package hospital.subsystem;

public class AllergyConflictException extends SubsystemException {

    public AllergyConflictException(String medicineName, String activeSubstance) {
        super(medicineName + " contains " + activeSubstance + ", the patient is allergic to it");
    }
}
