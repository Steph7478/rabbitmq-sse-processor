package adapter.rabbitmq.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adapter.product.model.Product;
import adapter.product.strategy.processor.ProductProcessor;
import adapter.sse.service.SSEEventService;
import shared.mapper.JsonMapper;
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
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> consume(org.eclipse.microprofile.reactive.messaging.Message<String> message) {
        return CompletableFuture.supplyAsync(() -> parseMessage(message.getPayload()))
            .thenCompose(data -> processAndNotify(data))
            .thenRun(message::ack)
            .exceptionally(throwable -> {
                LOG.error("Failed to process message", throwable);
                message.nack(throwable);
                return null;
            });
    }

    private MessageData parseMessage(String payload) {
        Map<String, Object> map = json.fromJsonToMap(payload);
        
        String id = (String) map.get("id");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> productMap = (Map<String, Object>) map.get("product");
        
        String productId = (String) productMap.get("id");
        String productName = (String) productMap.get("product");
        Double productPrice = ((Number) productMap.get("price")).doubleValue();
        
        Product product = new Product(productId, productName, productPrice);
        Object price = map.get("price");
        
        return new MessageData(id, product, price);
    }

    private CompletionStage<Void> processAndNotify(MessageData data) {
        sendStatusUpdate(data.id, StatusResponse.Status.PROCESSING, "Processing started");
        
        return productProcessor.process(data.id, data.product, data.price)
            .thenRun(() -> sendStatusUpdate(data.id, StatusResponse.Status.COMPLETED, "Processing completed"))
            .exceptionally(throwable -> {
                sendStatusUpdate(data.id, StatusResponse.Status.FAILED, throwable.getMessage());
                throw new RuntimeException(throwable);
            });
    }

    private void sendStatusUpdate(String id, StatusResponse.Status status, String message) {
        StatusResponse response = new StatusResponse(id, status, message);
        controller.updateStatus(id, response);
        sseEventService.sendEvent(id, response);
    }

    private record MessageData(String id, Product product, Object price) {}
}