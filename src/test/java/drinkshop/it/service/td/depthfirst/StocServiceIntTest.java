package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Integration Test Step 3 (Scenario 3: S -> R -> E, depth-first).
 * Integram si E (Stoc real): toate clasele sunt reale, fara mock-uri.
 */
public class StocServiceIntTest {

    private StocService stocService;
    private Repository<Integer, Stoc> stocRepo; // REAL in-memory

    @Before
    public void setUp() {
        stocRepo = new Repository<Integer, Stoc>() {
            private final List<Stoc> memorie = new ArrayList<>();

            @Override
            public Stoc findOne(Integer id) {
                return memorie.stream().filter(s -> s.getId()==(id)).findFirst().orElse(null);
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
                memorie.removeIf(x -> x.getId()==(s.getId()));
                memorie.add(s);
                return s;
            }
        };

        stocService = new StocService(stocRepo);
    }

    @After
    public void tearDown() {
        stocService = null;
        stocRepo = null;
    }

    @Test
    public void testAdd_withRealStocAndRepo() {
        Stoc stocNou = new Stoc(1, "menta", 50.0, 5.0);
        stocService.add(stocNou);

        List<Stoc> stocuri = stocService.getAll();

        Assert.assertEquals(1, stocuri.size());
        Assert.assertEquals("menta", stocuri.get(0).getIngredient());
        Assert.assertEquals(50.0, stocuri.get(0).getCantitate(), 0.001);
    }

    @Test
    public void testAreSuficient_withRealStocAndRepo() {
        Stoc stocApa = new Stoc(1, "apa", 200.0, 0.0);
        Stoc stocZahar = new Stoc(2, "zahar", 300.0, 0.0);

        stocService.add(stocApa);
        stocService.add(stocZahar);

        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("apa", 100.0),
                new IngredientReteta("zahar", 150.0)
        ));

        Assert.assertTrue(stocService.areSuficient(reteta));
    }

    @Test
    public void testConsuma_modifyStocViaRepo() {
        Stoc stocApa1 = new Stoc(1, "apa", 100.0, 0.0);
        Stoc stocApa2 = new Stoc(2, "apa", 50.0, 0.0);
        stocService.add(stocApa1);
        stocService.add(stocApa2);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 120.0)));

        stocService.consuma(reteta);

        Stoc stoc1Actualizat = stocRepo.findOne(1);
        Stoc stoc2Actualizat = stocRepo.findOne(2);

        Assert.assertEquals(0.0, stoc1Actualizat.getCantitate(), 0.001);
        Assert.assertEquals(30.0, stoc2Actualizat.getCantitate(), 0.001);
    }

    @Test
    public void testAreSuficient_returnsFalse_stocInsuficient() {
        Stoc stocMic = new Stoc(1, "sirop", 10.0, 0.0);
        stocService.add(stocMic);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("sirop", 100.0)));

        Assert.assertFalse(stocService.areSuficient(reteta));
    }
}