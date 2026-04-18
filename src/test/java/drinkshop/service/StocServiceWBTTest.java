package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White-Box Testing (WBT) for StocService.areSuficient(Reteta)
 *
 * CFG Decisions:
 *   D1: reteta == null
 *   D2: ingredienteNecesare == null
 *   D3: ingredienteNecesare.isEmpty()
 *   D4: for-each loop condition (has more elements)
 *   D5: necesar <= 0
 *   D6: disponibil < necesar
 *
 * Cyclomatic Complexity: CC = 6 decisions + 1 = 7
 */
@DisplayName("WBT Tests - StocService.areSuficient()")
class StocServiceWBTTest {

    private List<Stoc> stocList;
    private StocService stocService;

    @BeforeEach
    void setUp() {
        stocList = new ArrayList<>();
        Repository<Integer, Stoc> stocRepo = new Repository<>() {
            @Override
            public Stoc findOne(Integer id) {
                return stocList.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
            }

            @Override
            public List<Stoc> findAll() {
                return new ArrayList<>(stocList);
            }

            @Override
            public Stoc save(Stoc s) {
                stocList.add(s);
                return s;
            }

            @Override
            public Stoc delete(Integer id) {
                Stoc s = findOne(id);
                stocList.remove(s);
                return s;
            }

            @Override
            public Stoc update(Stoc s) {
                stocList.removeIf(x -> x.getId() == s.getId());
                stocList.add(s);
                return s;
            }
        };
        stocService = new StocService(stocRepo);
    }

    @AfterEach
    void tearDown() {
        stocList = null;
        stocService = null;
    }

    // -------------------------------------------------------------------------
    // TC01 – Path P1 | Coverage: SC, DC(D1=T), CC, DCC, APC
    // Non-valid input: reteta null -> D1=T -> throws IllegalArgumentException
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC01 [P1] reteta=null -> throws IllegalArgumentException")
    void tc01_reteta_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> stocService.areSuficient(null));
    }

    // -------------------------------------------------------------------------
    // TC02 – Path P2 | Coverage: SC, DC(D1=F, D2=T), CC, DCC, APC, LC(0 iters)
    // Non-valid input: ingredienteNecesare==null -> D2=T -> returns true
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC02 [P2] ingredienteNecesare=null -> returns true")
    void tc02_ingredienteNull_returnsTrue() {
        Reteta reteta = new Reteta(1, null);
        assertTrue(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC03 – Path P3 | Coverage: SC, DC(D3=T), CC, DCC, APC, LC(0 iters)
    // Valid input: lista goala -> D3=T -> returns true
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC03 [P3] ingredienteNecesare empty -> returns true")
    void tc03_ingredienteEmpty_returnsTrue() {
        Reteta reteta = new Reteta(1, new ArrayList<>());
        assertTrue(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC04 – Path P6 | Coverage: SC, DC(D5=T), CC, DCC, MCC, APC, LC(1 iter)
    // Valid input: cantitate=0 -> D5=T (necesar<=0) -> continue -> return true
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC04 [P6] necesar=0 (cantitate<=0) -> skipped, returns true")
    void tc04_necesar_zero_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 0.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 0.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC05 – Path P5 | Coverage: SC, DC(D6=T), CC, DCC, MCC, APC, LC(1 iter)
    // Non-valid input: stoc insuficient -> D6=T -> returns false
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC05 [P5] disponibil(50) < necesar(100) -> returns false")
    void tc05_stocInsuficient_returnsFalse() {
        stocList.add(new Stoc(1, "apa", 50.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertFalse(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC06 – Path P7 | Coverage: SC, DC(D6=F), CC, DCC, MCC, APC, LC(1 iter)
    // Valid input: stoc suficient -> D6=F -> return true
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC06 [P7] disponibil(150) >= necesar(100) -> returns true")
    void tc06_stocSuficient_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 150.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC07 – Path P7 | Coverage: SC, DC, CC, DCC, MCC, APC, LC(2 iters)
    // Valid input: 2 ingrediente, ambele suficiente -> return true
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC07 [P7] 2 ingrediente suficiente -> returns true")
    void tc07_douaIngredienteSuficiente_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 200.0, 0.0));
        stocList.add(new Stoc(2, "zahar", 300.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("apa", 100.0),
                new IngredientReteta("zahar", 200.0)
        ));
        assertTrue(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC08 – APC, LC(2 iters, al doilea esueaza)
    // Valid input: 2 ingrediente, al doilea insuficient -> returns false
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC08 al doilea ingredient insuficient -> returns false")
    void tc08_alDoileaIngredientInsuficient_returnsFalse() {
        stocList.add(new Stoc(1, "apa", 200.0, 0.0));
        stocList.add(new Stoc(2, "zahar", 50.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("apa", 100.0),
                new IngredientReteta("zahar", 200.0)
        ));
        assertFalse(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC09 – MCC boundary: disponibil == necesar (exact) -> returns true
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC09 [MCC boundary] disponibil(100) == necesar(100) -> returns true")
    void tc09_disponibilEgalNecesar_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 100.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC10 – APC, LC(1 iter): cantitate negativa -> D5=T (necesar<=0) -> continue -> return true
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC10 necesar negativ (-5) -> skipped, returns true")
    void tc10_necesar_negativ_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 0.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", -5.0)));
        assertTrue(stocService.areSuficient(reteta));
    }
}
