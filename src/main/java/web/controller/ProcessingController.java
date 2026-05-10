package web.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import modules.product.model.Product;
import modules.rabbitmq.producer.ProcessingProducer;
import modules.sse.service.SSEEventService;
import web.output.StatusResponse;

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

    private final Map<String, StatusResponse> statusCache =
        new ConcurrentHashMap<>();
    private final Map<String, Product> productCache = new ConcurrentHashMap<>();

    @POST
    public Response createProcessing(@Valid Product request) {
        String id = UUID.randomUUID().toString();

        Product product = new Product(id, request.product(), request.price());
        productCache.put(id, product);

        StatusResponse status = new StatusResponse(
            id,
            StatusResponse.Status.PENDING,
            "Processing created"
        );
        updateStatus(id, status);

        producer.sendMessage(id, product);

        return Response.accepted().entity(status).build();
    }

    @PUT
    @Path("/{id}/price")
    public Response updatePrice(
        @PathParam("id") String id,
        Map<String, Double> body
    ) {
        Product product = productCache.get(id);
        if (
            product == null || statusCache.get(id) == null
        ) return Response.status(Response.Status.NOT_FOUND).build();

        Double newPrice = body.get("price");
        if (newPrice == null || newPrice < 0) return Response.status(
            Response.Status.BAD_REQUEST
        ).build();

        Product updatedProduct = new Product(
            product.id(),
            product.product(),
            newPrice
        );

        productCache.put(id, updatedProduct);

        StatusResponse status = new StatusResponse(
            id,
            StatusResponse.Status.COMPLETED,
            String.format("Price updated to %.2f", newPrice)
        );
        updateStatus(id, status);
        sseService.sendEvent(id, status);

        return Response.ok(status).build();
    }

    @GET
    @Path("/{id}/status")
    public Response getStatus(@PathParam("id") String id) {
        StatusResponse status = statusCache.get(id);

        if (status == null) return Response.status(
            Response.Status.NOT_FOUND
        ).build();

        return Response.ok(status).build();
    }

    @GET
    @Path("/{id}/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void streamStatus(
        @PathParam("id") String id,
        @Context SseEventSink sink
    ) {
        sseService.addClient(id, sink);

        StatusResponse status = statusCache.get(id);
        if (status != null) sseService.sendEvent(id, status);

        sink.send(
            sse
                .newEventBuilder()
                .name("connected")
                .data("Connected to stream for: " + id)
                .build()
        );
    }

    public void updateStatus(String id, StatusResponse status) {
        statusCache.put(id, status);
    }
}
