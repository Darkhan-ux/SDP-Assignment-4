package hospital;

import hospital.facade.HospitalAdmissionFacade;
import hospital.facade.HospitalStatusFacade;
import hospital.subsystem.DoctorScheduler;
import hospital.subsystem.InsurancePolicy;
import hospital.subsystem.InsuranceVerifier;
import hospital.subsystem.MedicalRecords;
import hospital.subsystem.PatientRegistry;
import hospital.subsystem.Pharmacy;
import hospital.subsystem.Specialty;

import java.time.LocalDate;

/**
 * Composition root: the one place that creates the subsystem objects, fills them
 * with demo data and wires them into the facades. Both facades share the same
 * subsystem instances, so they see the same hospital.
 */
public final class Hospital {

    private final HospitalAdmissionFacade admissions;
    private final HospitalStatusFacade status;

    private Hospital(HospitalAdmissionFacade admissions, HospitalStatusFacade status) {
        this.admissions = admissions;
        this.status = status;
    }

    public static Hospital openWithDemoData() {
        LocalDate today = LocalDate.now();

        PatientRegistry patientRegistry = new PatientRegistry();
        InsuranceVerifier insuranceVerifier = createInsuranceVerifier(today);
        DoctorScheduler doctorScheduler = createDoctorScheduler();
        MedicalRecords medicalRecords = new MedicalRecords();
        Pharmacy pharmacy = createPharmacy();

        return new Hospital(
                new HospitalAdmissionFacade(patientRegistry, insuranceVerifier, doctorScheduler, medicalRecords, pharmacy),
                new HospitalStatusFacade(patientRegistry, doctorScheduler, medicalRecords, pharmacy));
    }

    public HospitalAdmissionFacade admissions() {
        return admissions;
    }

    public HospitalStatusFacade status() {
        return status;
    }

    private static InsuranceVerifier createInsuranceVerifier(LocalDate today) {
        InsuranceVerifier verifier = new InsuranceVerifier(today);
        verifier.addPolicy(new InsurancePolicy("KZ-MED-1001", "990101400123", "Halyk Insurance", 80, today.plusYears(1)));
        verifier.addPolicy(new InsurancePolicy("KZ-MED-2002", "850505300456", "Freedom Insurance", 60, today.minusDays(10)));
        verifier.addPolicy(new InsurancePolicy("KZ-MED-3003", "010202500789", "Halyk Insurance", 100, today.plusMonths(6)));
        return verifier;
    }

    private static DoctorScheduler createDoctorScheduler() {
        DoctorScheduler scheduler = new DoctorScheduler();
        scheduler.addDoctor("Dr. Akhmetov", Specialty.THERAPIST, 8_000, 3);
        scheduler.addDoctor("Dr. Sergeeva", Specialty.THERAPIST, 8_000, 3);
        scheduler.addDoctor("Dr. Nurlanova", Specialty.CARDIOLOGIST, 15_000, 2);
        scheduler.addDoctor("Dr. Kim", Specialty.NEUROLOGIST, 18_000, 1);
        scheduler.addDoctor("Dr. Bekov", Specialty.SURGEON, 25_000, 2);
        return scheduler;
    }

    private static Pharmacy createPharmacy() {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.addMedicine("Paracetamol", "paracetamol", 600, 40);
        pharmacy.addMedicine("Ibuprofen", "ibuprofen", 900, 25);
        pharmacy.addMedicine("Amoxiclav", "penicillin", 3_500, 12);
        pharmacy.addMedicine("Aspirin Cardio", "acetylsalicylic acid", 1_800, 10);
        pharmacy.addMedicine("Bisoprolol", "bisoprolol", 2_400, 3);
        return pharmacy;
    }
}
