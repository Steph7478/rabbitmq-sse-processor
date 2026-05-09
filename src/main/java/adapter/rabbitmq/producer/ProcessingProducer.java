package adapter.rabbitmq.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import adapter.product.model.Product;
import shared.mapper.JsonMapper;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ProcessingProducer {

    @Inject
    @Channel("producer")
    Emitter<String> emitter;

    @Inject
    JsonMapper json;

    public void sendMessage(String id, Product request) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", id);
        message.put("product", Map.of(
            "id", request.id(),
            "product", request.product(),
            "price", request.price()
        ));
        message.put("price", request.price());

        emitter.send(json.toJson(message))
                .toCompletableFuture()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Failed to send to RabbitMQ", throwable);
                });
    }
}