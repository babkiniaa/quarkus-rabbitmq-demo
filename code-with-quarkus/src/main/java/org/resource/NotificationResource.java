package org.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.dto.NotificationResponse;
import org.service.NotificationService;

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

}
