package drinkshop.receipt;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ReceiptGenerator {
    public static String generate(Order o, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BON FISCAL =====\n").append("Comanda #").append(o.getId()).append("\n");
        for (OrderItem i : o.getItems()) {
            try {
                Product p = products.stream().filter((p1) -> i.getProduct().getId() == p1.getId()).collect(Collectors.toList()).get(0);
                sb.append(p.getNume()).append(": ").append(p.getPret()).append(" x ").append(i.getQuantity()).append(" = ").append(i.getTotal()).append(" RON\n");
            }
            catch (IndexOutOfBoundsException e){
                throw new RuntimeException("Product from order not found in service!");
            }
        }
        sb.append("---------------------\nTOTAL: ").append(o.getTotalPrice()).append(" RON\n=====================\n");
        return sb.toString();
    }
}