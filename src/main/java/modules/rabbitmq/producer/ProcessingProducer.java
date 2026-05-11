package modules.rabbitmq.producer;

import config.mapper.JsonMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
        emitter
            .send(json.toJson(request))
            .toCompletableFuture()
            .exceptionally(throwable -> {
                throw new RuntimeException(
                    "Failed to send to RabbitMQ",
                    throwable
                );
            });
    }
}
