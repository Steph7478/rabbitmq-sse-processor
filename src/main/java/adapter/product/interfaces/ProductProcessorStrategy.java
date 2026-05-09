package adapter.product.interfaces;

import adapter.product.model.Product;

public interface ProductProcessorStrategy {
    void process(Product product);
}