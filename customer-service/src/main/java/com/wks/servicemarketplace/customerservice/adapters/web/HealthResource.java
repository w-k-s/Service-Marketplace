package com.wks.servicemarketplace.customerservice.adapters.web;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.wks.servicemarketplace.customerservice.adapters.web.response.HealthCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/health")
public class HealthResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthResource.class.getSimpleName());

    private final HealthCheckRegistry healthCheckRegistry;

    @Inject
    public HealthResource(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() {
        final List<String> failedHealthChecks = healthCheckRegistry.runHealthChecks()
                .entrySet()
                .stream()
                .filter(it -> !it.getValue().isHealthy())
                .peek( it -> LOGGER.error(it.getValue().getMessage(), it.getValue().getError()) )
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (failedHealthChecks.isEmpty()) {
            return Response.ok().build();
        }

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(HealthCheckResponse.of(failedHealthChecks))
                .build();
    }
}
