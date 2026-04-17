package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;

import java.util.List;
import java.util.Map;

public class StocService {

    private final Repository<Integer, Stoc> stocRepo;

    public StocService(Repository<Integer, Stoc> stocRepo) {
        this.stocRepo = stocRepo;
    }

    public List<Stoc> getAll() {
        return stocRepo.findAll();
    }

    public void add(Stoc s) {
        stocRepo.save(s);
    }

    public void update(Stoc s) {
        stocRepo.update(s);
    }

    public void delete(int id) {
        stocRepo.delete(id);
    }

    public boolean areSuficient(Reteta reteta) {
        if (reteta == null) {                                           // D1
            throw new IllegalArgumentException("Reteta nu poate fi null");
        }
        List<IngredientReteta> ingredienteNecesare = reteta.getIngrediente();
        if (ingredienteNecesare == null) {                              // D2
            return true;
        }
        if (ingredienteNecesare.isEmpty()) {                            // D3
            return true;
        }

        for (IngredientReteta e : ingredienteNecesare) {                // D4
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            double disponibil = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .mapToDouble(Stoc::getCantitate)
                    .sum();

            if (necesar <= 0) {                                         // D5
                continue;
            }
            if (disponibil < necesar) {                                 // D6
                return false;
            }
        }
        return true;
    }

    public void consuma(Reteta reteta) {
        if (!areSuficient(reteta)) {
            throw new IllegalStateException("Stoc insuficient pentru rețeta.");
        }

        for (IngredientReteta e : reteta.getIngrediente()) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            List<Stoc> ingredienteStoc = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .toList();

            double ramas = necesar;

            for (Stoc s : ingredienteStoc) {
                if (ramas <= 0) break;

                double deScazut = Math.min(s.getCantitate(), ramas);
                s.setCantitate((int)(s.getCantitate() - deScazut));
                ramas -= deScazut;

                stocRepo.update(s);
            }
        }
    }
}