# Anleitung: Bank-Software zum Laufen bringen (Übung 3)

Die Bank-Software ist als Maven-Projekt unter `Teststrategie/bank-software-mvn/` im Repository. Maven lädt die Abhängigkeiten (GSON, OKHTTP) automatisch herunter – die JAR-Files müssen nicht manuell installiert werden.

**Voraussetzung:**
- Java installiert (mindestens JDK 17; prüfen mit `java -version`)
- Maven installiert (prüfen mit `mvn --version`)

---

## Variante A: Mit Maven in der Konsole

1. Öffne ein Terminal und wechsle in den Projektordner:
   ```
   cd Teststrategie/bank-software-mvn
   ```

2. Kompiliere und starte die Anwendung:
   ```
   mvn compile
   java -cp target/classes main.java.ch.tbz.bank.software.Main
   ```

3. (Optional) Alle Unit-Tests ausführen:
   ```
   mvn test
   ```

4. Die Anwendung startet, du siehst die Begrüssung und das Menü. Damit kannst du nun die Black-Box-Testfälle aus der Lösung durchgehen.

---

## Variante B: Mit IDE und Maven

1. Öffne in deiner IDE (z. B. IntelliJ) den Ordner `Teststrategie/bank-software-mvn/`. Wähle beim Öffnen die `pom.xml` an, damit das Projekt als Maven-Projekt importiert wird.
2. Die IDE erkennt die `pom.xml` automatisch und lädt die Abhängigkeiten (GSON, OKHTTP) herunter.
3. Öffne die Klasse `Main` und starte sie mit dem grünen Pfeil.

---

## Menüübersicht

**Hauptmenü** (nach dem Start):

| Eingabe | Aktion |
|---------|--------|
| Kontonummer (z. B. 1) | Konto auswählen und bearbeiten |
| `a` | Alle Konten anzeigen |
| `e` | Neues Konto erstellen |
| `w` | Wechselkurs abfragen (braucht Internet) |
| `q` | Programm beenden |

**Bearbeitungsmenü** (nach Kontoauswahl):

| Eingabe | Aktion |
|---------|--------|
| `e` | Einzahlen |
| `a` | Abheben |
| `k` | Kontostand abfragen |
| `ü` | Auf ein anderes Konto überweisen |
| `l` | Konto löschen (mit Bestätigung) |
| `w` | Konto wechseln |
| `q` | Beenden |