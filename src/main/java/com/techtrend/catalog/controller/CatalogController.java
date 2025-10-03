package com.techtrend.catalog.controller;

import com.techtrend.catalog.model.Product;
import com.techtrend.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para el catálogo de productos
 */
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    
    private final CatalogService catalogService;
    
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
    /**
     * Lista todos los productos disponibles
     */
    @GetMapping("/products")
    public Flux<Product> getAllProducts() {
        return catalogService.getAllProducts();
    }
    
    /**
     * Obtiene un producto por su ID
     */
    @GetMapping("/products/{id}")
    public Mono<ResponseEntity<Product>> getProductById(@PathVariable String id) {
        return catalogService.getProductById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Verifica stock para un producto específico
     */
    @GetMapping("/products/{id}/stock")
    public Mono<ResponseEntity<Boolean>> checkStock(
            @PathVariable String id,
            @RequestParam Integer quantity) {
        try {
            Mono<Boolean> stockCheck = catalogService.checkStock(id, quantity);
            if (stockCheck == null) {
                return Mono.just(ResponseEntity.badRequest().build());
            }
            return stockCheck
                    .map(ResponseEntity::ok)
                    .onErrorReturn(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }
    
    /**
     * Obtiene detalles de un producto (nombre y precio)
     */
    @GetMapping("/products/{id}/details")
    public Mono<ResponseEntity<Product>> getProductDetails(@PathVariable String id) {
        return catalogService.getProductDetails(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}