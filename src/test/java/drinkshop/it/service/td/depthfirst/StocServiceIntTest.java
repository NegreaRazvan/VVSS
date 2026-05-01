package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test Step 3 (Scenario 3: S -> R -> E, depth-first).
 * Integram si E (Stoc real): toate clasele sunt reale, fara mock-uri.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceIntTest {

    private StocService stocService;
    private Repository<Integer, Stoc> stocRepo; // REAL in-memory

    @BeforeEach
    void setUp() {
        stocRepo = new Repository<>() {
            private final List<Stoc> memorie = new ArrayList<>();

            @Override
            public Stoc findOne(Integer id) {
                return memorie.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
            }

            @Override
            public List<Stoc> findAll() {
                return new ArrayList<>(memorie);
            }

            @Override
            public Stoc save(Stoc s) {
                memorie.add(s);
                return s;
            }

            @Override
            public Stoc delete(Integer id) {
                Stoc s = findOne(id);
                if (s != null) memorie.remove(s);
                return s;
            }

            @Override
            public Stoc update(Stoc s) {
                memorie.removeIf(x -> x.getId() == s.getId());
                memorie.add(s);
                return s;
            }
        };

        stocService = new StocService(stocRepo);
    }

    @AfterEach
    void tearDown() {
        stocService = null;
        stocRepo = null;
    }

    @Test
    @Order(1)
    void testAdd_withRealStocAndRepo() {
        // cream entitati reale Stoc (E)
        Stoc stocNou = new Stoc(1, "menta", 50.0, 5.0);

        stocService.add(stocNou);

        List<Stoc> stocuri = stocService.getAll();

        assertEquals(1, stocuri.size());
        assertEquals("menta", stocuri.get(0).getIngredient());
        assertEquals(50.0, stocuri.get(0).getCantitate());
    }

    @Test
    @Order(2)
    void testAreSuficient_withRealStocAndRepo() {
        Stoc stocApa = new Stoc(1, "apa", 200.0, 0.0);
        Stoc stocZahar = new Stoc(2, "zahar", 300.0, 0.0);

        stocService.add(stocApa);
        stocService.add(stocZahar);

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("apa", 100.0),
                new IngredientReteta("zahar", 150.0)
        ));

        assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    @Order(3)
    void testConsuma_modifyStocViaRepo() {
        Stoc stocApa1 = new Stoc(1, "apa", 100.0, 0.0);
        Stoc stocApa2 = new Stoc(2, "apa", 50.0, 0.0);
        stocService.add(stocApa1);
        stocService.add(stocApa2);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 120.0)));

        stocService.consuma(reteta);

        Stoc stoc1Actualizat = stocRepo.findOne(1);
        Stoc stoc2Actualizat = stocRepo.findOne(2);

        assertEquals(0.0, stoc1Actualizat.getCantitate());
        assertEquals(30.0, stoc2Actualizat.getCantitate());
    }

    @Test
    @Order(4)
    void testAreSuficient_returnsFalse_stocInsuficient() {
        Stoc stocMic = new Stoc(1, "sirop", 10.0, 0.0);
        stocService.add(stocMic);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("sirop", 100.0)));

        assertFalse(stocService.areSuficient(reteta));
    }
}
