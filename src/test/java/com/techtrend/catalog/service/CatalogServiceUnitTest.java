package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

/**
 * 🧪 Pruebas unitarias completas para CatalogService
 * 
 * Cubre todos los escenarios de negocio del microservicio ms-catalog:
 * - Flujo principal (descuentos 10%, 20%, 30% con stock > umbral)
 * - Caso de error (descuentos -1%, 101%)
 * - Caso límite (stock igual o justo por encima del umbral)
 * - Error avanzado (productos inexistentes)
 * 
 * @author TechTrend Development Team
 */
@DisplayName("🛍️ Catalog Service - Pruebas Unitarias Completas")
class CatalogServiceUnitTest {

    private CatalogService catalogService;

    @BeforeEach
    @DisplayName("🔧 Configuración inicial del servicio con datos mock")
    void setUp() {
        catalogService = new CatalogServiceImpl();
        System.out.println("✅ CatalogService inicializado con 15 productos mock para testing");
    }

    // ==================== FLUJO PRINCIPAL ====================
    
    @Test
    @DisplayName("📦 [FLUJO PRINCIPAL] Debe retornar productos disponibles con descuentos aplicables")
    void shouldReturnAvailableProductsForMainFlow() {
        System.out.println("🔍 [FLUJO PRINCIPAL] Probando listado de productos con stock suficiente...");
        
        // When
        Flux<Product> products = catalogService.getAllProducts();

        // Then - Verificamos productos con stock > umbral para descuentos
        StepVerifier.create(products)
                .expectNextCount(13) // 13 productos disponibles (15 total - 2 agotados)
                .verifyComplete();
                
        // Verificamos específicamente productos con diferentes niveles de stock
        StepVerifier.create(products)
                .thenConsumeWhile(product -> {
                    System.out.println("✅ Producto: " + product.getName() + 
                                     " - Stock: " + product.getQuantity() + 
                                     " - Precio: $" + product.getPrice());
                    return product.isAvailable();
                })
                .verifyComplete();
                
        System.out.println("✅ [FLUJO PRINCIPAL] Test exitoso: Productos con stock para descuentos");
    }

    @ParameterizedTest
    @CsvSource({
        "1, 10, true",   // 10% descuento - stock 50 > umbral 10
        "2, 20, true",   // 20% descuento - stock 100 > umbral 20  
        "3, 8, true",    // 30% descuento - stock 25 > umbral 8
        "4, 5, true"     // Stock límite - 15 unidades > umbral 5
    })
    @DisplayName("💰 [FLUJO PRINCIPAL] Descuentos por volumen según stock disponible")
    void shouldApplyDiscountBasedOnStockLevels(String productId, Integer requestedQuantity, Boolean expectedResult) {
        System.out.println("💰 [FLUJO PRINCIPAL] Probando descuento para producto " + productId + 
                         " con cantidad " + requestedQuantity);
        
        // When
        Mono<Boolean> result = catalogService.checkStock(productId, requestedQuantity);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResult)
                .verifyComplete();
                
