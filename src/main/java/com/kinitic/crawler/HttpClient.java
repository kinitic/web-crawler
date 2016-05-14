package com.kinitic.crawler;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.client.ClientBuilder.newClient;

public class HttpClient {

    public String getPageContent(String url) throws ServiceUnavailableException {
        final WebTarget path = newClient().target(url);

        final Response response = path.request("application/html").get();

        if (response.getStatus() != 200) {
            throw new ServiceUnavailableException(format("Problem with calling url: %s", url));
        }
        return response.readEntity(String.class);
    }
}
