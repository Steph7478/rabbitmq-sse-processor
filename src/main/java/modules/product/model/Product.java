package modules.product.model;

import config.mapper.JsonMapper;
import java.util.Map;

public record Product(String id, String product, Double price) {
    public static Product fromJson(JsonMapper json, String payload) {
        Map<String, Object> map = json.fromJsonToMap(payload);
        return new Product(
            (String) map.get("id"),
            (String) map.get("product"),
            ((Number) map.get("price")).doubleValue()
        );
    }
}
