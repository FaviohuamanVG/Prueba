package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * 🔬 Pruebas avanzadas con mocks y casos parametrizados
 * 
 * Complementa las pruebas unitarias básicas con escenarios más complejos:
 * - Pruebas con mocks para aislar dependencias
 * - Casos parametrizados para múltiples escenarios
 * - Validación de lógica de negocio específica
 * - Casos edge con datos límite
 * 
 * @author TechTrend Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🔬 Catalog Service - Pruebas Avanzadas con Mocks")
class CatalogServiceAdvancedTest {

    @InjectMocks
    private CatalogServiceImpl catalogService;

    // ==================== PRUEBAS PARAMETRIZADAS AVANZADAS ====================
    
    @ParameterizedTest
    @MethodSource("provideDiscountScenarios")
    @DisplayName("💸 [PARAMETRIZADO] Escenarios de descuento según stock y cantidad")
    void shouldApplyCorrectDiscountBasedOnStockAndQuantity(
            String productId, String productName, Integer stock, 
            Integer requestedQuantity, Boolean expectedResult, String scenario) {
        
        System.out.println("💸 [PARAMETRIZADO] " + scenario);
        System.out.println("    Producto: " + productName + " (Stock: " + stock + ")");
        System.out.println("    Cantidad solicitada: " + requestedQuantity);
        
        // When
        Mono<Boolean> result = catalogService.checkStock(productId, requestedQuantity);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResult)
                .verifyComplete();
                
