package hospital.facade;

import java.util.List;

public record HospitalStatusReport(int registeredPatients,
                                   long patientsInTreatment,
                                   List<String> doctorWorkload,
                                   List<String> medicinesRunningLow) {

    @Override
    public String toString() {
        StringBuilder report = new StringBuilder()
                .append("      registered patients: ").append(registeredPatients).append('\n')
                .append("      in treatment now:    ").append(patientsInTreatment).append('\n')
                .append("      doctors:\n");
        doctorWorkload.forEach(line -> report.append("        - ").append(line).append('\n'));
        report.append("      medicines running low: ")
                .append(medicinesRunningLow.isEmpty() ? "none" : medicinesRunningLow);
        return report.toString();
    }
}
