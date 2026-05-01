package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/**
 * Integration Test Step 2 (Scenario 3: S -> R -> E, depth-first).
 * Integram R (Repository real in-memory); E (Stoc) ramane mock.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceLevel1RepoIntTest {

    private Stoc stoc;
    private Repository<Integer, Stoc> stocRepo; // REAL in-memory
    private StocService stocService;

    @BeforeEach
    void setUp() {
        // cream obiect mock pentru Stoc (E) - inca nemockeuit
        stoc = mock(Stoc.class);

        // integram Repository-ul cu implementare reala in memorie
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
        stoc = null;
    }

    @Test
    @Order(1)
    void testAdd_withRealRepo_mockStoc() {
        // asociem comportamente obiectului mock Stoc (E)
        when(stoc.getId()).thenReturn(1);

        // apelam metoda add si evaluam cu fail
        try {
            stocService.add(stoc);
        } catch (Exception e) {
            fail("Add should not throw: " + e.getMessage());
        }

        // evaluam cu assert ca repository-ul real contine mock-ul
        assert 1 == stocService.getAll().size();

        // verificam interactiunile cu obiectul mock ramas (Stoc)
        verify(stoc, never()).getIngredient(); // add nu apeleaza getIngredient
    }

    @Test
    @Order(2)
    void testGetAll_afterAddMultiple_withRealRepo_mockStoc() {
        Stoc stoc1 = mock(Stoc.class);
        Stoc stoc2 = mock(Stoc.class);

        stocService.add(stoc1);
        stocService.add(stoc2);

        // evaluam cu assert ca repo-ul real a stocat ambele mock-uri
        assert 2 == stocService.getAll().size();

        // verificam ca nici un mock nu a fost interogat (add nu foloseste campuri Stoc)
        verify(stoc1, never()).getId();
        verify(stoc2, never()).getId();
    }

    @Test
    @Order(3)
    void testAreSuficient_returnsTrue_withRealRepo_mockStoc() {
        // asociem comportamente mock-ului Stoc (E): ingredient si cantitate
        when(stoc.getIngredient()).thenReturn("apa");
        when(stoc.getCantitate()).thenReturn(200.0);
        stocService.add(stoc);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 100.0)));

        // evaluam cu assert
        assert stocService.areSuficient(reteta);

        // verificam ca mock-ul Stoc a fost interogat corect de service
        verify(stoc, atLeastOnce()).getIngredient();
        verify(stoc, atLeastOnce()).getCantitate();
    }

    @Test
    @Order(4)
    void testAreSuficient_returnsFalse_withRealRepo_mockStoc() {
        // simulam stoc insuficient
        when(stoc.getIngredient()).thenReturn("lapte");
        when(stoc.getCantitate()).thenReturn(30.0);
        stocService.add(stoc);

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("lapte", 100.0)));

        // evaluam cu assert
        assert !stocService.areSuficient(reteta);

        // verificam interactiunile cu mock-ul Stoc
        verify(stoc, atLeastOnce()).getCantitate();
    }
}
