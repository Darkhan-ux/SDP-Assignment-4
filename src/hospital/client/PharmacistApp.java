package hospital.client;

import hospital.subsystem.AllergyConflictException;
import hospital.subsystem.Pharmacy;

import java.util.List;
import java.util.Set;

/**
 * "Advanced" client that bypasses the facade on purpose.
 * A pharmacist only needs the pharmacy, so they use the subsystem class directly.
 * This shows that subsystem classes do not depend on the facade and stay usable on their own.
 */
public class PharmacistApp {

    public static void main(String[] args) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.addMedicine("Paracetamol", "paracetamol", 600, 6);
        pharmacy.addMedicine("Amoxiclav", "penicillin", 3_500, 2);

        System.out.println("Walk-in customer buys paracetamol without being admitted:");
        pharmacy.dispense(List.of("Paracetamol", "Paracetamol"), Set.of());
        System.out.println("Paracetamol left: " + pharmacy.stockOf("Paracetamol"));

        System.out.println("Customer allergic to penicillin asks for Amoxiclav:");
        try {
            pharmacy.dispense(List.of("Amoxiclav"), Set.of("penicillin"));
        } catch (AllergyConflictException refused) {
            System.out.println("Refused: " + refused.getMessage());
        }

        System.out.println("Medicines to reorder: " + pharmacy.medicinesBelow(5));
    }
}
