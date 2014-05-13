import co.freeside.betamax.Betamax;
import co.freeside.betamax.Recorder;
import co.freeside.betamax.TapeMode;
import co.freeside.betamax.httpclient.BetamaxRoutePlanner;
import co.freeside.betamax.proxy.jetty.ProxyServer;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import jersey.DefaultRestResourceSupplier;
import jersey.RestResourceSupplier;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BetamaxTest {

    public static final String EXCHANGE_RATE_API_URL = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";

    @Rule
    public Recorder recorder = new Recorder();

    private ProxyServer proxyServer = new ProxyServer(recorder);

    @Betamax(tape = "euro-exchange-response-recording",
             mode = TapeMode.WRITE_ONLY)
    @Test
    public void shouldRecordData() throws IOException {
        HttpResponse response = getHttpResponseUsingApacheDefaultClient();
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    @Betamax(tape = "euro-exchange-response",
            mode = TapeMode.READ_ONLY)
    public void shouldReplayExchangeRateResponseWithDefaultHttpClient() throws Exception {
        HttpResponse response = getHttpResponseUsingApacheDefaultClient();
        assertThat(response.getStatusLine().getStatusCode(), is(201));
    }

    private HttpResponse getHttpResponseUsingApacheDefaultClient() throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        BetamaxRoutePlanner.configure(client);
        HttpGet getMethod = new HttpGet(EXCHANGE_RATE_API_URL);
        return client.execute(getMethod);
    }

    @Test
    @Betamax(tape = "euro-exchange-response",
            mode = TapeMode.READ_ONLY)
    public void shouldReplayResponseUsingSystemDefaultHttpClient() throws Exception {
        DefaultHttpClient client = new SystemDefaultHttpClient();
        HttpGet getMethod = new HttpGet(EXCHANGE_RATE_API_URL);
        HttpResponse response = client.execute(getMethod);
        assertThat(response.getStatusLine().getStatusCode(), is(201));
    }

    @Test
    @Betamax(tape = "euro-exchange-response",
            mode = TapeMode.READ_ONLY)
    public void shouldReplayResponseUsingJerseyClient() throws URISyntaxException {
        RestResourceSupplier supplier = new DefaultRestResourceSupplier(new URI(EXCHANGE_RATE_API_URL));
        WebResource.Builder resource = supplier.get()
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE);

        ClientResponse clientResponse = resource.get(ClientResponse.class);
        assertThat(clientResponse.getClientResponseStatus(), is(ClientResponse.Status.CREATED));
    }
}
