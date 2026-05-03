package drinkshop.ut.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/// Unit testing pentru clasa S folosind JUnit si Mockito
/// avem evaluare cu assert si cu verify
/// total 4 teste implementate
/// Scenariu 3: S->R->E
/// Step 1: Unit testing
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceMockitoTest {

    private Stoc stoc;
    private Repository<Integer, Stoc> stocRepo;
    private StocService stocService;

    @BeforeEach
    public void setUp() {
        // cream obiecte mock pentru cele doua clase dependente: Repository (R) si Stoc (E)
        stoc = mock(Stoc.class);
        stocRepo = mock(Repository.class);
        stocService = new StocService(stocRepo);
    }

    @AfterEach
    public void tearDown() {
        stocService = null;
        stocRepo = null;
        stoc = null;
    }

    @Test
    @Order(1)
    public void testGetAllValid() {
        // cream obiecte mock suplimentare
        Stoc stoc1 = mock(Stoc.class);
        Stoc stoc2 = mock(Stoc.class);

        // asociem comportamente obiectelor mock
        when(stocRepo.findAll()).thenReturn(Arrays.asList(stoc1, stoc2));

        // testam metoda getAll si evaluam cu assert
        assert 2 == stocService.getAll().size();

        // verificam interactiunile cu obiectele mock
        verify(stocRepo, times(1)).findAll();
        verify(stocRepo, never()).save(any());
    }

    @Test
    @Order(2)
    void testAddValid() {
        // asociem comportament obiectului mock Repository
        when(stocRepo.save(stoc)).thenReturn(stoc);

        // apelam metoda add si evaluam cu fail
        try {
            stocService.add(stoc);
        } catch (Exception e) {
            fail("Add should not throw an exception: " + e.getMessage());
        }

        // verificam interactiunile cu obiectele mock
        verify(stocRepo, times(1)).save(stoc);
        verify(stocRepo, never()).delete(any());
    }

    @Test
    @Order(3)
    void testAreSuficientReturnsTrueWhenStocSuficient() {
        // simulam un stoc mock cu ingredient "apa" si cantitate 100.0
        when(stoc.getIngredient()).thenReturn("apa");
        when(stoc.getCantitate()).thenReturn(100.0);
        when(stocRepo.findAll()).thenReturn(Collections.singletonList(stoc));

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("apa", 50.0)));

        // evaluam rezultatul cu assert
        assert stocService.areSuficient(reteta);

        // verificam interactiunile cu obiectele mock
        verify(stocRepo, times(1)).findAll();
        verify(stoc, atLeastOnce()).getIngredient();
        verify(stoc, atLeastOnce()).getCantitate();
    }

    @Test
    @Order(4)
    void testAreSuficientReturnsFalseWhenStocInsuficient() {
        // simulam un stoc mock cu cantitate insuficienta
        when(stoc.getIngredient()).thenReturn("zahar");
        when(stoc.getCantitate()).thenReturn(10.0);
        when(stocRepo.findAll()).thenReturn(Collections.singletonList(stoc));

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("zahar", 100.0)));

        // evaluam rezultatul cu assert
        assert !stocService.areSuficient(reteta);

        // verificam interactiunile cu obiectele mock
        verify(stocRepo, times(1)).findAll();
        verify(stoc, atLeastOnce()).getCantitate();
    }
}
