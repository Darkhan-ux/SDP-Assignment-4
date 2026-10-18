package hospital.subsystem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Subsystem #2: checks that an insurance policy is real, belongs to the
 * patient and is still valid, and tells how much of the bill it covers.
 */
public class InsuranceVerifier {

    private final Map<String, InsurancePolicy> policiesByNumber = new HashMap<>();
    private final LocalDate today;

    public InsuranceVerifier(LocalDate today) {
        this.today = today;
    }

    public void addPolicy(InsurancePolicy policy) {
        policiesByNumber.put(policy.policyNumber(), policy);
    }

    /**
     * @return the share of the bill the insurer pays, in percent (0..100)
     */
    public int verifyCoverage(String policyNumber, String patientNationalId) {
        InsurancePolicy policy = policiesByNumber.get(policyNumber);
        if (policy == null) {
            throw new InsuranceRejectedException(policyNumber, "policy not found");
        }
        if (!policy.holderNationalId().equals(patientNationalId)) {
            throw new InsuranceRejectedException(policyNumber, "policy belongs to another person");
        }
        if (policy.validUntil().isBefore(today)) {
            throw new InsuranceRejectedException(policyNumber, "expired on " + policy.validUntil());
        }
        log("Policy " + policyNumber + " (" + policy.provider() + ") covers " + policy.coveragePercent() + "%");
        return policy.coveragePercent();
    }

    private void log(String message) {
        System.out.println("    [InsuranceVerifier] " + message);
    }
}
