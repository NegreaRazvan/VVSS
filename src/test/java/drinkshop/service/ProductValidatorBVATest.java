package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.Test;
import org.junit.Assert;

public class ProductValidatorBVATest {

    private final ProductValidator validator = new ProductValidator();

    @Test
    public void tc1_bva_validMinPrice() {
        Product product = new Product(1, "Espresso", 0.01, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        try {
            validator.validate(product);
        } catch (ValidationException e) {
            Assert.fail("Should not throw exception for price 0.01");
        }
    }

    @Test
    public void tc2_bva_invalidZeroPrice() {
        Product product = new Product(2, "Espresso", 0.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        try {
            validator.validate(product);
            Assert.fail("Should have thrown ValidationException for price 0.0");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Pret invalid!"));
        }
    }

    @Test
    public void tc3_bva_invalidNegativePrice() {
        Product product = new Product(3, "Espresso", -0.01, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        try {
            validator.validate(product);
            Assert.fail("Should have thrown ValidationException for price -0.01");
        } catch (ValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Pret invalid!"));
        }
    }

    @Test
    public void tc4_bva_validControlPrice() {
        Product product = new Product(4, "Espresso", 1.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        try {
            validator.validate(product);
        } catch (ValidationException e) {
            Assert.fail("Should not throw exception for price 1.0");
        }
    }

    @Test
    public void tc5_bva_validShortName() {
        Product product = new Product(5, "A", 10.0, CategorieBautura.TEA, TipBautura.WATER_BASED);
        try {
            validator.validate(product);
        } catch (ValidationException e) {
            Assert.fail("Should not throw exception for single character name");
        }
    }

    @Test
    public void tc6_bva_validMinPlusOneName() {
        Product product = new Product(6, "AB", 10.0, CategorieBautura.TEA, TipBautura.WATER_BASED);
        try {
            validator.validate(product);
        } catch (ValidationException e) {
            Assert.fail("Should not throw exception for two character name");
        }
    }
}