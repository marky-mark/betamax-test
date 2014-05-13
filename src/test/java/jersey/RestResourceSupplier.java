package jersey;

import com.google.common.base.Supplier;
import com.sun.jersey.api.client.WebResource;

public interface RestResourceSupplier extends Supplier<WebResource> {
}
