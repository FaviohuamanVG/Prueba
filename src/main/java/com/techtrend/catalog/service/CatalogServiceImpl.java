package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de catálogo usando datos mock
 */
@Service
public class CatalogServiceImpl implements CatalogService {
    
    private final Map<String, Product> products;
    
    public CatalogServiceImpl() {
        this.products = new HashMap<>();
        initializeMockData();
    }
    
    private void initializeMockData() {
        products.put("1", new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50));
        products.put("2", new Product("2", "Mouse Gaming", new BigDecimal("299.99"), 100));
        products.put("3", new Product("3", "Teclado Mecánico", new BigDecimal("599.99"), 25));
        products.put("4", new Product("4", "Monitor 4K", new BigDecimal("1299.99"), 15));
        products.put("5", new Product("5", "Auriculares Bluetooth", new BigDecimal("199.99"), 0));
        products.put("6", new Product("6", "Webcam HD", new BigDecimal("149.99"), 75));
        products.put("7", new Product("7", "SSD 1TB", new BigDecimal("899.99"), 30));
        products.put("8", new Product("8", "RAM 16GB DDR4", new BigDecimal("449.99"), 60));
        products.put("9", new Product("9", "Tarjeta Gráfica RTX 4060", new BigDecimal("3499.99"), 8));
        products.put("10", new Product("10", "Procesador Intel i7", new BigDecimal("2199.99"), 20));
        products.put("11", new Product("11", "Motherboard Gaming", new BigDecimal("1599.99"), 12));
        products.put("12", new Product("12", "Fuente de Poder 750W", new BigDecimal("799.99"), 35));
        products.put("13", new Product("13", "Case Gaming RGB", new BigDecimal("699.99"), 18));
        products.put("14", new Product("14", "Cooler CPU Líquido", new BigDecimal("999.99"), 22));
        products.put("15", new Product("15", "Tablet Android 10\"", new BigDecimal("1899.99"), 0));
    }
    
    @Override
    public Flux<Product> getAllProducts() {
        return Flux.fromIterable(products.values())
                .filter(Product::isAvailable);
    }
    
    @Override
    public Mono<Product> getProductById(String id) {
        Product product = products.get(id);
        return product != null ? Mono.just(product) : Mono.empty();
    }
    
    @Override
    public Mono<Boolean> checkStock(String productId, Integer requestedQuantity) {
        if (requestedQuantity <= 0) {
            return Mono.error(new IllegalArgumentException("La cantidad debe ser mayor a 0"));
        }
        
        return getProductById(productId)
                .map(product -> product.hasStock(requestedQuantity))
                .defaultIfEmpty(false);
    }
    
    @Override
    public Mono<Product> getProductDetails(String productId) {
        return getProductById(productId);
    }
}