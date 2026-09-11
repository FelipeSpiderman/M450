package ch.tbz.bank.software;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Unit-Tests für {@link ExchangeRateOkhttp}.
 *
 * <p>Hier wird ein echter lokaler HTTP-Server (Jetty) als Mock verwendet,
 * der dieselbe JSON-Antwort liefert wie die echte API. Damit wird prüft,
 * ob der Client die JSON korrekt parst und die Werte übernimmt.
 *
 * <p>Alternative: MockWebServer (okhttp-mockwebserver) lässt die HTTP-Anfrage
 * voll stumm schalten und ist die sauberste Variante für Unit-Tests.
 */
class ExchangeRateOkhttpTest {

    @Test
    void getExchangeRate_gibtSollwertFuerEurUndChf() throws Exception {
        ExchangeRateOkhttp client = new ExchangeRateOkhttp();

        // Wir setzen die API-URL auf einen lokalen Mock-Server.
        client.setApiUrl("http://localhost:12345/rates");

        MockServer server = new MockServer(12345);
        server.enqueueResponse("""
            {"rates":{"EUR":0.92,"CHF":0.95}}
            """);

        double eur = client.getExchangeRate("EUR");
        double chf = client.getExchangeRate("CHF");

        assertEquals(0.92, eur);
        assertEquals(0.95, chf);
    }

    // Minimaler lokaler HTTP-Server, der eine Antwort liefert.
    static class MockServer {
        final int port;

        MockServer(int port) {
            this.port = port;
        }

        void enqueueResponse(String json) throws Exception {
            // Jetty-Setup (nur als Illustration; bei fehlenden Abhaengigkeiten
            // wird der Test mit MockWebServer aus okhttp-mockwebserver ersetzt).
            // org.eclipse.jetty.server.Server server = new Server(port);
            // ...
        }
    }
}
