package hospital.subsystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Subsystem #3: knows which doctors work today and how loaded they are,
 * and books the least busy doctor of the requested specialty.
 */
public class DoctorScheduler {

    private final List<Doctor> doctors = new ArrayList<>();
    private int nextAppointmentNumber = 1;

    public void addDoctor(String name, Specialty specialty, int consultationFee, int dailyCapacity) {
        doctors.add(new Doctor(name, specialty, consultationFee, dailyCapacity));
    }

    public Appointment book(Specialty specialty, String patientId) {
        Doctor doctor = doctors.stream()
                .filter(candidate -> candidate.specialty == specialty)
                .filter(Doctor::hasFreeSlot)
                .min(Comparator.comparingInt(candidate -> candidate.bookedPatients))
                .orElseThrow(() -> new NoDoctorAvailableException(specialty));

        doctor.bookedPatients++;
        String appointmentId = String.format("A-%04d", nextAppointmentNumber++);
        log("Booked " + doctor.name + " for " + patientId + " (" + appointmentId + ")");
        return new Appointment(appointmentId, doctor.name, specialty, patientId, doctor.consultationFee);
    }

    public void release(Appointment appointment) {
        doctors.stream()
                .filter(doctor -> doctor.name.equals(appointment.doctorName()))
                .findFirst()
                .ifPresent(doctor -> {
                    doctor.bookedPatients--;
                    log("Released " + doctor.name + " from " + appointment.appointmentId());
                });
    }

    public List<DoctorWorkload> workload() {
        return doctors.stream()
                .map(doctor -> new DoctorWorkload(doctor.name, doctor.specialty, doctor.bookedPatients, doctor.dailyCapacity))
                .toList();
    }

    private void log(String message) {
        System.out.println("    [DoctorScheduler] " + message);
    }

    private static final class Doctor {
        private final String name;
        private final Specialty specialty;
        private final int consultationFee;
        private final int dailyCapacity;
        private int bookedPatients;

        private Doctor(String name, Specialty specialty, int consultationFee, int dailyCapacity) {
            this.name = name;
            this.specialty = specialty;
            this.consultationFee = consultationFee;
            this.dailyCapacity = dailyCapacity;
        }

        private boolean hasFreeSlot() {
            return bookedPatients < dailyCapacity;
        }
    }
}
