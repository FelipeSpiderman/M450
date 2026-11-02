# Video-Präsentation Projekt Health Leveling (für mich)

Ziel: In etwa 10 Minuten zeigen, dass das Projekt alle Punkte der Bewertungsrubrik und des Checkpoints erfüllt.
Pro Abschnitt steht unter **Zeigen**, was auf dem Bildschirm sein soll, und unter **Sagen**, was ich erzähle. Nicht
Wort für Wort ablesen, sondern in eigenen Worten sagen. Die Lehrperson bewertet bei KI-Nutzung vor allem, ob ich es
verstehe.

## Zuerst: Was ist erfüllt und was noch nicht

Stand heute, geprüft am Branch Project.

| Anforderung | Stand | Wo ich es zeige |
|---|---|---|
| Teststrategie im README.md des Projekts | erfüllt | Projekt/README.md, Abschnitt Testing |
| Unit Tests | erfüllt (Backend 116, Frontend 54) | XpCalculatorTest, validators.spec.ts |
| Integration Tests | erfüllt (12 Tests) | HealthLevelingIntegrationTest, EntryRepositoryTest |
| Schnittstellen gemockt | erfüllt | SyncServiceTest, GoogleHealthClientTest, E2E-Tests |
| Testresultate werden reportet | erfüllt (Backend) | JaCoCo- und Surefire-Report, Artefakt in GitHub Actions |
| Pipeline führt Tests **beim Deploy auf main** aus | **nicht erfüllt** | Pipeline läuft nur auf dem Branch Project, es gibt keine Deploy-Stage, Project ist noch nicht auf main |
| TDD (optional) | teilweise | in der README beschrieben, in der Git-Historie nicht sichtbar |
| KI-Nutzung deklariert | vorhanden, **prüfen** | siehe Hinweis unten |
| Kleine Commits | **schwach** | Backend kam in 2 grossen Commits |
| Code Reviews (3 PRs pro Person, von einer Person kommentiert) | **nicht erfüllt** | bisher nur PR #5 und #6 im Projekt |
| Reflexion über TDD und Code Reviews | **fehlt** | noch nicht geschrieben |
| Scope und Zeit abgeglichen | erfüllt | Tabelle Planung in BESCHREIBUNG.md |

**Vor dem Video erledigen** (Details stehen in OFFENE-PUNKTE.md auf dem Branch Project):
1. Pipeline so anpassen, dass sie auf main läuft und eine Deploy-Stage hat, die die Reports veröffentlicht.
   Danach Project per Pull Request auf main bringen. Das ist der wichtigste offene Rubrik-Punkt.
2. Reflexion schreiben (ein paar Absätze reichen, Fragen siehe Abschnitt 11 unten).
3. KI-Deklaration prüfen: In der README steht, dass ich Komponenten und HTML selbst geschrieben habe und die KI
   vor allem Nachschlagewerk, Research und Styling war. In FEHLENDE_TESTS.md steht dagegen, dass Backend und Frontend
   mit KI geschrieben wurden. Die README muss stimmen, sonst ist der Lernziel-Punkt „transparent deklarieren“ in
   Gefahr. Lieber ehrlich mehr angeben als zu wenig.

Was ich bis zum Video nicht schaffe, sage ich im Video offen (Abschnitt 11). Das ist besser, als es zu verstecken.

## Vorbereitung (vor der Aufnahme)

1. In VS Code auf den Branch Project wechseln.
2. Backend starten (im Ordner health-leveling/backend über den Maven Wrapper) und Frontend starten (im Ordner
   health-leveling/frontend mit npm start). Im Browser localhost:4200 öffnen.
3. Ein paar Beispieldaten erfassen oder eine CSV-Datei importieren (z. B. valid.csv aus den Test-Ressourcen), damit
   das Profil nicht leer ist.
4. Im Backend einmal den Maven-Befehl „verify“ ausführen. Danach liegen die Reports bereit:
   JaCoCo unter target/site/jacoco/index.html, Surefire unter target/reports. Beide im Browser öffnen.
