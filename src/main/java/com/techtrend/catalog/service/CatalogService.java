package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio de lógica de negocio para el catálogo
 */
public interface CatalogService {
    
    /**
     * Lista todos los productos disponibles
     */
    Flux<Product> getAllProducts();
    
    /**
     * Obtiene un producto por su ID
     */
    Mono<Product> getProductById(String id);
    
    /**
     * Verifica si hay stock suficiente para un producto
     */
    Mono<Boolean> checkStock(String productId, Integer requestedQuantity);
    
    /**
     * Obtiene los detalles de un producto (nombre y precio)
     */
    Mono<Product> getProductDetails(String productId);
}