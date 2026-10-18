package hospital.subsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Subsystem #5: hospital pharmacy. Dispenses medicines only if all of them
 * are in stock and none conflicts with the patient's allergies.
 * Dispensing is all-or-nothing: stock is changed only after every check passes.
 */
public class Pharmacy {

    private final Map<String, Medicine> catalog = new HashMap<>();
    private final Map<String, Integer> stock = new HashMap<>();

    public void addMedicine(String name, String activeSubstance, int unitPrice, int quantity) {
        catalog.put(name, new Medicine(name, activeSubstance.toLowerCase(), unitPrice));
        stock.merge(name, quantity, Integer::sum);
    }

    public List<DispensedMedicine> dispense(List<String> medicineNames, Set<String> patientAllergies) {
        checkCanDispense(medicineNames, patientAllergies);

        List<DispensedMedicine> dispensed = medicineNames.stream()
                .map(name -> {
                    stock.merge(name, -1, Integer::sum);
                    return new DispensedMedicine(name, catalog.get(name).unitPrice());
                })
                .toList();
        log("Dispensed " + medicineNames);
        return dispensed;
    }

    public int stockOf(String medicineName) {
        return stock.getOrDefault(medicineName, 0);
    }

    public List<String> medicinesBelow(int threshold) {
        return stock.entrySet().stream()
                .filter(entry -> entry.getValue() < threshold)
                .map(entry -> entry.getKey() + " (" + entry.getValue() + " left)")
                .sorted()
                .toList();
    }

    private void checkCanDispense(List<String> medicineNames, Set<String> patientAllergies) {
        Map<String, Long> requestedQuantities = medicineNames.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for (Map.Entry<String, Long> request : requestedQuantities.entrySet()) {
            String name = request.getKey();
            Medicine medicine = catalog.get(name);
            if (medicine == null) {
                throw new MedicineUnavailableException(name, "not in the catalog");
            }
            if (stockOf(name) < request.getValue()) {
                throw new MedicineUnavailableException(name, "only " + stockOf(name) + " left in stock");
            }
            if (patientAllergies.contains(medicine.activeSubstance())) {
                throw new AllergyConflictException(name, medicine.activeSubstance());
            }
        }
    }

    private void log(String message) {
        System.out.println("    [Pharmacy] " + message);
    }

    private record Medicine(String name, String activeSubstance, int unitPrice) {
    }
}