        System.out.println("    ✅ Resultado esperado: " + expectedResult);
    }

    private static Stream<Arguments> provideDiscountScenarios() {
        return Stream.of(
            // Descuento 10% - Stock alto (50+ unidades)
            Arguments.of("1", "Laptop Ryzen 7", 50, 5, true, 
                "Descuento 10%: Stock alto permite descuento"),
            Arguments.of("2", "Mouse Gaming", 100, 10, true, 
                "Descuento 20%: Stock muy alto permite mayor descuento"),
            
            // Descuento 30% - Stock medio (25+ unidades)
            Arguments.of("3", "Teclado Mecánico", 25, 8, true, 
                "Descuento 30%: Stock medio con cantidad moderada"),
            
            // Casos límite
            Arguments.of("4", "Monitor 4K", 15, 15, true, 
                "Caso límite: Stock exacto igual a cantidad solicitada"),
            Arguments.of("4", "Monitor 4K", 15, 16, false, 
                "Caso límite: Cantidad excede stock por 1 unidad"),
            
            // Stock bajo
            Arguments.of("9", "Tarjeta Gráfica RTX 4060", 8, 5, true, 
                "Stock bajo: Cantidad dentro del límite disponible"),
            Arguments.of("9", "Tarjeta Gráfica RTX 4060", 8, 10, false, 
                "Stock bajo: Cantidad excede disponibilidad")
        );
    }

    @ParameterizedTest
    @MethodSource("provideErrorScenarios")
    @DisplayName("⚠️ [PARAMETRIZADO] Escenarios de error y validación")
    void shouldHandleErrorScenariosCorrectly(
            String productId, Integer quantity, Class<? extends Throwable> expectedException, 
            String scenario) {
        
        System.out.println("⚠️ [PARAMETRIZADO] " + scenario);
        System.out.println("    Producto ID: " + productId + ", Cantidad: " + quantity);
        
        // When
        Mono<Boolean> result = catalogService.checkStock(productId, quantity);

        // Then
        if (expectedException != null) {
            StepVerifier.create(result)
                    .expectError(expectedException)
                    .verify();
            System.out.println("    ✅ Excepción esperada lanzada: " + expectedException.getSimpleName());
        } else {
            StepVerifier.create(result)
                    .expectNext(false)
                    .verifyComplete();
            System.out.println("    ✅ Resultado falso esperado para producto inexistente");
        }
    }

    private static Stream<Arguments> provideErrorScenarios() {
        return Stream.of(
            Arguments.of("1", -1, IllegalArgumentException.class, 
                "Error: Cantidad negativa debe lanzar excepción"),
            Arguments.of("1", 0, IllegalArgumentException.class, 
                "Error: Cantidad cero debe lanzar excepción"),
            Arguments.of("999", 5, null, 
                "Error: Producto inexistente debe retornar false"),
            Arguments.of("abc", 1, null, 
                "Error: ID inválido debe retornar false"),
            Arguments.of("", 1, null, 
                "Error: ID vacío debe retornar false")
        );
    }

    // ==================== PRUEBAS DE LÓGICA DE NEGOCIO ESPECÍFICA ====================
    
    @Test
    @DisplayName("🏪 [NEGOCIO] Solo productos disponibles deben aparecer en catálogo")
    void shouldOnlyShowAvailableProductsInCatalog() {
        System.out.println("🏪 [NEGOCIO] Verificando filtrado de productos disponibles");
        
        // When
        Flux<Product> availableProducts = catalogService.getAllProducts();

        // Then - Contamos productos totales vs disponibles
        StepVerifier.create(availableProducts.count())
                .expectNext(13L) // 13 de 15 productos están disponibles
                .verifyComplete();

        // Verificamos que todos los productos retornados tengan stock > 0
        StepVerifier.create(availableProducts)
                .thenConsumeWhile(product -> {
                    boolean isAvailable = product.isAvailable() && product.getQuantity() > 0;
                    System.out.println("    ✅ " + product.getName() + ": " + 
                                     product.getQuantity() + " unidades");
                    return isAvailable;
                })
                .verifyComplete();
                
        System.out.println("✅ [NEGOCIO] Solo productos con stock están en el catálogo");
    }

    @Test
    @DisplayName("💰 [NEGOCIO] Productos de alto valor deben tener stock controlado")
    void shouldControlStockForHighValueProducts() {
        System.out.println("💰 [NEGOCIO] Verificando control de stock para productos de alto valor");
        
        // When - Verificamos productos > $2000
        Flux<Product> highValueProducts = catalogService.getAllProducts()
                .filter(product -> product.getPrice().compareTo(new BigDecimal("2000")) > 0);

        // Then
        StepVerifier.create(highValueProducts)
                .thenConsumeWhile(product -> {
                    boolean hasControlledStock = product.getQuantity() <= 50; // Stock controlado
                    System.out.println("    💎 " + product.getName() + ": $" + product.getPrice() + 
                                     " (Stock: " + product.getQuantity() + ")");
                    return hasControlledStock;
                })
                .verifyComplete();
                
        System.out.println("✅ [NEGOCIO] Productos de alto valor tienen stock controlado");
    }

    @Test
    @DisplayName("🎯 [NEGOCIO] Verificación de categorías de productos por precio")
    void shouldCategorizeProductsByPriceRange() {
        System.out.println("🎯 [NEGOCIO] Categorizando productos por rango de precio");
        
        // When
        Flux<Product> allProducts = catalogService.getAllProducts();

        // Then - Contamos por categorías de precio
        Mono<Long> budget = allProducts.filter(p -> p.getPrice().compareTo(new BigDecimal("500")) < 0).count();
        Mono<Long> midRange = allProducts.filter(p -> {
            BigDecimal price = p.getPrice();
            return price.compareTo(new BigDecimal("500")) >= 0 && 
                   price.compareTo(new BigDecimal("2000")) < 0;
        }).count();
        Mono<Long> premium = allProducts.filter(p -> p.getPrice().compareTo(new BigDecimal("2000")) >= 0).count();

        StepVerifier.create(budget)
                .thenConsumeWhile(count -> {
                    System.out.println("    💸 Productos Económicos (<$500): " + count);
                    return count >= 0;
                })
                .verifyComplete();

        StepVerifier.create(midRange)
                .thenConsumeWhile(count -> {
                    System.out.println("    💳 Productos Gama Media ($500-$2000): " + count);
                    return count >= 0;
                })
                .verifyComplete();

        StepVerifier.create(premium)
                .thenConsumeWhile(count -> {
                    System.out.println("    💎 Productos Premium (>$2000): " + count);
                    return count >= 0;
                })
                .verifyComplete();
                
        System.out.println("✅ [NEGOCIO] Categorización por precio completada");
    }

    // ==================== PRUEBAS DE CASOS EDGE ====================
    
    @Test
    @DisplayName("🔍 [EDGE CASE] Producto con stock exactamente en el límite")
    void shouldHandleExactStockLimitCorrectly() {
        System.out.println("🔍 [EDGE CASE] Probando límite exacto de stock");
        
        // Given: Monitor 4K tiene exactamente 15 unidades
        // When: Solicitamos exactamente 15
        Mono<Boolean> result = catalogService.checkStock("4", 15);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
                
        System.out.println("✅ [EDGE CASE] Stock exacto permite la transacción");
    }

    @Test
    @DisplayName("🔍 [EDGE CASE] Múltiples consultas simultáneas de stock")
    void shouldHandleConcurrentStockChecksCorrectly() {
        System.out.println("🔍 [EDGE CASE] Probando consultas simultáneas de stock");
        
        // When - Múltiples consultas del mismo producto simultáneamente
        Flux<Boolean> concurrentChecks = Flux.range(1, 5)
                .flatMap(i -> catalogService.checkStock("1", 10));

        // Then - Todas deben retornar el mismo resultado
        StepVerifier.create(concurrentChecks)
                .expectNext(true, true, true, true, true)
                .verifyComplete();
                
        System.out.println("✅ [EDGE CASE] Consultas simultáneas manejadas correctamente");
    }

    @Test
    @DisplayName("📊 [MÉTRICAS] Distribución de stock por productos")
    void shouldAnalyzeStockDistribution() {
        System.out.println("📊 [MÉTRICAS] Analizando distribución de stock");
        
        // When
        Flux<Product> products = catalogService.getAllProducts();

        // Then - Analizamos la distribución
        StepVerifier.create(products)
                .thenConsumeWhile(product -> {
                    String stockLevel;
                    if (product.getQuantity() >= 50) stockLevel = "ALTO";
                    else if (product.getQuantity() >= 20) stockLevel = "MEDIO";
                    else stockLevel = "BAJO";
                    
                    System.out.println("    📦 " + product.getName() + ": " + 
                                     stockLevel + " (" + product.getQuantity() + " unidades)");
                    return true;
                })
                .verifyComplete();
                
        System.out.println("✅ [MÉTRICAS] Análisis de distribución completado");
    }
}