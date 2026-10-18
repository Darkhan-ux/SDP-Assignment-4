package hospital.facade;

import hospital.subsystem.Appointment;
import hospital.subsystem.DispensedMedicine;
import hospital.subsystem.DoctorScheduler;
import hospital.subsystem.InsuranceVerifier;
import hospital.subsystem.MedicalRecord;
import hospital.subsystem.MedicalRecords;
import hospital.subsystem.Patient;
import hospital.subsystem.PatientRegistry;
import hospital.subsystem.Pharmacy;
import hospital.subsystem.Specialty;
import hospital.subsystem.SubsystemException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main facade: the reception desk of the hospital.
 * <p>
 * Two simple operations, {@link #admitPatient} and {@link #dischargePatient}, hide
 * the coordination of five subsystems: patient registry, insurance check, doctor
 * scheduling, medical records and pharmacy. The facade only decides <em>what is
 * called and in which order</em>; all business rules live in the subsystem classes.
 */
public class HospitalAdmissionFacade {

    private static final int FULL_COVERAGE_PERCENT = 100;
    private static final int NO_COVERAGE_PERCENT = 0;

    private final PatientRegistry patientRegistry;
    private final InsuranceVerifier insuranceVerifier;
    private final DoctorScheduler doctorScheduler;
    private final MedicalRecords medicalRecords;
    private final Pharmacy pharmacy;

    private final Map<String, ActiveAdmission> activeAdmissions = new HashMap<>();

    public HospitalAdmissionFacade(PatientRegistry patientRegistry,
                                   InsuranceVerifier insuranceVerifier,
                                   DoctorScheduler doctorScheduler,
                                   MedicalRecords medicalRecords,
                                   Pharmacy pharmacy) {
        this.patientRegistry = patientRegistry;
        this.insuranceVerifier = insuranceVerifier;
        this.doctorScheduler = doctorScheduler;
        this.medicalRecords = medicalRecords;
        this.pharmacy = pharmacy;
    }

    /**
     * Registers the patient, checks insurance, opens a medical record and books a doctor.
     * If any step fails, the steps already done are undone in reverse order.
     */
    public AdmissionTicket admitPatient(AdmissionRequest request) {
        Deque<Runnable> undoSteps = new ArrayDeque<>();
        try {
            Specialty specialty = parseSpecialty(request.requiredSpecialist());
            Patient patient = findOrRegisterPatient(request, undoSteps);
            int coveragePercent = checkInsurance(request, patient);

            MedicalRecord record = medicalRecords.open(patient.id(), request.complaint());
            undoSteps.push(() -> medicalRecords.delete(record.recordId()));

            Appointment appointment = doctorScheduler.book(specialty, patient.id());
            undoSteps.push(() -> doctorScheduler.release(appointment));

            medicalRecords.addNote(record.recordId(), "Assigned to " + appointment.doctorName());
            activeAdmissions.put(patient.id(), new ActiveAdmission(patient, record.recordId(), appointment, coveragePercent));

            return new AdmissionTicket(patient.id(), patient.fullName(), appointment.doctorName(),
                    specialty.displayName(), coveragePercent);
        } catch (SubsystemException | IllegalArgumentException | IllegalStateException failure) {
            rollBack(undoSteps);
            throw new AdmissionFailedException("Cannot admit " + request.fullName() + ": " + failure.getMessage(), failure);
        }
    }

    /**
     * Records the diagnosis, dispenses the prescribed medicines, closes the record,
     * frees the doctor and calculates the final bill.
     */
    public DischargeSummary dischargePatient(String patientId, String diagnosis, List<String> prescribedMedicines) {
        ActiveAdmission admission = activeAdmissions.get(patientId);
        if (admission == null) {
            throw new DischargeFailedException("Patient " + patientId + " is not currently admitted");
        }

        try {
            medicalRecords.setDiagnosis(admission.recordId(), diagnosis);
            List<DispensedMedicine> medicines = pharmacy.dispense(prescribedMedicines, admission.patient().allergies());
            medicalRecords.close(admission.recordId());
            doctorScheduler.release(admission.appointment());
            activeAdmissions.remove(patientId);

            return buildSummary(admission, diagnosis, medicines);
        } catch (SubsystemException | IllegalArgumentException | IllegalStateException failure) {
            throw new DischargeFailedException("Cannot discharge " + patientId + ": " + failure.getMessage(), failure);
        }
    }

    private Patient findOrRegisterPatient(AdmissionRequest request, Deque<Runnable> undoSteps) {
        return patientRegistry.findByNationalId(request.nationalId())
                .orElseGet(() -> {
                    Patient registered = patientRegistry.register(request.fullName(), request.nationalId(), request.allergies());
                    undoSteps.push(() -> patientRegistry.unregister(registered.id()));
                    return registered;
                });
    }

    private int checkInsurance(AdmissionRequest request, Patient patient) {
        if (request.isSelfPaying()) {
            return NO_COVERAGE_PERCENT;
        }
        return insuranceVerifier.verifyCoverage(request.insurancePolicyNumber(), patient.nationalId());
    }

    private Specialty parseSpecialty(String specialistName) {
        try {
            return Specialty.valueOf(specialistName.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException unknown) {
            throw new IllegalArgumentException("Unknown specialist \"" + specialistName + "\"");
        }
    }

    private DischargeSummary buildSummary(ActiveAdmission admission, String diagnosis, List<DispensedMedicine> medicines) {
        int medicinesCost = medicines.stream().mapToInt(DispensedMedicine::price).sum();
        int totalCost = admission.appointment().consultationFee() + medicinesCost;
        int paidByInsurance = totalCost * admission.coveragePercent() / FULL_COVERAGE_PERCENT;
        List<String> medicineNames = medicines.stream().map(DispensedMedicine::medicineName).toList();

        return new DischargeSummary(admission.patient().id(), diagnosis, medicineNames,
                totalCost, paidByInsurance, totalCost - paidByInsurance);
    }

    private void rollBack(Deque<Runnable> undoSteps) {
        if (!undoSteps.isEmpty()) {
            System.out.println("    [Facade] Rolling back " + undoSteps.size() + " completed step(s)");
        }
        while (!undoSteps.isEmpty()) {
            undoSteps.pop().run();
        }
    }

    private record ActiveAdmission(Patient patient, String recordId, Appointment appointment, int coveragePercent) {
    }
}
