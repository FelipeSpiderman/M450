# Anleitung: Bank-Software zum Laufen bringen (Übung 3)

Diese Anleitung zeigt, wie du die Bank-Software aus der Teststrategie-Übung 3 lokal aufsetzt, damit du sie als Benutzer testen kannst. Du hast zwei Möglichkeiten – wähle die, die zu deinem Rechner passt.

**Was du brauchst:**
- Java ist installiert (mindestens JDK 17; prüfen mit `java -version`)
- Die drei Zip-Dateien von GitLab (heruntergeladen unter `Unterlagen/teststrategie`):
  - `src.zip` (Quellcode)
  - `gson.zip` (JAR für GSON, wird für JSON benötigt)
  - `okhttp.zip` (JARs für OKHTTP, wird für den Wechselkurs-Aufruf benötigt)
- Variante B zusätzlich: Maven installiert (prüfen mit `mvn -version`) – oder eine IDE, die Maven kann (z. B. IntelliJ)

---

## Variante A: Mit Maven und IDE (einfacher Weg)

Diese Variante ist am einfachsten, weil du keine JAR-Dateien von Hand einrichten musst.

1. Lade die Datei **`bank-software-mvn.zip`** von GitLab herunter.
2. Entpacke die Zip-Datei in einen Ordner deiner Wahl (z. B. ins eigne Projektverzeichnis bei M450).
3. Öffne den entpackten Ordner `bank-software-mvn` in deiner IDE (z. B. IntelliJ).
4. Die IDE erkennt die Datei `pom.xml` automatisch als Maven-Projekt und lädt die Abhängigkeiten GSON und OKHTTP herunter. Warte, bis der Import abgeschlossen ist.
5. Öffne die Klasse `Main` und starte sie mit Klick auf den grünen Pfeil (Run).
6. Die Anwendung startet im Konsolenfenster – du kannst sie jetzt mit dem Menü testen.

Falls du Maven direkt in der Konsole verwenden willst, kannst du die Anwendung alternativ so starten:

```
mvn compile exec:java -Dexec.mainClass="ch.tbz.bank.software.Main"
```

---

## Variante B: Ohne Maven, direkt mit der Konsole

Diese Variante kommst du ohne Maven aus, brauchst dafür aber die JAR-Dateien.

1. Lade die drei Zip-Dateien **`src.zip`**, **`gson.zip`** und **`okhttp.zip`** herunter.
2. Lege einen Projektordner an, z. B. `bank-software`, mit den Unterordnern `src` und `lib`.
3. Entpacke den Inhalt von `src.zip` in den Ordner `src`. Es liegen dann die Java-Dateien direkt im `src`-Ordner (Account.java, Bank.java, Counter.java, ExchangeRateOkhttp.java, Main.java).
4. Entpacke `gson.zip` in den `lib`-Ordner. Es enthält die Datei `gson/gson-2.8.2.jar`.
5. Entpacke `okhttp.zip` ebenfalls in den `lib`-Ordner. Es enthält den Ordner `okhttp` mit mehreren JAR-Dateien (okhttp, okio, kotlin-stdlib usw.).

Deine Struktur sollte danach so aussehen:

```
bank-software/
├── src/
│   ├── Account.java
│   ├── Bank.java
│   ├── Counter.java
│   ├── ExchangeRateOkhttp.java
│   └── Main.java
└── lib/
    ├── gson/
    │   └── gson-2.8.2.jar
    └── okhttp/
        ├── okhttp-5.0.0-alpha.10.jar
        ├── okohio... (mehrere JARs)
        └── ...
```

6. Kompiliere den Quellcode. Wechsle dazu ins Terminal in den Projektordner und gib ein:

```
javac -cp ".:./lib/gson/*:./lib/okhttp/*" src/*.java
```

7. Starte die Anwendung:

```
java -cp ".:./lib/gson/*:./lib/okhttp/*" src/Main
```

Genauer gesagt müssen die `class`-Dateien im richtigen Pfad liegen (sie werden beim Kompilieren direkt neben die `.java`-Dateien in `src` geschrieben). Falls es bei dir nicht klappt, kannst du die `class`-Dateien auch in einen separaten Ordner `classes` schreiben lassen:

```
javac -d classes -cp ".:./lib/gson/*:./lib/okhttp/*" src/*.java
java -cp "classes:./lib/gson/*:./lib/okhttp/*" Main
```

8. Die Anwendung startet und du kannst sie nun mit dem Menü testen.

---

## So bedienst du die Anwendung

Beim Start werden fünf Beispielkonten angelegt. Danach erscheint das Hauptmenü:

- **Kontonummer** (z. B. `1`) – Konto auswählen und bearbeiten
- **`a`** – alle Konten anzeigen
- **`e`** – neues Konto erstellen
- **`w`** – Wechselkurs abfragen (braucht Internetzugang, der API-Schlüssel ist bereits im Code hinterlegt)
- **`q`** – Programm beenden

Im Bearbeitungsmenü eines Kontos gilt dann:

- **`e`** – einzahlen
- **`a`** – abheben
- **`k`** – Kontostand abfragen
- **`ü`** – auf ein anderes Konto überweisen
- **`l`** – Konto löschen
- **`w`** – Konto wechseln
- **`q`** – beenden

Tipp für die Abgabe: Wenn du die App laufen hast, kannst du die Black-Box-Testfälle aus meinem Lösungsteil durchgehen und die Spalte «Effektives Resultat» in der Tabelle ausfüllen.