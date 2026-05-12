package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.Test;
import org.junit.Assert;

public class ProductValidatorECPTest {

    private final ProductValidator validator = new ProductValidator();

    @Test
    public void tc1_ecp_validProduct() {
        Product product = new Product(1, "Cappuccino", 12.5, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        try {
            validator.validate(product);
        } catch (ValidationException e) {
            Assert.fail("Should not throw exception for valid product");
        }
    }

    @Test
    public void tc2_ecp_invalidNumeEmpty() {
        Product product = new Product(2, "", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        try {
            validator.validate(product);
            Assert.fail("Should have thrown ValidationException for empty name");
        } catch (ValidationException exception) {
            Assert.assertTrue(exception.getMessage().contains("Numele nu poate fi gol!"));
        }
    }

    @Test
    public void tc3_ecp_invalidPretNegativ() {
        Product product = new Product(3, "Espresso", -5.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        try {
            validator.validate(product);
            Assert.fail("Should have thrown ValidationException for negative price");
        } catch (ValidationException exception) {
            Assert.assertTrue(exception.getMessage().contains("Pret invalid!"));
        }
    }

    @Test
    public void tc4_ecp_invalidNumeNullPretZero() {
        Product product = new Product(4, null, 0.0, CategorieBautura.TEA, TipBautura.WATER_BASED);
        try {
            validator.validate(product);
            Assert.fail("Should have thrown ValidationException for null name/zero price");
        } catch (ValidationException exception) {
            String msg = exception.getMessage();
            Assert.assertTrue(msg.contains("Numele nu poate fi gol!"));
            Assert.assertTrue(msg.contains("Pret invalid!"));
        }
    }
}