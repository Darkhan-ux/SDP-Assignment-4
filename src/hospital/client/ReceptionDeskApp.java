package hospital.client;

import hospital.Hospital;
import hospital.facade.AdmissionFailedException;
import hospital.facade.AdmissionRequest;
import hospital.facade.AdmissionTicket;
import hospital.facade.DischargeFailedException;
import hospital.facade.HospitalAdmissionFacade;
import hospital.facade.HospitalStatusFacade;

import java.util.List;
import java.util.Set;

/**
 * Client. Talks to the hospital only through the two facades;
 * it never imports or creates anything from {@code hospital.subsystem}.
 */
public class ReceptionDeskApp {

    private static final String SELF_PAYING = null;

    public static void main(String[] args) {
        Hospital hospital = Hospital.openWithDemoData();
        HospitalAdmissionFacade reception = hospital.admissions();
        HospitalStatusFacade headDoctor = hospital.status();

        section("1. Insured patient: admitted, treated and discharged");
        AdmissionTicket aigerim = admit(reception, new AdmissionRequest(
                "Aigerim Seitkali", "990101400123", Set.of(), "KZ-MED-1001",
                "Chest pain after exercise", "cardiologist"));
        discharge(reception, aigerim.patientId(), "Stable angina", List.of("Aspirin Cardio", "Bisoprolol"));

        section("2. Expired insurance: admission refused, registration rolled back");
        admit(reception, new AdmissionRequest(
                "Dauren Omarov", "850505300456", Set.of(), "KZ-MED-2002",
                "Back pain", "therapist"));

        section("3. Allergic patient: first prescription refused, second one accepted");
        AdmissionTicket madina = admit(reception, new AdmissionRequest(
                "Madina Ismailova", "030303600111", Set.of("Penicillin"), SELF_PAYING,
                "Sore throat and fever", "therapist"));
        discharge(reception, madina.patientId(), "Acute tonsillitis", List.of("Amoxiclav"));
        discharge(reception, madina.patientId(), "Acute tonsillitis", List.of("Paracetamol", "Ibuprofen"));

        section("4. Only one neurologist: the second patient is refused, all steps rolled back");
        admit(reception, new AdmissionRequest(
                "Arman Tulegenov", "010202500789", Set.of(), "KZ-MED-3003",
                "Recurring migraine", "neurologist"));
        admit(reception, new AdmissionRequest(
                "Zhanna Abenova", "920909400222", Set.of(), SELF_PAYING,
                "Dizziness", "neurologist"));

        section("5. Head doctor's overview (second facade)");
        System.out.println(headDoctor.currentStatus());
    }

    private static AdmissionTicket admit(HospitalAdmissionFacade reception, AdmissionRequest request) {
        System.out.println("  > Admitting " + request.fullName());
        try {
            AdmissionTicket ticket = reception.admitPatient(request);
            System.out.println("  OK  " + ticket);
            return ticket;
        } catch (AdmissionFailedException refused) {
            System.out.println("  FAILED  " + refused.getMessage());
            return null;
        }
    }

    private static void discharge(HospitalAdmissionFacade reception, String patientId,
                                  String diagnosis, List<String> medicines) {
        System.out.println("  > Discharging " + patientId + " with " + medicines);
        try {
            System.out.println("  OK  " + reception.dischargePatient(patientId, diagnosis, medicines));
        } catch (DischargeFailedException refused) {
            System.out.println("  FAILED  " + refused.getMessage());
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
