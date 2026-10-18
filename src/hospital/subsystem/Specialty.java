package hospital.subsystem;

public enum Specialty {
    THERAPIST,
    CARDIOLOGIST,
    NEUROLOGIST,
    SURGEON;

    public String displayName() {
        return name().toLowerCase();
    }
}
