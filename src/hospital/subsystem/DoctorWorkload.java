package hospital.subsystem;

public record DoctorWorkload(String doctorName, Specialty specialty, int bookedPatients, int dailyCapacity) {
}
