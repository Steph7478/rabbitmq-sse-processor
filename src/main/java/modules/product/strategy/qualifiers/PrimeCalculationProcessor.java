package modules.product.strategy.qualifiers;

import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modules.product.model.Product;
import modules.product.strategy.interfaces.ProductProcessorStrategy;

import java.util.stream.IntStream;

@ApplicationScoped
@Named("prime-calculation")
public class PrimeCalculationProcessor implements ProductProcessorStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(PrimeCalculationProcessor.class);
    
    @Override
    public void process(Product product) {
        LOG.debug("Performing prime calculation for product: {}", product.id());
        
        long result = IntStream.range(0, 1_000_000)
                .mapToLong(i -> (long) i * i)
                .sum();
        
        LOG.trace("Calculation result for product {}: {}", product.id(), result);
    }
}