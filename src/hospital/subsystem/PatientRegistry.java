package hospital.subsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Subsystem #1: keeps the list of known patients and issues hospital IDs.
 */
public class PatientRegistry {

    private static final int NATIONAL_ID_LENGTH = 12;

    private final Map<String, Patient> patientsById = new HashMap<>();
    private final Map<String, String> patientIdByNationalId = new HashMap<>();
    private int nextPatientNumber = 1;

    public Patient register(String fullName, String nationalId, Set<String> allergies) {
        validateName(fullName);
        validateNationalId(nationalId);
        if (patientIdByNationalId.containsKey(nationalId)) {
            throw new IllegalStateException("Patient with national ID " + nationalId + " is already registered");
        }

        String patientId = String.format("P-%04d", nextPatientNumber++);
        Set<String> normalizedAllergies = allergies.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Patient patient = new Patient(patientId, fullName, nationalId, normalizedAllergies);

        patientsById.put(patientId, patient);
        patientIdByNationalId.put(nationalId, patientId);
        log("Registered " + patientId + " (" + fullName + ")");
        return patient;
    }

    public Optional<Patient> findByNationalId(String nationalId) {
        return Optional.ofNullable(patientIdByNationalId.get(nationalId)).map(patientsById::get);
    }

    public void unregister(String patientId) {
        Patient removed = patientsById.remove(patientId);
        if (removed != null) {
            patientIdByNationalId.remove(removed.nationalId());
            log("Unregistered " + patientId);
        }
    }

    public int registeredPatientCount() {
        return patientsById.size();
    }

    private void validateName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Patient name must not be empty");
        }
    }

    private void validateNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() != NATIONAL_ID_LENGTH || !nationalId.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("National ID must be exactly " + NATIONAL_ID_LENGTH + " digits");
        }
    }

    private void log(String message) {
        System.out.println("    [PatientRegistry] " + message);
    }
}
