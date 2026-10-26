# Codereview – Zwei Personen, zwei Features

Da die Aufgabe normalerweise zu zweit bearbeitet wird, werden hier zwei Personen simuliert.
Beide setzen dieselben Features um – aber jede Person entwickelt ihre eigene, vollständige
Lösung. Die Lösungen bauen nicht aufeinander auf und benötigen den Code der anderen Person
nicht.

| Rolle | Aufgabe |
|---|---|
| **Person A** | Eigene Umsetzung von Feature 1 und Feature 2 |
| **Person B** | Eigene, unabhängige Umsetzung von Feature 1 und Feature 2 |
| **Review** | Die jeweils andere Person prüft den Pull Request |

## Feature 1: Neues Rezept hinzufügen (Frontend)

Jede Person implementiert das Feature im Frontend vollständig selbst:

- Rezeptname, Beschreibung und Bild-URL können eingegeben werden.
- Zutaten können hinzugefügt, bearbeitet und entfernt werden.
- Pflichtfelder und Mengen werden validiert.
- Das Rezept wird per `POST /api/recipes` an das Backend gesendet.
- Lade-, Erfolgs- und Fehlerzustände werden angezeigt.

**Person A** und **Person B** arbeiten dafür in getrennten Branches und erstellen je einen
eigenen Pull Request. Keine der beiden Lösungen verändert oder benötigt die andere.

## Feature 2: Rezept bearbeiten (Frontend und Backend)

Jede Person implementiert das Feature im Frontend und im Backend vollständig selbst:

- Backend: neuer Endpoint `PUT /api/recipes/{recipeId}` inklusive Fehlerbehandlung.
- Frontend: Der Button "Edit Details" öffnet ein vorausgefülltes Formular.
- Name, Beschreibung, Bild-URL und Zutaten können geändert werden.
- Die Änderungen werden per `PUT` gespeichert und sind danach in der Übersicht sichtbar.
- Ungültige IDs und fehlerhafte Eingaben werden verständlich gemeldet.

**Person A** und **Person B** bauen jeweils ihre eigene Frontend- und Backend-Lösung, die
zusammen funktioniert, ohne auf die Lösung der anderen Person angewiesen zu sein. Beide
erstellen getrennte Pull Requests für Frontend und Backend.

## Vorgehen

Person A und Person B starten vom gleichen Stand. Zuerst wird Feature 1 von beiden
unabhängig umgesetzt, gegenseitig reviewt und zusammengeführt. Danach folgt Feature 2
auf dieselbe Weise, diesmal mit Frontend und Backend.
