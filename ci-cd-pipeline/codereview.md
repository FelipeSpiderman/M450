# Codereview – Add Recipe und Edit Recipe

**Team:** Person A und Person B (simuliert) · **Projekt:** Recipe Planner (Frontend + Backend)

## Kurzfassung

Beide Aufgaben wurden unabhängig umgesetzt, je per Pull Request eingereicht und gegenseitig
reviewt. Aufgabe 1 betraf nur das Frontend, Aufgabe 2 zusätzlich das Backend. Beide Lösungen
funktionieren, unterscheiden sich aber in Aufbau, Fehlerbehandlung und Validierung. Genau diese
Unterschiede waren der interessanteste Teil der Reviews.

## Vorgehen

- Gemeinsamer Startpunkt war `main`, jede Person arbeitete auf einem eigenen Branch.
- Pro Feature ein Pull Request, bei Aufgabe 2 je einer für Frontend und Backend.
- Review durch die andere Person mit den Inputs aus der Theorie: Lesbarkeit, Benennung,
  Validierung, Fehlerfälle, Wiederverwendung und Tests.
- Die Rückmeldungen wurden zuerst einzeln gesammelt, danach verglichen und diskutiert.
- Gemergt wurde erst, nachdem die wichtigsten Punkte eingearbeitet waren.

| Aufgabe | Person | Branch |
|---|---|---|
| 1 – Add Recipe | Person A | `feat/add-recipe-a` |
| 2 – Edit Recipe | Person A | `feat2-a` |
| 2 – Edit Recipe | Person B | `feat2-b` |

## Aufgabe 1 – Neues Rezept hinzufügen (Frontend)

Umgesetzt wurden ein Formular für Name, Beschreibung und Bild-URL, dynamische Zutatenzeilen
sowie eine Validierung vor dem Absenden. Gespeichert wird über `POST /api/recipes`, mit
Lade-, Erfolgs- und Fehlermeldungen.

**Review-Fokus:** Formularzustand, Pflichtfelder, Zutatenmenge, Fehlermeldungen und das
Verhalten nach dem Speichern.

**Wichtigste Findings:**

- Pflichtfelder und die Menge (ganze Zahl grösser als 0) müssen geprüft werden, bevor gesendet wird.
- Eine leere Zutatenliste darf nicht abgeschickt werden.
- Nach dem Speichern braucht es eine klare Rückmeldung, sonst wird doppelt abgeschickt.
- Zutatenzeilen brauchen stabile Keys, sonst springen die Eingaben beim Löschen.

## Aufgabe 2 – Rezept bearbeiten (Frontend und Backend)

Beide Personen haben Frontend und Backend selbst umgesetzt. Der API-Vertrag war vorab
abgesprochen: `PUT /api/recipes/{recipeId}` gibt das aktualisierte Rezept zurück, `404` bei
unbekannter ID und `400` bei leerem Namen.

| Aspekt | Person A | Person B |
|---|---|---|
| Einstieg | eigene Seite `/recipes/:recipeId/edit` | Modal direkt auf der Übersicht |
| HTTP-Client | `axios` | `fetch` |
| Formularzustand | verteilte States, eigene Zutaten-Komponente | ein `formData`-Objekt, zentrale Validierung |
| Backend-Fehler | `ResponseStatusException` im Service | `Optional<Recipe>` und `ResponseEntity` im Controller |
| Nach dem Speichern | Erfolgsmeldung mit Link zur Übersicht | Modal schliesst, Liste wird direkt aktualisiert |
| Tests | Controller- und Service-Tests | gleiche Fälle, anderer Rückgabetyp |

**Review-Fokus:** Fehlerbehandlung bei `404` und `400`, doppelte Validierung, Umgang mit
Nebenwirkungen und die Frage, wo Logik hingehört.

**Wichtigste Findings:**

- `404` und `400` müssen getrennt behandelt und im Frontend verständlich angezeigt werden.
- Validierung im Frontend ist Komfort, die Wahrheit liegt im Backend.
- Der Controller soll keine Geschäftslogik enthalten – nur HTTP und Statuscodes.
- Beim Speichern muss der Button gesperrt werden, sonst sind Doppelklicks möglich.
- Beide Varianten nutzen dieselbe API und sind deshalb austauschbar.

## Vergleich der Review-Inputs

Die Rückmeldungen deckten sich bei den Basics: Validierung, Fehlerfälle, Tests und Lesbarkeit.
Unterschiedlich beurteilt wurden die Architekturfragen. Person A bevorzugte eine eigene Seite,
weil sie verlinkbar und besser testbar ist. Person B bevorzugte das Modal, weil der Kontext der
Übersicht erhalten bleibt und weniger Navigation nötig ist. Beim Backend war die Diskussion
ähnlich: Exceptions sind kürzer und Spring-typisch, `Optional` macht die mögliche Abwesenheit
explizit. Beide Ansätze sind vertretbar – entscheidend ist, dass sie konsequent umgesetzt und
getestet sind.

## Erkenntnisse

1. **API-Vertrag zuerst festlegen.** Dann bleiben zwei unabhängige Lösungen kompatibel.
2. **Fehlerfälle früh durchdenken.** `404`, `400` und leere Eingaben fallen sonst erst beim Testen auf.
3. **Klein und klar schneiden.** Kleine Komponenten und ein Pull Request pro Thema erleichtern das Review.
4. **Theorie-Inputs helfen.** Struktur statt Geschmack kommentieren führt zu besseren Diskussionen.
5. **Geteilte Dateien sind Konfliktpunkte.** `Browse`, `Recipe`, Controller und Service früh absprechen.
6. **Tests zeigen Denkweisen.** Gleiche Testfälle bei beiden Varianten machen Unterschiede sichtbar.
7. **Beide Wege dürfen bestehen.** Die Diskussion war wertvoller als eine erzwungene einheitliche Lösung.

## Hinweis zum Stand im Repository

Aufgabe 1 liegt als Umsetzung von Person A auf `feat/add-recipe-a` vor; eine separate
Umsetzung von Person B wurde für dieses Feature nicht abgelegt. Der Review dazu fand direkt
am Pull Request von Person A statt. Aufgabe 2 ist mit beiden Varianten vorhanden (`feat2-a`
und `feat2-b`). Backend-Tests: 18 erfolgreich, Frontend-Build erfolgreich.
