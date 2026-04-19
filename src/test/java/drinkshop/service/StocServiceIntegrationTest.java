package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration Tests - Top-Down (Scenariul 3: S -> R -> E)")
class StocServiceIntegrationTest {

    private StocService stocService;
    private Repository<Integer, Stoc> repositoryReal;

    @BeforeEach
    void setUp() {
        // Pentru testarea de integrare S -> R, NU mai folosim Mockito.
        // Avem nevoie de un obiect Repository 100% functional.
        // Daca ai deja un fisier repository (ex: StocFileRepository), poti face new cu el aici.
        // Daca nu, folosim aceasta implementare reala in memorie:
        repositoryReal = new Repository<>() {
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

        // Asamblăm sistemul: Service-ul primeste Repository-ul real
        stocService = new StocService(repositoryReal);
    }

    @AfterEach
    void tearDown() {
        stocService = null;
        repositoryReal = null;
    }

    // =========================================================================
    // STEP 2 (pentru Scenariul 3): Integrare S + R cu E
    // (Conform PDF, clasa de test pentru Step 2/3/4 trebuie să aibă minim 2 teste)
    // =========================================================================

    @Test
    @DisplayName("Integrare S+R+E: Adaugarea unui Stoc (E) prin Service (S) ajunge cu succes in Repo (R)")
    void testIntegration_AdaugareSiPreluare() {
        // Cream entitatea (E)
        Stoc stocNou = new Stoc(1, "menta", 50.0, 0.0);

        // Apelam metoda din Service (S) care trebuie sa comunice cu Repo (R)
        stocService.add(stocNou);

        // Verificam preluarea
        List<Stoc> stocuri = stocService.getAll();

        // Assert-uri pentru a confirma succesul integrarii
        assertEquals(1, stocuri.size(), "Repository-ul real ar trebui sa contina o entitate");
        assertEquals("menta", stocuri.get(0).getIngredient(), "Datele entitatii au fost corupte pe traseu");
    }

    @Test
    @DisplayName("Integrare S+R+E: Modificarea stocului prin metoda consuma() se reflecta in Repo")
    void testIntegration_ConsumaModificaRepoReal() {
        // Populam baza de date (R) cu entitati (E)
        Stoc stocApa1 = new Stoc(1, "apa", 100.0, 0.0);
        Stoc stocApa2 = new Stoc(2, "apa", 50.0, 0.0);
        stocService.add(stocApa1);
        stocService.add(stocApa2);

        // Cream un necesar
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 120.0)));

        // Rulam logica de business din Service (S)
        stocService.consuma(reteta);

        // Verificam efectele in Repository-ul real (R)
        // Stoc1 ar trebui sa fie 0, Stoc2 ar trebui sa fie 30
        Stoc stoc1Actualizat = repositoryReal.findOne(1);
        Stoc stoc2Actualizat = repositoryReal.findOne(2);

        assertEquals(0.0, stoc1Actualizat.getCantitate(), "Primul stoc nu a fost epuizat complet");
        assertEquals(30.0, stoc2Actualizat.getCantitate(), "Al doilea stoc nu a fost scazut corect");
    }
}