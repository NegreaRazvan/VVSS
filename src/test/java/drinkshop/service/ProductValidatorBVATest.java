package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;



@Tag("BVA")
@DisplayName("BVA Tests - ProductValidator.validate()")
class ProductValidatorBVATest {

    private final ProductValidator validator = new ProductValidator();

    @ParameterizedTest
    @DisplayName("TC1_BVA: Price slightly above zero (Valid Min)")
    @CsvSource({"1, 'Espresso', 0.01, CLASSIC_COFFEE, BASIC"})
    void tc1_bva_validMinPrice(int id, String nume, double pret, CategorieBautura cat, TipBautura tip) {
        Product product = new Product(id, nume, pret, cat, tip);
        assertDoesNotThrow(() -> validator.validate(product));
    }

    @ParameterizedTest
    @DisplayName("TC2_BVA: Price is zero (Invalid Boundary)")
    @CsvSource({"2, 'Espresso', 0.0, CLASSIC_COFFEE, BASIC"})
    void tc2_bva_invalidZeroPrice(int id, String nume, double pret, CategorieBautura cat, TipBautura tip) {
        Product product = new Product(id, nume, pret, cat, tip);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(product));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
    }

    @ParameterizedTest
    @DisplayName("TC3_BVA: Price slightly below zero (Invalid)")
    @CsvSource({"3, 'Espresso', -0.01, CLASSIC_COFFEE, BASIC"})
    void tc3_bva_invalidNegativePrice(int id, String nume, double pret, CategorieBautura cat, TipBautura tip) {
        Product product = new Product(id, nume, pret, cat, tip);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(product));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
    }

    @ParameterizedTest
    @DisplayName("TC4_BVA: Price is 1.0 (Valid Control Point)")
    @CsvSource({"4, 'Espresso', 1.0, CLASSIC_COFFEE, BASIC"})
    void tc4_bva_validControlPrice(int id, String nume, double pret, CategorieBautura cat, TipBautura tip) {
        Product product = new Product(id, nume, pret, cat, tip);
        assertDoesNotThrow(() -> validator.validate(product));
    }

    @ParameterizedTest
    @DisplayName("TC5_BVA: Name is single character (Valid Min)")
    @CsvSource({"5, 'A', 10.0, TEA, WATER_BASED"})
    void tc5_bva_validShortName(int id, String nume, double pret, CategorieBautura cat, TipBautura tip) {
        Product product = new Product(id, nume, pret, cat, tip);
        assertDoesNotThrow(() -> validator.validate(product));
    }

    @ParameterizedTest
    @DisplayName("TC6_BVA: Name is two characters (Valid Min+1)")
    @CsvSource({"6, 'AB', 10.0, TEA, WATER_BASED"})
    void tc6_bva_validMinPlusOneName(int id, String nume, double pret, CategorieBautura cat, TipBautura tip) {
        Product product = new Product(id, nume, pret, cat, tip);
        assertDoesNotThrow(() -> validator.validate(product));
    }
}