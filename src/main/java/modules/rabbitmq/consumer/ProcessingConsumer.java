package modules.rabbitmq.consumer;

import config.mapper.JsonMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.CompletionStage;
import modules.product.model.Product;
import modules.product.strategy.processor.ProductProcessor;
import modules.sse.service.SSEEventService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import web.controller.ProcessingController;
import web.output.StatusResponse;

@ApplicationScoped
public class ProcessingConsumer {

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
        Product product = Product.fromJson(json, message.getPayload());
        return processAndNotify(product);
    }

    private CompletionStage<Void> processAndNotify(Product data) {
        sendStatus(
            data.id(),
            StatusResponse.Status.PROCESSING,
            "Processing started"
        );

        return productProcessor
            .process(data.id(), data)
            .thenRun(() ->
                sendStatus(
                    data.id(),
                    StatusResponse.Status.COMPLETED,
                    "Processing completed"
                )
            )
            .exceptionally(e -> {
                sendStatus(
                    data.id(),
                    StatusResponse.Status.FAILED,
                    e.getMessage()
                );
                throw new RuntimeException(e);
            });
    }

    private void sendStatus(
        String id,
        StatusResponse.Status status,
        String msg
    ) {
        StatusResponse response = new StatusResponse(id, status, msg);
        controller.updateStatus(id, response);
        sseEventService.sendEvent(id, response);
    }
}
