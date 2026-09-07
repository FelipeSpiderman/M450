# Bank-Simulation – Funktionsweise und Zusammenhänge

## Überblick

Die Bank-Simulation ist eine kleine Konsolen-Anwendung im Ordner `src/main/java/ch/schule`. Sie bildet ein einfaches Banksystem ab: Es gibt eine Bank, verschiedene Konto-Typen und Buchungen (Ein- und Auszahlungen). Alle Beträge werden intern in **Millirappen** (1/100'000 CHF) als ganze Zahlen (`long`) verwaltet – so vermeidet man Rundungsfehler von `double`.

Das Klassendiagramm liegt im Projekt unter `Design/bank6_klassendiagramm.png`.

---

## Die Klassen und ihre Rollen

| Klasse | Art | Rolle |
|--------|-----|-------|
| `Account` | abstrakt | Basisklasse aller Konten. Verwaltet Kontonummer (`id`), Kontostand (`balance`) und die Liste der Buchungen. |
| `SavingsAccount` | Konto | Sparkonto. Erbt von `Account`. Ein Abheben ins Minus ist nicht erlaubt. |
| `SalaryAccount` | Konto | Lohnkonto. Erbt von `Account`. Darf bis zu einer Kreditlimite (negative Zahl) ins Minus gehen. |
| `PromoYouthSavingsAccount` | Konto | Jugend-Sparpromo. Erbt von `SavingsAccount`. Bei jeder Einzahlung gibt es 1% Bonus obendrauf. |
| `Booking` | Hilfsklasse | Eine einzelne Buchung mit Datum und Betrag (bei Auszahlungen negativ). |
| `Bank` | Verwaltung | Verwaltet alle Konten in einer `TreeMap` (sortiert nach Kontonummer) und erzeugt neue Konten. |
| `BankUtils` | Hilfsklasse (statisch) | Formatiert Datum und Beträge für die Ausgabe. |
| `AccountBalanceComparator` | Sortierer | Sortiert Konten absteigend nach Kontostand (für Top 5). |
| `AccountInverseBalanceComparator` | Sortierer | Sortiert Konten aufsteigend nach Kontostand (für Bottom 5). |
| `Main` | Einstieg | Startet die Anwendung. |

---

## Die Vererbungsstruktur

```
Account  (abstrakt)
├── SavingsAccount
│   └── PromoYouthSavingsAccount
└── SalaryAccount
```

- `SavingsAccount` erbt die komplette Konto-Logik von `Account` und blockiert nur das Überziehen (ein `withdraw` wird verweigert, wenn das Saldo nicht ausreicht).
- `PromoYouthSavingsAccount` erbt die Sparkonto-Regeln und setzt zusätzlich den Einzahlungs-Bonus um.
- `SalaryAccount` erbt ebenfalls von `Account`, erlaubt aber ein Minus-Guthaben bis zur Kreditlimite.

---

## Die wichtigsten Zusammenhänge

### 1. Bank → Account (eine Bank, viele Konten)

Die `Bank` hält alle Konten in einer `TreeMap<String, Account>`. Der Schlüssel ist die Kontonummer:

- Sparkonto: `S-1000`, `S-1001`, ...
- Promo-Jugendsparkonto: `Y-1000`, `Y-1001`, ...
- Lohnkonto: `P-1000`, `P-1001`, ...

Die nächste freie Nummer wird intern von `nextAccountId` hochgezählt (Start bei 1000).

### 2. Account → Booking (ein Konto, viele Buchungen)

Jedes Konto führt seine eigenen Buchungen in einer Liste. Eine Einzahlung legt eine Buchung mit positivem Betrag an, eine Auszahlung eine mit negativem Betrag. `canTransact(date)` stellt sicher, dass Buchungen nur in zeitlicher Reihenfolge eingetragen werden (das Datum einer neuen Buchung darf nicht älter sein als das der letzten).

### 3. Bank ↔ Account (Operationen pro Konto)

Die Bank ist die äussere Schnittstelle: `deposit(id, date, amount)`, `withdraw(id, date, amount)`, `getBalance(id)`. Sie sucht das Konto in der Map, delegiert die eigentliche Arbeit an das Konto und meldet Fehler (unbekanntes Konto) mit `false` zurück.

### 4. Das Bonus-Prinzip beim Jugendkonto

Bei `PromoYouthSavingsAccount` wird bei einer Einzahlung zuerst der Bonus berechnet (`amount / 100`, also 1%) und zum Betrag addiert, bevor die Einzahlung an das `SavingsAccount`-Urteil weitergegeben wird.

---

## Typischer Ablauf

1. `new Bank()` legt eine leere Bank an (`nextAccountId = 1000`).
2. `bank.createSavingsAccount()` erzeugt `S-1000` und legt es ab.
3. `bank.deposit("S-1000", 13576, 12000)` sucht das Konto und ruft `konto.deposit(13576, 12000)` auf.
4. Das Konto prüft: Ist der Betrag negativ? Nein. Ist das Datum erlaubt? Ja. Dann wird das Saldo erhöht und eine `Booking` angelegt.
5. `bank.getBalance("S-1000")` liefert den aktuellen Kontostand (hier 12000 Millirappen).
6. `bank.printTop5()` sortiert alle Konten absteigend nach Saldo und gibt die fünf höchsten aus.

---

## Wichtige Details zur Logik

| Thema | Verhalten |
|-------|-----------|
| Einzahlung mit negativem Betrag | Abgelehnt (`false`), Saldo bleibt unverändert. |
| Auszahlung mit negativem Betrag | Abgelehnt (`false`). |
| Auszahlung mit altem Datum | Abgelehnt (`false`). |
| Sparkonto ins Minus | Verweigert, wenn Betrag grösser als das Saldo. |
| Lohnkonto ins Minus | Erlaubt bis zur Kreditlimite, sonst verweigert. |
| Unbekannte Kontonummer bei der Bank | `deposit`/`withdraw` liefern `false`, `getBalance` liefert 0. |
| Gesamtbilanz der Bank | `getBalance()` ist die negative Summe aller Kontosalden (aus Sicht der Bank eine Verbindlichkeit gegenüber den Kunden). |
| Bonus beim Jugendkonto | Bei Einzahlung 1% obendrauf (z. B. 10000 → 10100). |