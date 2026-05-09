package adapter.product.strategy;

import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adapter.product.interfaces.ProductProcessorStrategy;
import adapter.product.model.Product;


@ApplicationScoped
@Named("default")
public class DefaultProcessor implements ProductProcessorStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultProcessor.class);
    
    @Override
    public void process(Product product) {
        LOG.debug("Performing default processing for product: {}", product.id());
        sleep(1000);
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}