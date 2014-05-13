package jersey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URI;

public final class DefaultRestResourceSupplier implements RestResourceSupplier {

    private final WebResource resource;

    public DefaultRestResourceSupplier(URI baseUri) {
        resource = createResource(baseUri);
    }

    @Override
    public WebResource get() {
        return resource;
    }

    private WebResource createResource(URI baseUri) {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        clientConfig.getSingletons().add(mapper);
        return Client.create(clientConfig).resource(baseUri);
    }

}
