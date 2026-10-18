package hospital.subsystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Subsystem #4: stores medical records. A patient may have only one open
 * record at a time; a record can be closed only after a diagnosis is set.
 */
public class MedicalRecords {

    private final Map<String, MedicalRecord> recordsById = new HashMap<>();
    private int nextRecordNumber = 1;

    public MedicalRecord open(String patientId, String complaint) {
        boolean alreadyOpen = recordsById.values().stream()
                .anyMatch(record -> record.patientId().equals(patientId) && record.isOpen());
        if (alreadyOpen) {
            throw new RecordAlreadyOpenException(patientId);
        }

        String recordId = String.format("MR-%04d", nextRecordNumber++);
        MedicalRecord record = new MedicalRecord(recordId, patientId, complaint);
        recordsById.put(recordId, record);
        log("Opened " + recordId + " for " + patientId + ": \"" + complaint + "\"");
        return record;
    }

    public void addNote(String recordId, String note) {
        requireOpenRecord(recordId).addNote(note);
    }

    public void setDiagnosis(String recordId, String diagnosis) {
        if (diagnosis == null || diagnosis.isBlank()) {
            throw new IllegalArgumentException("Diagnosis must not be empty");
        }
        requireOpenRecord(recordId).setDiagnosis(diagnosis);
        log("Diagnosis for " + recordId + ": " + diagnosis);
    }

    public void close(String recordId) {
        MedicalRecord record = requireOpenRecord(recordId);
        if (record.diagnosis() == null) {
            throw new IllegalStateException("Record " + recordId + " cannot be closed without a diagnosis");
        }
        record.close();
        log("Closed " + recordId);
    }

    public void delete(String recordId) {
        if (recordsById.remove(recordId) != null) {
            log("Deleted " + recordId);
        }
    }

    public long openRecordCount() {
        return recordsById.values().stream().filter(MedicalRecord::isOpen).count();
    }

    private MedicalRecord requireOpenRecord(String recordId) {
        MedicalRecord record = recordsById.get(recordId);
        if (record == null || !record.isOpen()) {
            throw new IllegalStateException("No open record with id " + recordId);
        }
        return record;
    }

    private void log(String message) {
        System.out.println("    [MedicalRecords] " + message);
    }
}
