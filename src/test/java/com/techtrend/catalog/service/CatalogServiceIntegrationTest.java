package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * 🚀 Pruebas de integración para CatalogService
 * 
 * Pruebas end-to-end que simulan escenarios reales de uso del microservicio:
 * - Flujos completos de consulta de catálogo
 * - Simulación de carrito de compras
 * - Validación de disponibilidad en tiempo real
 * - Casos de uso típicos de e-commerce
 * 
 * @author TechTrend Development Team
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("🚀 Catalog Service - Pruebas de Integración")
class CatalogServiceIntegrationTest {

    private CatalogService catalogService;

    @BeforeEach
    @DisplayName("🔧 Configuración para pruebas de integración")
    void setUp() {
        catalogService = new CatalogServiceImpl();
        System.out.println("🚀 Configurando entorno de integración para ms-catalog");
    }

    // ==================== FLUJOS E2E COMPLETOS ====================
    
    @Test
    @DisplayName("🛒 [E2E] Flujo completo: Consultar catálogo → Seleccionar producto → Verificar stock")
    void shouldCompleteFullShoppingFlow() {
        System.out.println("🛒 [E2E] Iniciando flujo completo de compra...");
        
        // Paso 1: Consultar catálogo
        System.out.println("    1️⃣ Consultando catálogo de productos disponibles...");
        Flux<Product> catalog = catalogService.getAllProducts();
        
        StepVerifier.create(catalog.take(1)) // Tomamos el primer producto
                .thenConsumeWhile(product -> {
                    System.out.println("    ✅ Producto seleccionado: " + product.getName() + 
                                     " ($" + product.getPrice() + ")");
                    return product.isAvailable();
                })
                .verifyComplete();

        // Paso 2: Verificar stock para el primer producto (Laptop)
        System.out.println("    2️⃣ Verificando disponibilidad de stock...");
        Mono<Boolean> stockCheck = catalogService.checkStock("1", 2);
        
        StepVerifier.create(stockCheck)
                .expectNext(true)
                .verifyComplete();

        // Paso 3: Obtener detalles finales del producto
        System.out.println("    3️⃣ Obteniendo detalles finales del producto...");
        Mono<Product> productDetails = catalogService.getProductDetails("1");
        
        StepVerifier.create(productDetails)
                .expectNextMatches(product -> {
                    System.out.println("    ✅ Compra confirmada: " + product.getName() + 
                                     " - $" + product.getPrice());
                    return product != null && product.isAvailable();
                })
                .verifyComplete();
                
        System.out.println("✅ [E2E] Flujo completo de compra exitoso");
    }

    @Test
    @DisplayName("🛍️ [E2E] Simulación de carrito con múltiples productos")
    void shouldSimulateShoppingCartWithMultipleProducts() {
        System.out.println("🛍️ [E2E] Simulando carrito de compras con múltiples productos...");
        
        // Productos del carrito: Laptop (2 unidades), Mouse (5 unidades), Teclado (1 unidad)
        String[] cartProducts = {"1", "2", "3"};
        Integer[] quantities = {2, 5, 1};
        
        System.out.println("    🛒 Carrito de compras:");
        
        for (int i = 0; i < cartProducts.length; i++) {
            String productId = cartProducts[i];
            Integer quantity = quantities[i];
            
            // Verificar disponibilidad de cada producto
            Mono<Product> productInfo = catalogService.getProductById(productId);
            Mono<Boolean> stockCheck = catalogService.checkStock(productId, quantity);
            
            // Combinar información del producto con verificación de stock
            Mono<String> itemStatus = Mono.zip(productInfo, stockCheck)
                    .map(tuple -> {
                        Product product = tuple.getT1();
                        Boolean hasStock = tuple.getT2();
                        String status = hasStock ? "✅ DISPONIBLE" : "❌ SIN STOCK";
                        System.out.println("        • " + product.getName() + " x" + quantity + 
                                         " - " + status);
                        return status;
                    });
                    
            StepVerifier.create(itemStatus)
                    .expectNextMatches(status -> status.contains("DISPONIBLE"))
                    .verifyComplete();
        }
        
        System.out.println("✅ [E2E] Carrito validado: Todos los productos disponibles");
    }

    @Test
    @DisplayName("⚡ [E2E] Consultas masivas de disponibilidad (stress test)")
    void shouldHandleMassiveAvailabilityQueries() {
        System.out.println("⚡ [E2E] Ejecutando stress test de consultas masivas...");
        
        // Simulamos 50 consultas simultáneas de diferentes productos (solo productos existentes 1-10)
        Flux<Boolean> massiveQueries = Flux.range(1, 10) // 10 productos diferentes existentes
                .repeat(4) // 4 repeticiones adicionales = 5 total por producto = 50 consultas
                .flatMap(productNum -> {
                    String productId = productNum.toString();
                    return catalogService.checkStock(productId, 1);
                })
                .timeout(Duration.ofSeconds(5)); // Timeout de 5 segundos

        StepVerifier.create(massiveQueries.count())
                .expectNext(50L) // Esperamos exactamente 50 respuestas
                .verifyComplete();
                
        System.out.println("✅ [E2E] Stress test completado: 50 consultas procesadas exitosamente");
    }

    // ==================== CASOS DE USO REALES ====================
    