5. Auf GitHub diese Tabs offen haben: Actions (ein grüner Lauf der Projekt-Pipeline mit dem Artefakt),
   Pull Requests (#5 und #6 und alle neuen), die Commit-Historie.
6. In VS Code diese Dateien offen haben: Projekt/README.md (in der Markdown-Vorschau, damit die Diagramme sichtbar
   sind), XpCalculatorTest, SyncServiceTest, GoogleHealthClientTest, HealthLevelingIntegrationTest, e2e/app.spec.ts,
   .github/workflows/projekt.yml.
7. Schriftgrösse hoch stellen.

---

## 1. Einleitung und Idee (ca. 30 Sekunden)

**Zeigen:** README.md oben

**Sagen:**
„Mein Projekt heisst Health Leveling. Ich habe ein Fitbit-Band bekommen und wollte aus meinen Gesundheitsdaten ein
kleines Spiel machen. Wer sich bewegt, gut schläft und regelmässig Einträge macht, sammelt XP und steigt im Level
auf. Im Modul geht es ums Testen, deshalb ist die Applikation eher das Mittel zum Zweck. Der Schwerpunkt liegt auf
den Tests.“

## 2. Scope und Zeit (ca. 30 Sekunden)

**Zeigen:** BESCHREIBUNG.md, Tabelle Planung

**Sagen:**
„Bevor ich angefangen habe, habe ich den Umfang geplant. Zuerst ein MVP mit der XP-Logik, manueller Eingabe und
Import. Die Google-Anbindung war bewusst optional, weil ich nicht wusste, ob ich das zeitlich schaffe. Die Idee war:
lieber weniger Features, dafür gut getestet.“

[Ehrlich ergänzen, wie es tatsächlich gelaufen ist, zum Beispiel ob die Google-Anbindung mehr Zeit gebraucht hat als
geplant.]

## 3. Die Applikation kurz zeigen (ca. 1 Minute)

**Zeigen:** Browser, alle vier Seiten

**Sagen und zeigen:**
- Profil: „Hier sieht man Level, XP, den Fortschritt bis zum nächsten Level, die Schritte-Serie und die
  Achievements.“
- Erfassen: Eine Aktivität erfassen, dann eine ungültige Eingabe zeigen (z. B. Dezimalzahl bei Minuten). „Ungültige
  Eingaben werden erklärt und gar nicht erst gesendet.“
- Tage: „Hier das XP-Diagramm der letzten 14 Tage.“
- Import: „Hier kann ich eine CSV- oder JSON-Datei hochladen, zum Beispiel aus dem Google-Export.“

## 4. Aufbau (ca. 1 Minute)

**Zeigen:** README.md, das erste Diagramm (Aufbau)

**Sagen:**
„Das Frontend ist Angular und spricht nur über einen ApiService mit dem Backend. Das Backend ist Spring Boot. Die
ganze Rechenlogik, also XP, Level und Streak, liegt in kleinen Klassen ohne Datenbank. Das habe ich bewusst so
gemacht, weil man solche Klassen sehr einfach testen kann.

Die Google-Anbindung steckt hinter einem eigenen Interface, dem HealthDataClient. Der Rest der App weiss nicht, dass
dahinter Google ist. Darum kann ich ihn in den Tests einfach ersetzen.

Wichtig ist noch: Das Profil wird nicht gespeichert, sondern bei jeder Abfrage aus den Rohdaten neu berechnet. So
können die XP nie falsch hängen bleiben, wenn ich einen Eintrag lösche oder etwas doppelt importiere.“

## 5. Teststrategie (ca. 1 Minute) – Rubrik: Teststrategie im README

**Zeigen:** README.md, Abschnitt Testing, die Testpyramide

**Sagen:**
„Meine Teststrategie steht im README. Ich habe nach der Testpyramide getestet: unten viele schnelle Unit Tests, in
der Mitte Integration Tests über HTTP mit einer echten Datenbank, oben wenige E2E-Tests im Browser. Insgesamt sind es
189 Tests.

Zwei Regeln waren mir wichtig. Erstens hängt kein Test von etwas Externem ab, also nicht von Google, nicht von meinen
echten Daten und nicht davon, ob das Backend läuft. Zweitens teste ich Grenzwerte immer: genau auf der Grenze, knapp
darunter und knapp darüber.“

## 6. Unit Tests und TDD (ca. 1,5 Minuten) – Rubrik: Unit Tests, TDD

**Zeigen:** XpCalculatorTest, einen parametrisierten Test mit der Tabelle für die Schritte

**Sagen:**
„Das hier ist ein Unit Test für die XP-Berechnung. Statt zehn fast gleiche Tests zu schreiben, habe ich einen
parametrisierten Test mit einer Tabelle gemacht: 0 Schritte gibt 0 XP, 999 gibt 0, 1000 gibt 5, bei 10'000 gibt es
einen Bonus und ab 20'000 wird nicht mehr gezählt. Das sind genau die Grenzwerte.

Die Rechenlogik habe ich mit TDD geschrieben. Die Regeln standen schon vorher fest, zum Beispiel ‚Level n auf n+1
braucht 100 mal n XP‘. Also habe ich zuerst den Test geschrieben, der war rot, dann den Code, bis er grün war, und
dann aufgeräumt.“

**Ehrlich dazusagen:**
„In der Git-Historie sieht man die einzelnen Red-Green-Schritte leider nicht, weil ich das Backend in zwei grossen
Commits hochgeladen habe. In der TDD-Übung habe ich das sauber mit einem Commit pro Schritt gemacht. Im Projekt würde
ich das beim nächsten Mal auch so machen.“

Optional einen Frontend-Test zeigen: validators.spec.ts, „dieselben Grenzen wie im Backend“.

## 7. Mocking (ca. 1,5 Minuten) – Rubrik: Schnittstellen weggemockt

**Zeigen:** nacheinander SyncServiceTest, GoogleHealthClientTest, e2e/app.spec.ts

**Sagen:**
„Ich habe an drei Stellen gemockt.

Erstens Mockito bei den Services. Hier im SyncServiceTest ersetze ich den HealthDataClient durch einen Mock. Ich kann
ihm sagen ‚gib diese zwei Tage zurück‘ oder ‚wirf einen Fehler‘ und dann prüfen, ob der Service richtig reagiert.

Zweitens ein gemockter HTTP-Server beim GoogleHealthClient. Der prüft, ob meine Anfrage die richtige URL und den
richtigen Token hat, und antwortet mit JSON-Dateien. Das Format dieser Dateien habe ich vorher mit dem api-explorer an
der echten API angeschaut. So ist der Test nah an der Realität, ruft Google aber nie wirklich auf.

Drittens im Frontend. In den E2E-Tests beantwortet Playwright alle Anfragen an /api direkt im Browser. Es braucht also
kein laufendes Backend.“

## 8. Integration Tests (ca. 1 Minute) – Rubrik: Integration Tests

**Zeigen:** HealthLevelingIntegrationTest, den Test, in dem importiert wird und dann das Level geprüft wird

**Sagen:**
„Die Integration Tests starten die ganze Spring-Boot-Applikation mit einer H2-Datenbank und schicken echte
HTTP-Anfragen. Hier wird zum Beispiel eine Datei importiert, dann werden Einträge erfasst und am Schluss geprüft, ob
das Profil auf dem richtigen Level ist. Die erwarteten XP habe ich vorher von Hand ausgerechnet. Andere Tests prüfen,
dass ein doppelter Import keine doppelten Tage erzeugt und dass ungültige Eingaben den Status 400 geben.“

## 9. Reports (ca. 1 Minute) – Rubrik: Testresultate reportet

**Zeigen:** Surefire-Report und JaCoCo-Report im Browser

**Sagen:**
„Nach jedem Build gibt es zwei Reports. Der Surefire-Report zeigt alle Tests und ob sie bestanden haben. JaCoCo zeigt
die Abdeckung, ich bin bei rund 99 Prozent der Zeilen und 96 Prozent der Branches. Die Zahl war aber nicht mein Ziel.
Ich habe den Report vor allem gebraucht, um Stellen zu finden, an die ich beim Testen nicht gedacht hatte.“

Im JaCoCo-Report auf eine Klasse klicken und die grünen und gelben Zeilen zeigen.

## 10. Pipeline (ca. 1 Minute) – Rubrik: Pipeline beim Deploy auf main

**Zeigen:** .github/workflows/projekt.yml, danach GitHub Actions mit einem grünen Lauf und dem Artefakt

**Sagen (wenn Punkt 1 der Vorbereitung erledigt ist):**
„Die Pipeline läuft mit GitHub Actions. Sie hat die Stages Build, Test und Deploy. Beim Deploy auf main werden die
Tests automatisch ausgeführt, und nur wenn alles grün ist, werden die Reports veröffentlicht. Hier ist der Link
dazu.“

**Sagen (wenn es noch nicht erledigt ist):**
„Die Pipeline läuft im Moment bei jedem Push auf den Branch Project. Backend und Frontend laufen parallel, und die
Reports werden als Artefakt hochgeladen, auch wenn ein Test fehlschlägt. Was noch fehlt, ist der Lauf auf main mit
einer Deploy-Stage. Die E2E-Tests laufen nur lokal, weil dafür in der Pipeline ein Browser installiert werden
müsste.“

## 11. Code Reviews, Commits und Reflexion (ca. 1,5 Minuten)

**Zeigen:** GitHub Pull Requests, dann die Commit-Historie

**Sagen:**
„Gearbeitet habe ich mit Feature-Branches für Backend und Frontend, die per Pull Request zusammengeführt wurden.“
[Hier zeigen, welche Kommentare es in den PRs gab und was ich daraufhin geändert habe.]

**Reflexion, in eigenen Worten beantworten:**
- Wo hat mir TDD geholfen, wo war es mühsam? (Tipp: Bei der Rechenlogik waren die Regeln klar, da hat es gut
  gepasst. Bei der Google-Anbindung wusste ich vorher nicht, was die API zurückgibt, da ging TDD schlecht.)
- Was hat mir das Code Review gebracht?
- Was würde ich beim nächsten Mal anders machen? (Kleinere Commits, Pipeline von Anfang an auf main.)

## 12. KI-Nutzung und Fazit (ca. 30 Sekunden) – Lernziel: KI transparent deklarieren

**Zeigen:** README.md, Abschnitt KI-Nutzung

**Sagen:**
„Im Projekt war KI erlaubt, wenn man sie deklariert. Hier steht, wofür ich sie benutzt habe.“ [Die Punkte kurz
vorlesen. Sie müssen mit der Wahrheit übereinstimmen.]

„Mein Fazit: Am meisten gebracht hat mir, dass ich die Logik von der Datenbank und von Google getrennt habe. Dadurch
war fast alles einfach testbar. Danke fürs Zuschauen.“

---

## Fragen, die die Lehrperson stellen könnte

- Warum wird das Profil nicht gespeichert? (Abschnitt 4)
- Was ist der Unterschied zwischen dem Mockito-Mock und dem gemockten HTTP-Server? (Mockito ersetzt eine ganze
  Klasse, der HTTP-Server testet den echten Client inklusive URL, Header und JSON-Auswertung.)
- Warum sind 0 Schritte nicht dasselbe wie ein leeres Feld? (0 heisst „ich bin nicht gelaufen“, leer heisst „keine
  Daten“. Beides muss anders behandelt werden.)
- Warum gehört Schlaf nach Mitternacht zum nächsten Tag? (Google liefert UTC, umgerechnet in lokale Zeit zählt der
  Schlaf zum Aufwachtag.)
- Was wird nicht getestet? (Die echte Google-Verbindung, PostgreSQL und das Aussehen der Seiten.)
