package hospital.facade;

import hospital.subsystem.DoctorScheduler;
import hospital.subsystem.DoctorWorkload;
import hospital.subsystem.MedicalRecords;
import hospital.subsystem.PatientRegistry;
import hospital.subsystem.Pharmacy;

import java.util.List;

/**
 * Additional facade for the head doctor: a read-only overview of the hospital.
 * Kept separate from {@link HospitalAdmissionFacade} so reporting does not
 * pollute the reception desk's API.
 */
public class HospitalStatusFacade {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final PatientRegistry patientRegistry;
    private final DoctorScheduler doctorScheduler;
    private final MedicalRecords medicalRecords;
    private final Pharmacy pharmacy;

    public HospitalStatusFacade(PatientRegistry patientRegistry,
                                DoctorScheduler doctorScheduler,
                                MedicalRecords medicalRecords,
                                Pharmacy pharmacy) {
        this.patientRegistry = patientRegistry;
        this.doctorScheduler = doctorScheduler;
        this.medicalRecords = medicalRecords;
        this.pharmacy = pharmacy;
    }

    public HospitalStatusReport currentStatus() {
        List<String> workload = doctorScheduler.workload().stream()
                .map(this::describe)
                .toList();

        return new HospitalStatusReport(
                patientRegistry.registeredPatientCount(),
                medicalRecords.openRecordCount(),
                workload,
                pharmacy.medicinesBelow(LOW_STOCK_THRESHOLD));
    }

    private String describe(DoctorWorkload workload) {
        return workload.doctorName() + " (" + workload.specialty().displayName() + "): "
                + workload.bookedPatients() + "/" + workload.dailyCapacity() + " patients";
    }
}
