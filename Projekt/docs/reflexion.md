# Reflexion

Rückblick auf das Projekt Health Leveling: TDD, Code Reviews, Commits, KI-Nutzung und was ich mitnehme.

> Stellen in eckigen Klammern muss ich noch mit meiner eigenen Erfahrung füllen.

## Scope und Zeit

Geplant war ein MVP (Profil, manuelle Eingabe, Import, XP- und Level-Berechnung) und Erweiterungen, falls die Zeit
reicht. Am Schluss sind auch alle Erweiterungen umgesetzt: Google Health API, XP-Diagramm, Streaks und Achievements.

[Wie gut hat die Planung gepasst? Wo habe ich mehr Zeit gebraucht als gedacht, zum Beispiel bei der Google-Anbindung
oder bei OAuth? Hätte ich mit weniger Funktionen mehr Zeit für Tests oder Dokumentation gehabt?]

## Test Driven Development

Die Rechenlogik (XpCalculator, LevelCalculator, StreakCalculator, DayReview) ist nach Red-Green-Refactor entstanden.
Die Regeln standen schon vorher fest, zum Beispiel «Level n auf n+1 braucht 100 mal n XP». Daraus liessen sich die
Testfälle direkt ableiten, vor allem die Grenzwerte.

Wo TDD gut gepasst hat:
- Bei klaren Regeln mit Zahlen. Der Test war schon die Spezifikation.
- Beim Refactoring: Konstanten einführen und doppelte Teile zusammenfassen ging ohne Angst, etwas kaputt zu machen.

Wo TDD schwierig war:
- Bei der Google-Anbindung wusste ich vorher nicht, wie die Antworten aussehen. Zuerst musste ich die echte API mit
  dem api-explorer anschauen. Erst danach konnte ich Tests mit Beispieldateien schreiben.
- Im Frontend war es einfacher, zuerst die Seite zu bauen und dann zu testen.

Was nicht gut lief: Die einzelnen Red-Green-Schritte sieht man in der Git-Historie nicht, weil das Backend in zwei
grossen Commits hochgeladen wurde. In der TDD-Übung (FancyApp) habe ich es mit einem Commit pro Schritt gemacht.
Das wäre auch im Projekt besser gewesen.

[Meine eigene Meinung: Würde ich TDD im Beruf verwenden? Für welche Art von Code ja, für welche nicht?]

## Code Reviews

Backend und Frontend wurden in eigenen Feature-Branches entwickelt und per Pull Request zusammengeführt (PR #5 Backend,
PR #6 Frontend, PR #7 Project auf main).

[Wer hat die Pull Requests reviewt? Welche Kommentare gab es und was habe ich daraufhin geändert? Wenn es keine
Kommentare gab: ehrlich sagen, dass ein Review von einer Person gefehlt hat, und was ich daraus mitnehme.]

Aus der Übung zu Code Reviews nehme ich mit, dass kleine Pull Requests viel einfacher zu reviewen sind. Ein Pull
Request mit über 2000 Zeilen wie beim Backend kann niemand gründlich anschauen.

## Commits

[Ehrlich festhalten: Am Anfang und am Ende gab es kleine Commits, das Backend und das Frontend kamen aber in wenigen
grossen Commits. Was war der Grund? Was mache ich nächstes Mal anders?]

## Pipeline und Reports

Die Pipeline hat drei Stages: Build, Test und Deploy. Die Reports werden direkt in GitHub angezeigt, als Artefakt
gespeichert und bei einem Push auf main auf GitHub Pages veröffentlicht, aber nur wenn alle Tests grün sind.

Zuerst lief die Pipeline nur auf dem Branch Project und ohne E2E-Tests. Gefunden habe ich ausserdem, dass der
Integrationstest die echte Google API aufrufen konnte, wenn auf meinem Rechner das Google-Token gesetzt war. Ein Test,
der je nach Rechner grün oder rot ist, ist genau das, was man vermeiden will. Jetzt werden diese Werte im Test fest
abgeschaltet.

[Was hat mir der Coverage-Report gezeigt, woran ich nicht gedacht hatte?]

## KI-Nutzung

Wie ich KI eingesetzt habe, steht in der [README](../README.md#ki-nutzung).

[Was hat die KI mir gebracht, wo war sie hinderlich? Habe ich durch die KI weniger gelernt als ohne? Konnte ich den
Code am Schluss erklären?]

## Fazit

Am meisten gebracht hat mir die Trennung der Rechenlogik von Datenbank und Google. Dadurch liess sich fast alles mit
schnellen Unit Tests prüfen, und Google musste nur an einer Stelle gemockt werden.

[Zwei bis drei Sätze: Was nehme ich aus dem Modul mit? Was mache ich im nächsten Projekt anders?]
