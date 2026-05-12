package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * White-Box Testing (WBT) for StocService
 * Refactored to use Mockito to meet Lab requirements (assert + verify).
 *
 * CFG Decisions (areSuficient):
 * D1: reteta == null
 * D2: ingredienteNecesare == null
 * D3: ingredienteNecesare.isEmpty()
 * D4: for-each loop condition (has more elements)
 * D5: necesar <= 0
 * D6: disponibil < necesar
 *
 * Cyclomatic Complexity: CC = 6 decisions + 1 = 7
 */
@RunWith(MockitoJUnitRunner.class)
class StocServiceWBTTest {

    @Mock
    private Repository<Integer, Stoc> stocRepo;

    @InjectMocks
    private StocService stocService;

    // Folosim o lista reala pentru a o returna prin mock la findAll()
    private List<Stoc> stocList;

    @Before
    void setUp() {
        stocList = new ArrayList<>();
        // Setam mock-ul sa returneze lista noastra de fiecare data cand se apeleaza findAll().
        // lenient() previne exceptiile in testele unde findAll() nu ajunge sa fie apelat (ex: tc01, tc02).
        lenient().when(stocRepo.findAll()).thenReturn(stocList);
    }

    @After
    void tearDown() {
        stocList = null;
    }

    // =========================================================================
    // TESTELE ORIGINALE PENTRU areSuficient(Reteta) - Neschimbate la logica
    // =========================================================================

    @Test
    void tc01_reteta_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> stocService.areSuficient(null));
    }

    @Test
    void tc02_ingredienteNull_returnsTrue() {
        Reteta reteta = new Reteta(1, null);
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    void tc03_ingredienteEmpty_returnsTrue() {
        Reteta reteta = new Reteta(1, new ArrayList<>());
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    void tc04_necesar_zero_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 0.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 0.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    void tc05_stocInsuficient_returnsFalse() {
        stocList.add(new Stoc(1, "apa", 50.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertFalse(stocService.areSuficient(reteta));
    }

    @Test
    void tc06_stocSuficient_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 150.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    void tc07_douaIngredienteSuficiente_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 200.0, 0.0));
        stocList.add(new Stoc(2, "zahar", 300.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("apa", 100.0),
                new IngredientReteta("zahar", 200.0)
        ));
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    void tc08_alDoileaIngredientInsuficient_returnsFalse() {
        stocList.add(new Stoc(1, "apa", 200.0, 0.0));
        stocList.add(new Stoc(2, "zahar", 50.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("apa", 100.0),
                new IngredientReteta("zahar", 200.0)
        ));
        assertFalse(stocService.areSuficient(reteta));
    }

    @Test
    void tc09_disponibilEgalNecesar_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 100.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    void tc10_necesar_negativ_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 0.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", -5.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    // =========================================================================
    // TESTE ADIȚIONALE PENTRU FULL COVERAGE & MOCKITO VERIFY
    // =========================================================================

    @Test
    void test_getAll() {
        stocList.add(new Stoc(1, "cafea", 10.0, 0.0));

        List<Stoc> rezultate = stocService.getAll();

        assertEquals(1, rezultate.size());
        assertEquals("cafea", rezultate.get(0).getIngredient());
        verify(stocRepo, times(1)).findAll(); // Verificare mock
    }

    @Test
    void test_add() {
        Stoc s = new Stoc(1, "lapte", 50.0, 0.0);

        stocService.add(s);

        verify(stocRepo, times(1)).save(s); // Verificare mock
    }

    @Test
    void test_update() {
        Stoc s = new Stoc(1, "sirop", 20.0, 0.0);

        stocService.update(s);

        verify(stocRepo, times(1)).update(s); // Verificare mock
    }

    @Test
    void test_delete() {
        stocService.delete(1);

        verify(stocRepo, times(1)).delete(1); // Verificare mock
    }

    @Test
    void test_consuma_stocInsuficient_throwsException() {
        stocList.add(new Stoc(1, "apa", 20.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));

        assertThrows(IllegalStateException.class, () -> stocService.consuma(reteta));
        verify(stocRepo, never()).update(any()); // Niciun update daca arunca exceptie
    }

    @Test
    void test_consuma_intrerupeCandRamasZero() {
        Stoc stoc1 = new Stoc(1, "zahar", 100.0, 0.0);
        Stoc stoc2 = new Stoc(2, "zahar", 50.0, 0.0);
        stocList.add(stoc1);
        stocList.add(stoc2);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("zahar", 60.0)));

        stocService.consuma(reteta);

        // Prima inregistrare a fost consumata partial
        assertEquals(40.0, stoc1.getCantitate());
        // A doua inregistrare a ramas neatinsa
        assertEquals(50.0, stoc2.getCantitate());

        // Verificam ca s-a facut update doar pentru stoc1
        verify(stocRepo, times(1)).update(stoc1);
        verify(stocRepo, never()).update(stoc2);
    }

    @Test
    void test_consuma_extrageDinMaiMulteStocuri() {
        Stoc stoc1 = new Stoc(1, "apa", 50.0, 0.0);
        Stoc stoc2 = new Stoc(2, "apa", 50.0, 0.0);
        stocList.add(stoc1);
        stocList.add(stoc2);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 80.0)));

        stocService.consuma(reteta);

        assertEquals(0.0, stoc1.getCantitate());
        assertEquals(20.0, stoc2.getCantitate());

        // Verificam ca s-a facut update la ambele intrari
        verify(stocRepo, times(1)).update(stoc1);
        verify(stocRepo, times(1)).update(stoc2);
    }

    @Test
    void test_consuma_cantitateZero() {
        Stoc stoc1 = new Stoc(1, "apa", 100.0, 0.0);
        stocList.add(stoc1);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 0.0)));

        stocService.consuma(reteta);

        assertEquals(100.0, stoc1.getCantitate());
        verify(stocRepo, never()).update(any()); // Nu s-a consumat nimic
    }
}