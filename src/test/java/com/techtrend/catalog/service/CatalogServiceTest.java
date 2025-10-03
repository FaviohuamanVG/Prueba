package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Pruebas unitarias para CatalogService
 * 
 * Verifica la lógica de negocio del catálogo usando datos mock.
 * Cubre todos los escenarios críticos de negocio para TechTrend.
 * 
 * @author TechTrend Development Team
 */
@DisplayName("🛍️ Catalog Service - Lógica de Negocio")
class CatalogServiceTest {

    private CatalogService catalogService;

    @BeforeEach
    @DisplayName("🔧 Configuración inicial del servicio")
    void setUp() {
        catalogService = new CatalogServiceImpl();
        System.out.println("✅ CatalogService inicializado con 15 productos mock");
    }

    @Test
    @DisplayName("📦 Debe retornar todos los productos disponibles (13 de 15 productos)")
    void shouldReturnAllAvailableProducts() {
        System.out.println("🔍 Probando listado de productos disponibles...");
        
        // When
        Flux<Product> products = catalogService.getAllProducts();

        // Then - Verificamos que todos los productos retornados estén disponibles
        StepVerifier.create(products)
                .expectNextCount(13) // 13 productos disponibles (15 total - 2 agotados)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Se retornaron 13 productos disponibles");
    }

    @Test
    @DisplayName("🔍 Debe retornar producto existente por ID (Laptop Ryzen 7)")
    void shouldReturnExistingProductById() {
        System.out.println("🔍 Probando búsqueda de producto por ID: 1");
        
        // When
        Mono<Product> product = catalogService.getProductById("1");

        // Then
        StepVerifier.create(product)
                .expectNextMatches(p -> {
                    boolean isValid = "1".equals(p.getId()) && "Laptop Ryzen 7".equals(p.getName());
                    if (isValid) {
                        System.out.println("✅ Producto encontrado: " + p.getName() + " - $" + p.getPrice());
                    }
                    return isValid;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("❌ Debe retornar vacío para producto no existente (ID: 999)")
    void shouldReturnEmptyForNonExistentProduct() {
        System.out.println("🔍 Probando búsqueda de producto inexistente ID: 999");
        
        // When
        Mono<Product> product = catalogService.getProductById("999");

        // Then
        StepVerifier.create(product)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Producto inexistente retorna vacío");
    }

    @Test
    @DisplayName("✅ Stock suficiente debe retornar true (10 de 50 disponibles)")
    void shouldReturnTrueWhenStockIsSufficient() {
        System.out.println("📊 Probando verificación de stock suficiente: 10 unidades de 50 disponibles");
        
        // Given: Laptop Ryzen 7 tiene 50 unidades disponibles
        // When
        Mono<Boolean> result = catalogService.checkStock("1", 10);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Stock suficiente confirmado");
    }

    @Test
    @DisplayName("❌ Stock insuficiente debe retornar false (60 de 50 disponibles)")
    void shouldReturnFalseWhenStockIsInsufficient() {
        System.out.println("📊 Probando verificación de stock insuficiente: 60 unidades de 50 disponibles");
        
        // Given: Laptop Ryzen 7 tiene 50 unidades disponibles
        // When
        Mono<Boolean> result = catalogService.checkStock("1", 60);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Stock insuficiente detectado correctamente");
    }

    @Test
    @DisplayName("🚫 Cantidad negativa debe lanzar excepción IllegalArgumentException")
    void shouldThrowExceptionForNegativeQuantity() {
        System.out.println("⚠️ Probando validación de cantidad negativa: -1");
        
        // When
        Mono<Boolean> result = catalogService.checkStock("1", -1);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
                
        System.out.println("✅ Test exitoso: Excepción lanzada para cantidad negativa");
    }

    @Test
    @DisplayName("🔍 Producto no existente debe retornar false para verificación de stock")
    void shouldReturnFalseForNonExistentProductStock() {
        System.out.println("📊 Probando verificación de stock para producto inexistente ID: 999");
        
        // When
        Mono<Boolean> result = catalogService.checkStock("999", 1);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Stock de producto inexistente retorna false");
    }

    @Test
    @DisplayName("📋 Debe retornar detalles correctos del producto (ID, nombre, precio)")
    void shouldReturnCorrectProductDetails() {
        System.out.println("📋 Probando obtención de detalles del producto ID: 1");
        
        // When
        Mono<Product> product = catalogService.getProductDetails("1");

        // Then
        StepVerifier.create(product)
                .expectNextMatches(p -> {
                    boolean isValid = "1".equals(p.getId()) && 
                                    "Laptop Ryzen 7".equals(p.getName()) &&
                                    new BigDecimal("9999.99").equals(p.getPrice());
                    if (isValid) {
                        System.out.println("✅ Detalles correctos: " + p.getName() + " - $" + p.getPrice() + " (" + p.getQuantity() + " unidades)");
                    }
                    return isValid;
                })
                .verifyComplete();
    }
}