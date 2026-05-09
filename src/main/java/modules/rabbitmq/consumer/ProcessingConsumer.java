package modules.rabbitmq.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.mapper.JsonMapper;
import modules.product.model.Product;
import modules.product.strategy.processor.ProductProcessor;
import modules.sse.service.SSEEventService;
import web.controller.ProcessingController;
import web.output.StatusResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProcessingConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingConsumer.class);

    @Inject
    JsonMapper json;
    @Inject
    ProductProcessor productProcessor;
    @Inject
    SSEEventService sseEventService;
    @Inject
    ProcessingController controller;

    @Incoming("consumer")
    public CompletionStage<Void> consume(Message<String> message) {
        return CompletableFuture.supplyAsync(() -> parseMessage(message.getPayload()))
                .thenCompose(this::processAndNotify)
                .exceptionally(e -> {
                    LOG.error("Failed to process message", e);
                    return null;
                });
    }

    private MessageData parseMessage(String payload) {
        Map<String, Object> map = json.fromJsonToMap(payload);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> productMap = (Map<String, Object>) map.get("product");
        
        return new MessageData(
            (String) map.get("id"),
            new Product(
                (String) productMap.get("id"),
                (String) productMap.get("product"),
                ((Number) productMap.get("price")).doubleValue()
            ),
            map.get("price")
        );
    }

    private CompletionStage<Void> processAndNotify(MessageData data) {
        sendStatus(data.id, StatusResponse.Status.PROCESSING, "Processing started");
        
        return productProcessor.process(data.id, data.product, data.price)
                .thenRun(() -> sendStatus(data.id, StatusResponse.Status.COMPLETED, "Processing completed"))
                .exceptionally(e -> {
                    sendStatus(data.id, StatusResponse.Status.FAILED, e.getMessage());
                    throw new RuntimeException(e);
                });
    }

    private void sendStatus(String id, StatusResponse.Status status, String msg) {
        StatusResponse response = new StatusResponse(id, status, msg);
        controller.updateStatus(id, response);
        sseEventService.sendEvent(id, response);
    }

    private record MessageData(String id, Product product, Object price) {}
}