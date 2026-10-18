package hospital.facade;

import java.util.List;

/**
 * Final bill and treatment summary handed to the patient on discharge. Amounts are in tenge.
 */
public record DischargeSummary(String patientId,
                               String diagnosis,
                               List<String> dispensedMedicines,
                               int totalCost,
                               int paidByInsurance,
                               int paidByPatient) {

    @Override
    public String toString() {
        return patientId + " discharged with \"" + diagnosis + "\""
                + "\n      medicines: " + dispensedMedicines
                + "\n      total: " + totalCost + " KZT, insurance: " + paidByInsurance
                + " KZT, patient pays: " + paidByPatient + " KZT";
    }
}
