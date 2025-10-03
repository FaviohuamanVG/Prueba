package com.techtrend.catalog.controller;

import com.techtrend.catalog.model.Product;
import com.techtrend.catalog.service.CatalogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 🌐 Pruebas unitarias para CatalogController
 * 
 * Verifica los endpoints REST usando mocks del servicio.
 * Prueba la capa de presentación y validaciones HTTP.
 * 
 * @author TechTrend Development Team
 */
@WebFluxTest(CatalogController.class)
@DisplayName("🌐 Catalog Controller - Endpoints REST")
class CatalogControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CatalogService catalogService;

    @Test
    @DisplayName("📦 GET /api/catalog/products debe retornar lista de productos (200 OK)")
    void shouldReturnProductsList() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products");
        
        // Given
        Product product1 = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        Product product2 = new Product("2", "Mouse Gaming", new BigDecimal("299.99"), 100);
        
        when(catalogService.getAllProducts()).thenReturn(Flux.just(product1, product2));

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(2);
                
        System.out.println("✅ Test exitoso: Endpoint retorna 2 productos con status 200 OK");
    }

    @Test
    @DisplayName("🔍 GET /api/catalog/products/{id} debe retornar producto existente (200 OK)")
    void shouldReturnExistingProduct() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products/1");
        
        // Given
        Product product = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        when(catalogService.getProductById("1")).thenReturn(Mono.just(product));

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .isEqualTo(product);
                
        System.out.println("✅ Test exitoso: Producto encontrado - Laptop Ryzen 7 ($9999.99)");
    }

    @Test
    @DisplayName("❌ GET /api/catalog/products/{id} debe retornar 404 para producto no existente")
    void shouldReturn404ForNonExistentProduct() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products/999 (producto inexistente)");
        
        // Given
        when(catalogService.getProductById("999")).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products/999")
                .exchange()
                .expectStatus().isNotFound();
                
        System.out.println("✅ Test exitoso: Status 404 Not Found para producto inexistente");
    }

    @Test
    @DisplayName("✅ GET /api/catalog/products/{id}/stock debe retornar true para stock suficiente")
    void shouldReturnTrueForSufficientStock() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products/1/stock?quantity=10");
        
        // Given
        when(catalogService.checkStock("1", 10)).thenReturn(Mono.just(true));

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products/1/stock?quantity=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
                
        System.out.println("✅ Test exitoso: Stock suficiente confirmado (true)");
    }

    @Test
    @DisplayName("❌ GET /api/catalog/products/{id}/stock debe retornar false para stock insuficiente")
    void shouldReturnFalseForInsufficientStock() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products/1/stock?quantity=60");
        
        // Given
        when(catalogService.checkStock("1", 60)).thenReturn(Mono.just(false));

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products/1/stock?quantity=60")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(false);
                
        System.out.println("✅ Test exitoso: Stock insuficiente detectado (false)");
    }

    @Test
    @DisplayName("🚫 GET /api/catalog/products/{id}/stock debe retornar 400 para cantidad inválida")
    void shouldReturn400ForInvalidQuantity() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products/1/stock?quantity=-1 (cantidad inválida)");
        
        // Given
        when(catalogService.checkStock(anyString(), any()))
                .thenReturn(Mono.error(new IllegalArgumentException("La cantidad debe ser mayor a 0")));

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products/1/stock?quantity=-1")
                .exchange()
                .expectStatus().isBadRequest();
                
        System.out.println("✅ Test exitoso: Status 400 Bad Request para cantidad negativa");
    }

    @Test
    @DisplayName("📋 GET /api/catalog/products/{id}/details debe retornar detalles del producto")
    void shouldReturnProductDetails() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products/1/details");
        
        // Given
        Product product = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        when(catalogService.getProductDetails("1")).thenReturn(Mono.just(product));

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products/1/details")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .isEqualTo(product);
                
        System.out.println("✅ Test exitoso: Detalles del producto retornados correctamente");
    }

    @Test
    @DisplayName("❌ GET /api/catalog/products/{id}/details debe retornar 404 para producto no existente")
    void shouldReturn404ForNonExistentProductDetails() {
        System.out.println("🌐 Probando endpoint: GET /api/catalog/products/999/details (producto inexistente)");
        
        // Given
        when(catalogService.getProductDetails("999")).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/catalog/products/999/details")
                .exchange()
                .expectStatus().isNotFound();
                
        System.out.println("✅ Test exitoso: Status 404 Not Found para detalles de producto inexistente");
    }
}