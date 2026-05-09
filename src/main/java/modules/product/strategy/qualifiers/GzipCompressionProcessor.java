package modules.product.strategy.qualifiers;

import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modules.product.model.Product;
import modules.product.strategy.interfaces.ProductProcessorStrategy;


@ApplicationScoped
@Named("gzip-compression")
public class GzipCompressionProcessor implements ProductProcessorStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(GzipCompressionProcessor.class);
    
    @Override
    public void process(Product product) {
        LOG.debug("Performing GZIP compression for product: {}", product.id());
        sleep(2000);
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}