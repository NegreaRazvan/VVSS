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
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ECP")
@DisplayName("ECP Tests - ProductValidator.validate()")
class ProductValidatorECPTest {

    private final ProductValidator validator = new ProductValidator();

    @ParameterizedTest
    @DisplayName("TC1_ECP: Valid product - non-empty name and positive price")
    @CsvSource({
            "1, Cappuccino, 12.5, MILK_COFFEE, DAIRY"
    })
    void tc1_ecp_validProduct(int id, String nume, double pret, CategorieBautura categorie, TipBautura tip) {
        Product product = new Product(id, nume, pret, categorie, tip);

        assertDoesNotThrow(() -> validator.validate(product));
    }

    @ParameterizedTest
    @DisplayName("TC2_ECP: Invalid product - empty name")
    @ValueSource(strings = {"", "   "})
    void tc2_ecp_invalidNumeEmpty(String nume) {
        Product product = new Product(2, nume, 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(product));

        assertTrue(exception.getMessage().contains("Numele nu poate fi gol!"));
    }

    @ParameterizedTest
    @DisplayName("TC3_ECP: Invalid product - negative price")
    @CsvSource({
            "3, Espresso, -5.0, CLASSIC_COFFEE, BASIC"
    })
    void tc3_ecp_invalidPretNegativ(int id, String nume, double pret, CategorieBautura categorie, TipBautura tip) {
        Product product = new Product(id, nume, pret, categorie, tip);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(product));

        assertTrue(exception.getMessage().contains("Pret invalid!"));
    }

    @ParameterizedTest
    @DisplayName("TC4_ECP: Invalid product - null name and zero price")
    @CsvSource({
            "4, 0.0, TEA, WATER_BASED"
    })
    void tc4_ecp_invalidNumeNullPretZero(int id, double pret, CategorieBautura categorie, TipBautura tip) {
        Product product = new Product(id, null, pret, categorie, tip);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(product));

        String msg = exception.getMessage();
        assertTrue(msg.contains("Numele nu poate fi gol!"));
        assertTrue(msg.contains("Pret invalid!"));
    }
}