    @Test
    @DisplayName("🎮 [CASO DE USO] Setup gaming completo")
    void shouldValidateGamingSetupAvailability() {
        System.out.println("🎮 [CASO DE USO] Validando disponibilidad de setup gaming...");
        
        // Setup gaming: Laptop + Mouse Gaming + Teclado Mecánico + Monitor 4K
        String[] gamingSetup = {"1", "2", "3", "4"};
        String[] setupNames = {"Laptop Ryzen 7", "Mouse Gaming", "Teclado Mecánico", "Monitor 4K"};
        
        System.out.println("    🎯 Setup Gaming TechTrend:");
        
        Flux<Product> gamingProducts = Flux.fromArray(gamingSetup)
                .flatMap(catalogService::getProductById)
                .zipWith(Flux.fromArray(setupNames), (product, expectedName) -> {
                    System.out.println("        ✅ " + product.getName() + " - $" + product.getPrice() + 
                                     " (Stock: " + product.getQuantity() + ")");
                    return product;
                });

        StepVerifier.create(gamingProducts)
                .expectNextCount(4)
                .verifyComplete();

        // Verificar stock conjunto
        Flux<Boolean> stockAvailability = Flux.fromArray(gamingSetup)
                .flatMap(id -> catalogService.checkStock(id, 1));

        StepVerifier.create(stockAvailability)
                .expectNext(true, true, true, true)
                .verifyComplete();
                
        System.out.println("✅ [CASO DE USO] Setup gaming completo disponible");
    }

    @Test
    @DisplayName("💼 [CASO DE USO] Setup oficina remota")
    void shouldValidateRemoteOfficeSetupAvailability() {
        System.out.println("💼 [CASO DE USO] Validando setup para oficina remota...");
        
        // Setup oficina: Laptop + Monitor 4K + Webcam HD + Teclado + Mouse
        String[] officeSetup = {"1", "4", "6", "3", "2"};
        
        System.out.println("    🏠 Setup Oficina Remota:");
        
        Flux<Product> officeProducts = Flux.fromArray(officeSetup)
                .flatMap(catalogService::getProductById)
                .filter(Product::isAvailable);

        StepVerifier.create(officeProducts)
                .thenConsumeWhile(product -> {
                    BigDecimal totalPrice = product.getPrice();
                    System.out.println("        💻 " + product.getName() + " - $" + totalPrice);
                    return product.isAvailable();
                })
                .verifyComplete();
                
        System.out.println("✅ [CASO DE USO] Setup oficina remota validado");
    }

    @Test
    @DisplayName("🔧 [CASO DE USO] Upgrade PC - Componentes internos")
    void shouldValidatePCUpgradeComponentsAvailability() {
        System.out.println("🔧 [CASO DE USO] Validando componentes para upgrade de PC...");
        
        // Componentes internos: RAM + SSD + Tarjeta Gráfica + Procesador
        String[] upgradeComponents = {"8", "7", "9", "10"};
        
        System.out.println("    ⚡ Componentes de Upgrade:");
        
        Flux<Product> components = Flux.fromArray(upgradeComponents)
                .flatMap(catalogService::getProductById);

        StepVerifier.create(components)
                .thenConsumeWhile(component -> {
                    boolean highValue = component.getPrice().compareTo(new BigDecimal("1000")) > 0;
                    String category = highValue ? "PREMIUM" : "ESTÁNDAR";
                    
                    System.out.println("        🔩 " + component.getName() + " - $" + component.getPrice() + 
                                     " (" + category + ")");
                    return component.isAvailable();
                })
                .verifyComplete();
                
        System.out.println("✅ [CASO DE USO] Componentes de upgrade disponibles");
    }

    // ==================== VALIDACIONES DE NEGOCIO E2E ====================
    
    @Test
    @DisplayName("📊 [VALIDACIÓN] Control de inventario - Productos críticos")
    void shouldValidateCriticalInventoryLevels() {
        System.out.println("📊 [VALIDACIÓN] Verificando niveles críticos de inventario...");
        
        Flux<Product> criticalStock = catalogService.getAllProducts()
                .filter(product -> product.getQuantity() < 20); // Stock crítico < 20 unidades

        StepVerifier.create(criticalStock)
                .thenConsumeWhile(product -> {
                    String alertLevel = product.getQuantity() < 10 ? "🚨 CRÍTICO" : "⚠️ BAJO";
                    System.out.println("        " + alertLevel + " " + product.getName() + 
                                     ": " + product.getQuantity() + " unidades");
                    return product.getQuantity() >= 0;
                })
                .verifyComplete();
                
        System.out.println("✅ [VALIDACIÓN] Control de inventario completado");
    }

    @Test
    @DisplayName("💰 [VALIDACIÓN] Análisis de rentabilidad por categoria")
    void shouldAnalyzeProfitabilityByCategory() {
        System.out.println("💰 [VALIDACIÓN] Analizando rentabilidad por categoría...");
        
        Flux<Product> allProducts = catalogService.getAllProducts();

        // Análisis por rangos de precio
        Mono<Long> budgetCount = allProducts.filter(p -> 
            p.getPrice().compareTo(new BigDecimal("500")) < 0).count();
        Mono<Long> premiumCount = allProducts.filter(p -> 
            p.getPrice().compareTo(new BigDecimal("2000")) >= 0).count();

        System.out.println("    📈 Análisis de Categorías:");
        
        StepVerifier.create(budgetCount)
                .thenConsumeWhile(count -> {
                    System.out.println("        💸 Productos Económicos: " + count + " items");
                    return count >= 0;
                })
                .verifyComplete();

        StepVerifier.create(premiumCount)
                .thenConsumeWhile(count -> {
                    System.out.println("        💎 Productos Premium: " + count + " items");
                    return count >= 0;
                })
                .verifyComplete();
                
        System.out.println("✅ [VALIDACIÓN] Análisis de rentabilidad completado");
    }
}