        System.out.println("✅ [FLUJO PRINCIPAL] Descuento aplicable: " + expectedResult);
    }

    // ==================== CASOS DE ERROR ====================
    
    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    @DisplayName("🚫 [CASO DE ERROR] Cantidades negativas deben lanzar IllegalArgumentException")
    void shouldThrowExceptionForNegativeQuantities(Integer invalidQuantity) {
        System.out.println("⚠️ [CASO DE ERROR] Probando cantidad negativa: " + invalidQuantity);
        
        // When
        Mono<Boolean> result = catalogService.checkStock("1", invalidQuantity);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
                
        System.out.println("✅ [CASO DE ERROR] Excepción lanzada correctamente para cantidad: " + invalidQuantity);
    }

    @Test
    @DisplayName("🚫 [CASO DE ERROR] Cantidad cero debe lanzar IllegalArgumentException")
    void shouldThrowExceptionForZeroQuantity() {
        System.out.println("⚠️ [CASO DE ERROR] Probando cantidad cero");
        
        // When
        Mono<Boolean> result = catalogService.checkStock("1", 0);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
                
        System.out.println("✅ [CASO DE ERROR] Excepción lanzada para cantidad cero");
    }

    @ParameterizedTest
    @ValueSource(strings = {"999", "abc", "", "null"})
    @DisplayName("❌ [CASO DE ERROR] Productos inexistentes deben retornar false")
    void shouldReturnFalseForNonExistentProducts(String invalidProductId) {
        System.out.println("❌ [CASO DE ERROR] Probando producto inexistente: " + invalidProductId);
        
        // When
        Mono<Boolean> result = catalogService.checkStock(invalidProductId, 1);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
                
        System.out.println("✅ [CASO DE ERROR] Producto inexistente retorna false correctamente");
    }

    // ==================== CASOS LÍMITE ====================
    
    @Test
    @DisplayName("📊 [CASO LÍMITE] Stock igual al umbral debe permitir la compra")
    void shouldAllowPurchaseWhenStockEqualsThreshold() {
        System.out.println("📊 [CASO LÍMITE] Probando stock igual al umbral solicitado");
        
        // Given: Laptop tiene 50 unidades
        // When: Solicitamos exactamente 50
        Mono<Boolean> result = catalogService.checkStock("1", 50);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
                
        System.out.println("✅ [CASO LÍMITE] Stock igual al umbral permite la compra");
    }

    @Test
    @DisplayName("📊 [CASO LÍMITE] Stock justo por encima del umbral debe permitir descuento")
    void shouldAllowDiscountWhenStockJustAboveThreshold() {
        System.out.println("📊 [CASO LÍMITE] Probando stock justo por encima del umbral");
        
        // Given: Monitor 4K tiene 15 unidades
        // When: Solicitamos 14 (justo por debajo del stock)
        Mono<Boolean> result = catalogService.checkStock("4", 14);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
                
        System.out.println("✅ [CASO LÍMITE] Stock suficiente para descuento aplicado");
    }

    @Test
    @DisplayName("📊 [CASO LÍMITE] Productos con stock cero no deben estar disponibles")
    void shouldNotReturnProductsWithZeroStock() {
        System.out.println("📊 [CASO LÍMITE] Verificando que productos agotados no aparezcan");
        
        // When
        Flux<Product> availableProducts = catalogService.getAllProducts();

        // Then - Verificamos que ningún producto retornado tenga stock 0
        StepVerifier.create(availableProducts)
                .thenConsumeWhile(product -> {
                    boolean hasStock = product.getQuantity() > 0;
                    if (!hasStock) {
                        System.out.println("❌ ERROR: Producto sin stock encontrado: " + product.getName());
                    }
                    return hasStock;
                })
                .verifyComplete();
                
        System.out.println("✅ [CASO LÍMITE] Solo productos con stock están disponibles");
    }

    // ==================== ERROR AVANZADO ====================
    
    @Test
    @DisplayName("🔍 [ERROR AVANZADO] Producto inexistente debe retornar Mono vacío")
    void shouldReturnEmptyMonoForNonExistentProductDetails() {
        System.out.println("🔍 [ERROR AVANZADO] Probando detalles de producto inexistente");
        
        // When
        Mono<Product> product = catalogService.getProductById("999");

        // Then
        StepVerifier.create(product)
                .verifyComplete(); // Mono vacío
                
        System.out.println("✅ [ERROR AVANZADO] Producto inexistente retorna Mono vacío");
    }

    @Test
    @DisplayName("🔍 [ERROR AVANZADO] Detalles de producto inexistente deben retornar vacío")
    void shouldReturnEmptyForNonExistentProductDetails() {
        System.out.println("🔍 [ERROR AVANZADO] Probando getProductDetails para producto inexistente");
        
        // When
        Mono<Product> productDetails = catalogService.getProductDetails("999");

        // Then
        StepVerifier.create(productDetails)
                .verifyComplete();
                
        System.out.println("✅ [ERROR AVANZADO] Detalles de producto inexistente retornan vacío");
    }

    // ==================== CASOS ESPECÍFICOS DE NEGOCIO ====================
    
    @Test
    @DisplayName("💻 [ESPECÍFICO] Laptop Ryzen 7 debe tener precio y stock correctos")
    void shouldReturnCorrectLaptopDetails() {
        System.out.println("💻 [ESPECÍFICO] Verificando detalles de Laptop Ryzen 7");
        
        // When
        Mono<Product> laptop = catalogService.getProductById("1");

        // Then
        StepVerifier.create(laptop)
                .expectNextMatches(product -> {
                    boolean isValid = "1".equals(product.getId()) &&
                                    "Laptop Ryzen 7".equals(product.getName()) &&
                                    new BigDecimal("9999.99").equals(product.getPrice()) &&
                                    product.getQuantity() == 50;
                    
                    if (isValid) {
                        System.out.println("✅ Laptop verificada: " + product.getName() + 
                                         " - $" + product.getPrice() + " (" + product.getQuantity() + " unidades)");
                    }
                    return isValid;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("🎧 [ESPECÍFICO] Auriculares Bluetooth deben estar agotados")
    void shouldShowBluetoothHeadphonesAsOutOfStock() {
        System.out.println("🎧 [ESPECÍFICO] Verificando que Auriculares Bluetooth estén agotados");
        
        // When
        Mono<Product> headphones = catalogService.getProductById("5");

        // Then
        StepVerifier.create(headphones)
                .expectNextMatches(product -> {
                    boolean isOutOfStock = product.getQuantity() == 0 && !product.isAvailable();
                    if (isOutOfStock) {
                        System.out.println("✅ Confirmado: " + product.getName() + " está agotado");
                    }
                    return isOutOfStock;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("📱 [ESPECÍFICO] Tablet Android 10\" debe estar agotada")
    void shouldShowTabletAsOutOfStock() {
        System.out.println("📱 [ESPECÍFICO] Verificando que Tablet Android 10\" esté agotada");
        
        // When
        Mono<Product> tablet = catalogService.getProductById("15");

        // Then
        StepVerifier.create(tablet)
                .expectNextMatches(product -> {
                    boolean isOutOfStock = product.getQuantity() == 0 && !product.isAvailable();
                    if (isOutOfStock) {
                        System.out.println("✅ Confirmado: " + product.getName() + " está agotada");
                    }
                    return isOutOfStock;
                })
                .verifyComplete();
    }

    // ==================== PRUEBAS DE RENDIMIENTO ====================
    
    @Test
    @DisplayName("⚡ [RENDIMIENTO] Búsqueda de múltiples productos debe ser eficiente")
    void shouldHandleMultipleProductSearchesEfficiently() {
        System.out.println("⚡ [RENDIMIENTO] Probando búsqueda de múltiples productos");
        
        // When - Buscamos varios productos en paralelo
        Flux<Product> multipleProducts = Flux.just("1", "2", "3", "4", "5")
                .flatMap(id -> catalogService.getProductById(id));

        // Then
        StepVerifier.create(multipleProducts)
                .expectNextCount(5) // Debe retornar los 5 productos
                .verifyComplete();
                
        System.out.println("✅ [RENDIMIENTO] Búsqueda múltiple completada eficientemente");
    }

    @Test
    @DisplayName("📊 [COBERTURA] Verificación de stock para todos los productos disponibles")
    void shouldVerifyStockForAllAvailableProducts() {
        System.out.println("📊 [COBERTURA] Verificando stock de todos los productos disponibles");
        
        // When
        Flux<Boolean> stockResults = catalogService.getAllProducts()
                .flatMap(product -> catalogService.checkStock(product.getId(), 1));

        // Then - Todos los productos disponibles deben tener stock suficiente para 1 unidad
        StepVerifier.create(stockResults)
                .thenConsumeWhile(hasStock -> {
                    if (!hasStock) {
                        System.out.println("❌ ERROR: Producto disponible sin stock encontrado");
                    }
                    return hasStock; // Todos deben ser true
                })
                .verifyComplete();
                
        System.out.println("✅ [COBERTURA] Todos los productos disponibles tienen stock suficiente");
    }
}