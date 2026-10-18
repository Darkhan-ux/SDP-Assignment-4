package hospital.facade;

/**
 * What the client gets back after a successful admission.
 */
public record AdmissionTicket(String patientId,
                              String patientName,
                              String doctorName,
                              String specialist,
                              int insuranceCoveragePercent) {

    @Override
    public String toString() {
        return patientName + " (" + patientId + ") -> " + doctorName + ", " + specialist
                + ", insurance covers " + insuranceCoveragePercent + "%";
    }
}
