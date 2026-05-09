package adapter.product.strategy.processor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adapter.product.interfaces.ProductProcessorStrategy;
import adapter.product.model.Product;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ProductProcessor.class);

    private final Map<String, ProductProcessorStrategy> strategyMap;

    @Inject
    public ProductProcessor(
            @Named("prime-calculation") Instance<ProductProcessorStrategy> primeCalculation,
            @Named("aes-encryption") Instance<ProductProcessorStrategy> aesEncryption,
            @Named("gzip-compression") Instance<ProductProcessorStrategy> gzipCompression,
            @Named("default") Instance<ProductProcessorStrategy> defaultStrategy) {
        
        this.strategyMap = Map.of(
            "prime-calculation", primeCalculation.get(),
            "aes-encryption", aesEncryption.get(),
            "gzip-compression", gzipCompression.get(),
            "default", defaultStrategy.get()
        );
        
        LOG.info("Loaded strategies: {}", strategyMap.keySet());
    }

    public CompletionStage<Void> process(String id, Product product, Object price) {
        return CompletableFuture.runAsync(() -> {
            LOG.info("Processing product '{}' (ID: {}) for process ID: {}", product.product(), product.id(), id);
            
            ProductProcessorStrategy processor = strategyMap.getOrDefault(
                product.product(), 
                strategyMap.get("default")
            );
            processor.process(product);
            
            LOG.info("Completed processing for process ID: {}", id);
        });
    }
}