package hospital.facade;

import java.util.Set;

/**
 * Everything the reception desk knows about a patient arriving at the hospital.
 * Uses only plain Java types so the client never has to import subsystem classes.
 *
 * @param insurancePolicyNumber may be {@code null} for self-paying patients
 * @param requiredSpecialist    e.g. "therapist", "cardiologist", "neurologist", "surgeon"
 */
public record AdmissionRequest(String fullName,
                               String nationalId,
                               Set<String> allergies,
                               String insurancePolicyNumber,
                               String complaint,
                               String requiredSpecialist) {

    public boolean isSelfPaying() {
        return insurancePolicyNumber == null;
    }
}
