package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
@ExtendWith(MockitoExtension.class)
@DisplayName("WBT Tests - StocService (Mockito)")
class StocServiceWBTTest {

    @Mock
    private Repository<Integer, Stoc> stocRepo;

    @InjectMocks
    private StocService stocService;

    // Folosim o lista reala pentru a o returna prin mock la findAll()
    private List<Stoc> stocList;

    @BeforeEach
    void setUp() {
        stocList = new ArrayList<>();
        // Setam mock-ul sa returneze lista noastra de fiecare data cand se apeleaza findAll().
        // lenient() previne exceptiile in testele unde findAll() nu ajunge sa fie apelat (ex: tc01, tc02).
        lenient().when(stocRepo.findAll()).thenReturn(stocList);
    }

    @AfterEach
    void tearDown() {
        stocList = null;
    }

    // =========================================================================
    // TESTELE ORIGINALE PENTRU areSuficient(Reteta) - Neschimbate la logica
    // =========================================================================

    @Test
    @DisplayName("TC01 [P1] reteta=null -> throws IllegalArgumentException")
    void tc01_reteta_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> stocService.areSuficient(null));
    }

    @Test
    @DisplayName("TC02 [P2] ingredienteNecesare=null -> returns true")
    void tc02_ingredienteNull_returnsTrue() {
        Reteta reteta = new Reteta(1, null);
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    @DisplayName("TC03 [P3] ingredienteNecesare empty -> returns true")
    void tc03_ingredienteEmpty_returnsTrue() {
        Reteta reteta = new Reteta(1, new ArrayList<>());
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    @DisplayName("TC04 [P6] necesar=0 (cantitate<=0) -> skipped, returns true")
    void tc04_necesar_zero_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 0.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 0.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    @DisplayName("TC05 [P5] disponibil(50) < necesar(100) -> returns false")
    void tc05_stocInsuficient_returnsFalse() {
        stocList.add(new Stoc(1, "apa", 50.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertFalse(stocService.areSuficient(reteta));
    }

    @Test
    @DisplayName("TC06 [P7] disponibil(150) >= necesar(100) -> returns true")
    void tc06_stocSuficient_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 150.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

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

    @Test
    @DisplayName("TC09 [MCC boundary] disponibil(100) == necesar(100) -> returns true")
    void tc09_disponibilEgalNecesar_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 100.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    @DisplayName("TC10 necesar negativ (-5) -> skipped, returns true")
    void tc10_necesar_negativ_returnsTrue() {
        stocList.add(new Stoc(1, "apa", 0.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", -5.0)));
        assertTrue(stocService.areSuficient(reteta));
    }

    // =========================================================================
    // TESTE ADIȚIONALE PENTRU FULL COVERAGE & MOCKITO VERIFY
    // =========================================================================

    @Test
    @DisplayName("Test getAll() - returneaza inregistrarile si apeleaza findAll")
    void test_getAll() {
        stocList.add(new Stoc(1, "cafea", 10.0, 0.0));

        List<Stoc> rezultate = stocService.getAll();

        assertEquals(1, rezultate.size());
        assertEquals("cafea", rezultate.get(0).getIngredient());
        verify(stocRepo, times(1)).findAll(); // Verificare mock
    }

    @Test
    @DisplayName("Test add() - apeleaza save pe repository")
    void test_add() {
        Stoc s = new Stoc(1, "lapte", 50.0, 0.0);

        stocService.add(s);

        verify(stocRepo, times(1)).save(s); // Verificare mock
    }

    @Test
    @DisplayName("Test update() - apeleaza update pe repository")
    void test_update() {
        Stoc s = new Stoc(1, "sirop", 20.0, 0.0);

        stocService.update(s);

        verify(stocRepo, times(1)).update(s); // Verificare mock
    }

    @Test
    @DisplayName("Test delete() - apeleaza delete pe repository")
    void test_delete() {
        stocService.delete(1);

        verify(stocRepo, times(1)).delete(1); // Verificare mock
    }

    @Test
    @DisplayName("consuma() arunca IllegalStateException daca stocul nu este suficient")
    void test_consuma_stocInsuficient_throwsException() {
        stocList.add(new Stoc(1, "apa", 20.0, 0.0));
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));

        assertThrows(IllegalStateException.class, () -> stocService.consuma(reteta));
        verify(stocRepo, never()).update(any()); // Niciun update daca arunca exceptie
    }

    @Test
    @DisplayName("consuma() isi opreste executia pentru un ingredient cand necesarul a fost indeplinit")
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
    @DisplayName("consuma() extrage din multiple intrari de stoc pana la satisfacerea necesarului")
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
    @DisplayName("consuma() nu face modificari daca necesarul este 0")
    void test_consuma_cantitateZero() {
        Stoc stoc1 = new Stoc(1, "apa", 100.0, 0.0);
        stocList.add(stoc1);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 0.0)));

        stocService.consuma(reteta);

        assertEquals(100.0, stoc1.getCantitate());
        verify(stocRepo, never()).update(any()); // Nu s-a consumat nimic
    }
}