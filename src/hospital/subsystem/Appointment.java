package hospital.subsystem;

public record Appointment(String appointmentId,
                          String doctorName,
                          Specialty specialty,
                          String patientId,
                          int consultationFee) {
}
