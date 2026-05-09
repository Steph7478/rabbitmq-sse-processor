package web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import jakarta.validation.Valid;

import adapter.rabbitmq.producer.ProcessingProducer;
import adapter.sse.service.SSEEventService;
import web.output.StatusResponse;
import adapter.product.model.Product;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Path("/api/processing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcessingController {
    
    @Inject
    ProcessingProducer producer;
    
    @Inject
    SSEEventService sseService;
    
    @Inject
    Sse sse;
    
    private final Map<String, StatusResponse> statusCache = new ConcurrentHashMap<>();
    
    @POST
    public Response createProcessing(@Valid Product request) {
        String id = UUID.randomUUID().toString();
        
        StatusResponse status = new StatusResponse(id, StatusResponse.Status.PENDING, "Processing created");
        updateStatus(id, status);
        
        producer.sendMessage(id, request);
        
        return Response.accepted()
                .entity(Map.of("processId", id))
                .build();
    }
    
    @GET
    @Path("/{id}/status")
    public Response getStatus(@PathParam("id") String id) {
        StatusResponse status = statusCache.get(id);
        return status != null 
                ? Response.ok(status).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @GET
    @Path("/{id}/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void streamStatus(@PathParam("id") String id, @Context SseEventSink sink) {
        sseService.addClient(id, sink);
        
        StatusResponse status = statusCache.get(id);
        if (status != null) {
            sseService.sendEvent(id, status);
        }
        
        sink.send(sse.newEventBuilder()
            .name("connected")
            .data("Connected to stream for: " + id)
            .build());
    }
    
    public void updateStatus(String id, StatusResponse status) {
        statusCache.put(id, status);
    }
}