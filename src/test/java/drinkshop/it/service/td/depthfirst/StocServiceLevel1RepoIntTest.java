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

import static org.mockito.Mockito.*;

/**
 * Integration Test Step 2 (Scenario 3: S -> R -> E, depth-first).
 * Integram R (Repository real in-memory); E (Stoc) ramane mock.
 */
public class StocServiceLevel1RepoIntTest {

    private Stoc stoc;
    private Repository<Integer, Stoc> stocRepo; // REAL in-memory
    private StocService stocService;

    @Before
    public void setUp() {
        stoc = mock(Stoc.class);

        // integram Repository-ul cu implementare reala in memorie
        stocRepo = new Repository<Integer, Stoc>() {
            private final List<Stoc> memorie = new ArrayList<>();

            @Override
            public Stoc findOne(Integer id) {
                // Java 11 compatible stream logic
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

    @After
    public void tearDown() {
        stocService = null;
        stocRepo = null;
        stoc = null;
    }

    @Test
    public void testAdd_withRealRepo_mockStoc() {
        when(stoc.getId()).thenReturn(1);

        try {
            stocService.add(stoc);
        } catch (Exception e) {
            Assert.fail("Add should not throw: " + e.getMessage());
        }

        Assert.assertEquals(1, stocService.getAll().size());
        verify(stoc, never()).getIngredient();
    }

    @Test
    public void testGetAll_afterAddMultiple_withRealRepo_mockStoc() {
        Stoc stoc1 = mock(Stoc.class);
        Stoc stoc2 = mock(Stoc.class);

        stocService.add(stoc1);
        stocService.add(stoc2);

        Assert.assertEquals(2, stocService.getAll().size());
        verify(stoc1, never()).getId();
        verify(stoc2, never()).getId();
    }

    @Test
    public void testAreSuficient_returnsTrue_withRealRepo_mockStoc() {
        when(stoc.getIngredient()).thenReturn("apa");
        when(stoc.getCantitate()).thenReturn(200.0);
        stocService.add(stoc);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));

        Assert.assertTrue(stocService.areSuficient(reteta));
        verify(stoc, atLeastOnce()).getIngredient();
        verify(stoc, atLeastOnce()).getCantitate();
    }

    @Test
    public void testAreSuficient_returnsFalse_withRealRepo_mockStoc() {
        when(stoc.getIngredient()).thenReturn("lapte");
        when(stoc.getCantitate()).thenReturn(30.0);
        stocService.add(stoc);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("lapte", 100.0)));

        Assert.assertFalse(stocService.areSuficient(reteta));
        verify(stoc, atLeastOnce()).getCantitate();
    }
}