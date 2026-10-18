package hospital.subsystem;

import java.util.Set;

public record Patient(String id, String fullName, String nationalId, Set<String> allergies) {

    public Patient {
        allergies = Set.copyOf(allergies);
    }

    public boolean isAllergicTo(String substance) {
        return allergies.contains(substance.toLowerCase());
    }
}
