# Anleitung: Bank-Software zum Laufen bringen (Übung 3)

Die Bank-Software liegt bereits im Repository unter `Teststrategie/bank-software/`. Hier gibt es zwei Varianten, sie zum Laufen zu bringen.

**Voraussetzung:**
- Java installiert (mindestens JDK 17; prüfen mit `java -version`)

---

## Variante A: Direkt in der Konsole (empfohlen, schnellster Weg)

Die Dateien liegen bereits fertig entpackt im Repo.

1. Öffne ein Terminal und wechsle in den Projektordner:
   ```
   cd Teststrategie/bank-software
   ```

2. Kompiliere den Quellcode:
   ```
   javac -encoding UTF-8 -d classes -cp ".:./lib/gson/*:./lib/okhttp/*" src/*.java
   ```

3. Starte die Anwendung:
   ```
   java -cp "classes:./lib/gson/*:./lib/okhttp/*" Main
   ```

4. Die Anwendung startet, du siehst die Begrüssung und das Menü. Damit kannst du nun die Black-Box-Testfälle aus der Lösung durchgehen.

**Falls du die Kompilierung zurücksetzen willst**, kannst du den `classes/`-Ordner einfach wieder löschen:
```
rm -rf classes
```

---

## Variante B: Mit IDE und Maven

1. Öffne in deiner IDE (z. B. IntelliJ) den Ordner `Teststrategie/bank-software-mvn/`.
2. Die IDE erkennt die `pom.xml` automatisch als Maven-Projekt und lädt die Abhängigkeiten (GSON, OKHTTP) herunter.
3. Öffne die Klasse `Main` und starte sie mit dem grünen Pfeil.
4. Falls du Maven in der Konsole nutzen willst:
   ```
   cd Teststrategie/bank-software-mvn
   mvn compile exec:java -Dexec.mainClass="ch.tbz.bank.software.Main"
   ```

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
