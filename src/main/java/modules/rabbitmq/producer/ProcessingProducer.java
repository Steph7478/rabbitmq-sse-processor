package modules.rabbitmq.producer;

import config.mapper.JsonMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import modules.product.model.Product;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

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
        message.put("product", request);

        emitter
            .send(json.toJson(message))
            .toCompletableFuture()
            .exceptionally(throwable -> {
                throw new RuntimeException(
                    "Failed to send to RabbitMQ",
                    throwable
                );
            });
    }
}
