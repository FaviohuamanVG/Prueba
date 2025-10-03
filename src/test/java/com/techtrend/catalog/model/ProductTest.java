package com.techtrend.catalog.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🏷️ Pruebas unitarias completas para la entidad Product
 * 
 * Verifica la lógica de negocio de la entidad de dominio:
 * - Validaciones de stock y disponibilidad
 * - Métodos equals y hashCode
 * - Constructores y getters/setters
 * - Casos límite y edge cases
 * 
 * @author TechTrend Development Team
 */
@DisplayName("🏷️ Product Entity - Lógica de Dominio Completa")
class ProductTest {

    // ==================== PRUEBAS DE DISPONIBILIDAD ====================

    @Test
    @DisplayName("✅ Producto con stock debe estar disponible")
    void productWithStockShouldBeAvailable() {
        System.out.println("🏷️ Probando disponibilidad de producto con stock: 10 unidades");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 10);

        // When & Then
        assertTrue(product.isAvailable());
        System.out.println("✅ Test exitoso: Producto con 10 unidades está disponible");
    }

    @Test
    @DisplayName("❌ Producto sin stock no debe estar disponible")
    void productWithoutStockShouldNotBeAvailable() {
        System.out.println("🏷️ Probando disponibilidad de producto sin stock: 0 unidades");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 0);

        // When & Then
        assertFalse(product.isAvailable());
        System.out.println("✅ Test exitoso: Producto con 0 unidades no está disponible");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 25, 50, 100})
    @DisplayName("✅ Productos con stock positivo deben estar disponibles")
    void productsWithPositiveStockShouldBeAvailable(int stock) {
        System.out.println("🏷️ Probando disponibilidad con stock: " + stock);
        
        // Given
        Product product = new Product("1", "Test Product", new BigDecimal("100"), stock);

        // When & Then
        assertTrue(product.isAvailable());
        assertEquals(stock, product.getQuantity());
    }

    // ==================== PRUEBAS DE STOCK ====================

    @Test
    @DisplayName("✅ Debe tener stock suficiente cuando cantidad solicitada ≤ disponible")
    void shouldHaveSufficientStockWhenRequestedQuantityIsLessOrEqual() {
        System.out.println("🏷️ Probando validación de stock suficiente (50 unidades disponibles)");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 50);

        // When & Then
        assertTrue(product.hasStock(10));
        assertTrue(product.hasStock(50));
        
        System.out.println("✅ Test exitoso: Stock suficiente para 10 y 50 unidades");
    }

    @Test
    @DisplayName("❌ No debe tener stock suficiente cuando cantidad solicitada > disponible")
    void shouldNotHaveSufficientStockWhenRequestedQuantityIsGreater() {
        System.out.println("🏷️ Probando validación de stock insuficiente (60 > 50 disponibles)");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 50);

        // When & Then
        assertFalse(product.hasStock(60));
        
        System.out.println("✅ Test exitoso: Stock insuficiente para 60 unidades (solo 50 disponibles)");
    }

    @ParameterizedTest
    @CsvSource({
        "50, 1, true",
        "50, 25, true", 
        "50, 50, true",
        "50, 51, false",
        "50, 100, false",
        "0, 1, false",
        "10, 10, true",
        "10, 11, false"
    })
    @DisplayName("📊 Validación parametrizada de stock suficiente")
    void shouldValidateStockCorrectly(int availableStock, int requestedQuantity, boolean expectedResult) {
        System.out.println("📊 Stock: " + availableStock + ", Solicitado: " + requestedQuantity + 
                         ", Esperado: " + expectedResult);
        
        // Given
        Product product = new Product("1", "Test Product", new BigDecimal("100"), availableStock);

        // When & Then
        assertEquals(expectedResult, product.hasStock(requestedQuantity));
    }

    // ==================== PRUEBAS DE CONSTRUCTORES ====================

    @Test
    @DisplayName("🏗️ Constructor vacío debe crear producto con valores null/0")
    void defaultConstructorShouldCreateEmptyProduct() {
        System.out.println("🏗️ Probando constructor vacío");
        
        // When
        Product product = new Product();

        // Then
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getPrice());
        assertNull(product.getQuantity());
        assertFalse(product.isAvailable()); // null quantity no está disponible
        
        System.out.println("✅ Constructor vacío funciona correctamente");
    }

    @Test
    @DisplayName("🏗️ Constructor completo debe asignar todos los valores")
    void fullConstructorShouldAssignAllValues() {
        System.out.println("🏗️ Probando constructor completo");
        
        // Given
        String id = "1";
        String name = "Laptop Gaming";
        BigDecimal price = new BigDecimal("1299.99");
        Integer quantity = 25;

        // When
        Product product = new Product(id, name, price, quantity);

        // Then
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(quantity, product.getQuantity());
        assertTrue(product.isAvailable());
        
        System.out.println("✅ Constructor completo asigna valores correctamente");
    }

    // ==================== PRUEBAS DE GETTERS Y SETTERS ====================

    @Test
    @DisplayName("🔧 Setters deben modificar valores correctamente")
    void settersShouldModifyValuesCorrectly() {
        System.out.println("🔧 Probando setters de Product");
        
        // Given
        Product product = new Product();

        // When
        product.setId("2");
        product.setName("Mouse Inalámbrico");
        product.setPrice(new BigDecimal("89.99"));
        product.setQuantity(75);

        // Then
        assertEquals("2", product.getId());
        assertEquals("Mouse Inalámbrico", product.getName());
        assertEquals(new BigDecimal("89.99"), product.getPrice());
        assertEquals(75, product.getQuantity());
        assertTrue(product.isAvailable());
        
        System.out.println("✅ Todos los setters funcionan correctamente");
    }

    // ==================== PRUEBAS DE EQUALS Y HASHCODE ====================

    @Test
    @DisplayName("⚖️ Productos iguales deben tener equals() verdadero")
    void equalProductsShouldHaveEqualsTrue() {
        System.out.println("⚖️ Probando equals() para productos iguales");
        
        // Given
        Product product1 = new Product("1", "Laptop", new BigDecimal("1000"), 10);
        Product product2 = new Product("1", "Laptop", new BigDecimal("1000"), 10);

        // When & Then
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
        
        System.out.println("✅ Productos iguales tienen equals() verdadero y mismo hashCode");
    }

    @Test
    @DisplayName("⚖️ Productos diferentes deben tener equals() falso")
    void differentProductsShouldHaveEqualsFalse() {
        System.out.println("⚖️ Probando equals() para productos diferentes");
        
        // Given
        Product product1 = new Product("1", "Laptop", new BigDecimal("1000"), 10);
        Product product2 = new Product("2", "Mouse", new BigDecimal("50"), 20);

        // When & Then
        assertNotEquals(product1, product2);
        
        System.out.println("✅ Productos diferentes tienen equals() falso");
    }

    @Test
    @DisplayName("⚖️ Producto comparado con null debe retornar falso")
    void productComparedWithNullShouldReturnFalse() {
        System.out.println("⚖️ Probando equals() con null");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 10);

        // When & Then
        assertNotEquals(product, null);
        
        System.out.println("✅ Comparación con null retorna falso");
    }

    @Test
    @DisplayName("⚖️ Producto comparado consigo mismo debe retornar verdadero")
    void productComparedWithItselfShouldReturnTrue() {
        System.out.println("⚖️ Probando equals() con el mismo objeto");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 10);

        // When & Then
        assertEquals(product, product);
        
        System.out.println("✅ Comparación consigo mismo retorna verdadero");
    }

    // ==================== CASOS LÍMITE Y EDGE CASES ====================

    @Test
    @DisplayName("🔍 Producto con quantity null debe manejar isAvailable() correctamente")
    void productWithNullQuantityShouldHandleAvailabilityCorrectly() {
        System.out.println("🔍 Probando isAvailable() con quantity null");
        
        // Given
        Product product = new Product("1", "Test", new BigDecimal("100"), null);

        // When & Then
        assertFalse(product.isAvailable()); // null quantity = no disponible
        
        System.out.println("✅ Quantity null manejado correctamente en isAvailable()");
    }

    @Test
    @DisplayName("🔍 Producto debe manejar precios decimales correctamente")
    void productShouldHandleDecimalPricesCorrectly() {
        System.out.println("🔍 Probando manejo de precios decimales");
        
        // Given
        BigDecimal precisePrice = new BigDecimal("1299.9999");
        Product product = new Product("1", "Laptop Premium", precisePrice, 5);

        // When & Then
        assertEquals(precisePrice, product.getPrice());
        assertTrue(product.getPrice().compareTo(new BigDecimal("1299.99")) > 0);
        
        System.out.println("✅ Precios decimales manejados correctamente");
    }

    @Test
    @DisplayName("🔍 hasStock() debe manejar quantity null correctamente")
    void hasStockShouldHandleNullQuantityCorrectly() {
        System.out.println("🔍 Probando hasStock() con quantity null");
        
        // Given
        Product product = new Product("1", "Test", new BigDecimal("100"), null);

        // When & Then
        assertFalse(product.hasStock(1)); // null quantity no puede tener stock
        
        System.out.println("✅ hasStock() con quantity null manejado correctamente");
    }

    @Test
    @DisplayName("💰 Productos con diferentes precios pero mismos otros campos deben ser diferentes")
    void productsWithDifferentPricesButSameOtherFieldsShouldBeDifferent() {
        System.out.println("💰 Probando equals() con diferentes precios");
        
        // Given
        Product product1 = new Product("1", "Laptop", new BigDecimal("1000.00"), 10);
        Product product2 = new Product("1", "Laptop", new BigDecimal("1000.01"), 10);

        // When & Then
        assertNotEquals(product1, product2);
        
        System.out.println("✅ Productos con diferentes precios son diferentes");
    }
}