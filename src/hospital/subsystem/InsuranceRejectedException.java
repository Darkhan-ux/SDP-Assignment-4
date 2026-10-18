package hospital.subsystem;

public class InsuranceRejectedException extends SubsystemException {

    public InsuranceRejectedException(String policyNumber, String reason) {
        super("Insurance policy " + policyNumber + " rejected: " + reason);
    }
}
