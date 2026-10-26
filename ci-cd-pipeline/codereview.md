# Codereview – Features

Da die Aufgabe normalerweise zu zweit bearbeitet wird, werden hier zwei Personen simuliert.
Beide setzen dieselben Features unabhängig voneinander um, reviewen sich gegenseitig und
vergleichen anschliessend ihre Lösungen.

| Rolle | Aufgabe |
|---|---|
| **Person A** | Erste Umsetzung von Feature 1 und Feature 2 |
| **Person B** | Zweite, unabhängige Umsetzung von Feature 1 und Feature 2 |
| **Review** | Die jeweils andere Person prüft den Pull Request |

## Feature 1: Neues Rezept hinzufügen (Frontend)

Das bestehende Formular auf der Seite "Add Recipe" wird fertig umgesetzt:

- Rezeptname, Beschreibung und Bild-URL können eingegeben werden.
- Zutaten können hinzugefügt, bearbeitet und entfernt werden.
- Pflichtfelder und Mengen werden validiert.
- Das Rezept wird per `POST /api/recipes` an das Backend gesendet.
- Lade-, Erfolgs- und Fehlerzustände werden angezeigt.

## Feature 2: Rezept bearbeiten (Frontend und Backend)

Im Frontend und Backend wird das Bearbeiten eines bestehenden Rezepts ergänzt:

- Backend: neuer Endpoint `PUT /api/recipes/{recipeId}` inklusive Fehlerbehandlung.
- Frontend: Der Button "Edit Details" öffnet ein vorausgefülltes Formular.
- Name, Beschreibung, Bild-URL und Zutaten können geändert werden.
- Die Änderungen werden per `PUT` gespeichert und sind danach in der Übersicht sichtbar.
- Ungültige IDs und fehlerhafte Eingaben werden verständlich gemeldet.

## Vorgehen

Person A und Person B implementieren zuerst Feature 1 unabhängig voneinander in eigenen
Branches. Nach dem gegenseitigen Review und Merge folgt Feature 2, diesmal im Frontend und
Backend. Auch hier werden die Lösungen getrennt umgesetzt, reviewt und zusammengeführt.
