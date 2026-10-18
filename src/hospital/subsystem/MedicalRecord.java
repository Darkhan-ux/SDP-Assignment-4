package hospital.subsystem;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    private final String recordId;
    private final String patientId;
    private final String complaint;
    private final List<String> notes = new ArrayList<>();
    private String diagnosis;
    private boolean open = true;

    MedicalRecord(String recordId, String patientId, String complaint) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.complaint = complaint;
    }

    public String recordId() {
        return recordId;
    }

    public String patientId() {
        return patientId;
    }

    public String complaint() {
        return complaint;
    }

    public String diagnosis() {
        return diagnosis;
    }

    public List<String> notes() {
        return List.copyOf(notes);
    }

    public boolean isOpen() {
        return open;
    }

    void addNote(String note) {
        notes.add(note);
    }

    void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    void close() {
        open = false;
    }
}
