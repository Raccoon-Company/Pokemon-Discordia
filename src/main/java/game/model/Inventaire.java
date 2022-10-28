package game.model;

import game.model.enums.Item;
import game.model.enums.ItemCategorie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Inventaire {

    private Logger logger = LoggerFactory.getLogger(Inventaire.class);

    //<idItemApi, nb>
    private HashMap<Item, Integer> items;

    public Inventaire() {
        items = new HashMap<>();
    }

    public HashMap<Item, Integer> getItems() {
        return items;
    }

    public void setItems(HashMap<Item, Integer> items) {
        this.items = items;
    }

    public void ajoutItem(Item item, int num) {
        if (num <= 0) {
            logger.error("Ajout négatif (" + num + ") de l'item " + item.name() + " !");
            return;
        }
        items.merge(item, num, Integer::sum);
    }

    public void retraitItem(Item item, int num) {
        if (num > 0) {
            logger.error("Ajout positif (" + num + ") de l'item " + item.name() + " !");
            return;
        }
        if (num > items.get(item)) {
            logger.error("Pas assez de l'item " + item.name() + " pour en retirer " + num + " !");
            return;
        }
        items.merge(item, -num, Integer::sum);
    }

    public boolean has(Item item) {
        return items.containsKey(item) && items.get(item) > 0;
    }

    public boolean hasPokeballs() {
        return items.entrySet().stream().anyMatch((k) -> k.getKey().getCategorie().equals(ItemCategorie.STANDARD_BALLS) && k.getValue() > 0);
    }
}
