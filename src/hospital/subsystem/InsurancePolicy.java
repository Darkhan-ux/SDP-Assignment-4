package hospital.subsystem;

import java.time.LocalDate;

public record InsurancePolicy(String policyNumber,
                              String holderNationalId,
                              String provider,
                              int coveragePercent,
                              LocalDate validUntil) {
}
