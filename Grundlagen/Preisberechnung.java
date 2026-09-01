public class Preisberechnung {

    // Original-Methode (fehlerhaft – siehe Bonus-Aufgabe)
    static double calculatePrice(double baseprice, double specialprice, double extraprice, int extras, double discount) {
        double addon_discount;
        double result;

        if (extras >= 3)
            addon_discount = 10;
        else if (extras >= 5)
            addon_discount = 15;
        else
            addon_discount = 0;

        if (discount > addon_discount)
            addon_discount = discount;

        result = baseprice / 100.0 * (100 - discount) + specialprice
                + extraprice / 100.0 * (100 - addon_discount);

        return result;
    }

    // Korrigierte Methode (Bonus-Aufgabe)
    static double calculatePriceFixed(double baseprice, double specialprice, double extraprice, int extras, double discount) {
        double addon_discount;
        double result;

        if (extras >= 5)
            addon_discount = 15;
        else if (extras >= 3)
            addon_discount = 10;
        else
            addon_discount = 0;

        if (discount > addon_discount)
            addon_discount = discount;

        result = baseprice / 100.0 * (100 - discount) + specialprice
                + extraprice / 100.0 * (100 - addon_discount);

        return result;
    }

    static boolean test_calculate_price() {
        double price;
        boolean test_ok = true;

        // Test 1: Keine Extras, kein Rabatt → Nur Grundpreis + Sonderpreis
        price = calculatePrice(20000, 500, 0, 0, 0);
        if (Math.abs(price - 20500.0) > 0.01) {
            System.out.println("FEHLER Test 1: Erwartet 20500.0, bekommen " + price);
            test_ok = false;
        }

        // Test 2: Nur Grundpreis mit 10% Händlerrabatt
        price = calculatePrice(20000, 0, 0, 0, 10);
        if (Math.abs(price - 18000.0) > 0.01) {
            System.out.println("FEHLER Test 2: Erwartet 18000.0, bekommen " + price);
            test_ok = false;
        }

        // Test 3: 2 Extras → kein Zusatzrabatt
        price = calculatePrice(20000, 0, 1000, 2, 0);
        if (Math.abs(price - 21000.0) > 0.01) {
            System.out.println("FEHLER Test 3: Erwartet 21000.0, bekommen " + price);
            test_ok = false;
        }

        // Test 4: 3 Extras → 10% Zusatzrabatt auf Extras
        price = calculatePrice(20000, 0, 1000, 3, 0);
        // 20000 + 1000 * 0.9 = 20900
        if (Math.abs(price - 20900.0) > 0.01) {
            System.out.println("FEHLER Test 4: Erwartet 20900.0, bekommen " + price);
            test_ok = false;
        }

        // Test 5: 5 Extras → 15% Zusatzrabatt auf Extras (nur mit korrigierter Methode!)
        price = calculatePriceFixed(20000, 0, 1000, 5, 0);
        // 20000 + 1000 * 0.85 = 20850
        if (Math.abs(price - 20850.0) > 0.01) {
            System.out.println("FEHLER Test 5: Erwartet 20850.0, bekommen " + price);
            test_ok = false;
        }

        // Test 6: Händlerrabatt höher als Zusatzrabatt → Händlerrabatt setzt sich durch
        price = calculatePrice(20000, 0, 1000, 3, 20);
        // 20000 * 0.8 + 1000 * 0.8 = 16000 + 800 = 16800
        if (Math.abs(price - 16800.0) > 0.01) {
            System.out.println("FEHLER Test 6: Erwartet 16800.0, bekommen " + price);
            test_ok = false;
        }

        // Test 7: Komplett: Grundpreis + Sondermodell + Extras + Rabatte
        price = calculatePrice(25000, 2000, 3000, 4, 5);
        // 25000 * 0.95 + 2000 + 3000 * 0.9 = 23750 + 2000 + 2700 = 28450
        if (Math.abs(price - 28450.0) > 0.01) {
            System.out.println("FEHLER Test 7: Erwartet 28450.0, bekommen " + price);
            test_ok = false;
        }

        if (test_ok) {
            System.out.println("Alle Tests bestanden!");
        }

        return test_ok;
    }

    public static void main(String[] args) {
        System.out.println("=== Testlauf (orig. fehlerhafter Code) ===");
        boolean result = test_calculate_price();
        System.out.println("Ergebnis: " + (result ? "OK" : "FEHLER"));

        System.out.println();
        System.out.println("=== Demonstration des Bugs ===");
        double priceBug = calculatePrice(20000, 0, 1000, 5, 0);
        double priceFix = calculatePriceFixed(20000, 0, 1000, 5, 0);
        System.out.println("5 Extras mit orig. Code:  " + priceBug + " (erwartet 20850.0, da 15% Rabatt)");
        System.out.println("5 Extras mit korrig. Code: " + priceFix + " (korrekt)");
    }
}
