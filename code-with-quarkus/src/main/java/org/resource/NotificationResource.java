package org.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.dto.NotificationResponse;
import org.service.NotificationService;

import java.util.Map;

@Slf4j
@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    NotificationService notificationService;

    @POST
    public Response sendNotification(NotificationRequest request) {
        log.info("POST запрос: " + request.getId());
        NotificationResponse response = notificationService.processNotification(request);
        return Response.status(202).entity(response).build();
    }

    @GET
    @Path("/test")
    public Response test() {
        Map<String, Object> response = notificationService.createTestResponse();
        return Response.ok(response).build();
    }

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok(notificationService.checkHealth()).build();
    }

    @GET
    @Path("/types")
    public Response types() {
        return Response.ok(notificationService.getNotificationTypes()).build();
    }
}
