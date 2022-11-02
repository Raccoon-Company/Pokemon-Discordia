package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import game.model.enums.Meteo;
import game.model.enums.StatutsTerrain;
import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;

public class Terrain implements Serializable {

    //liste des status du terrain
    /**
     * le num correspond au nb de tours avant que l'effet se dissipe, ou au nombre d'occurence du statut sinon
     */
    private HashMap<StatutsTerrain, Integer> alterations;

    public Terrain() {
        alterations = new HashMap<>();
    }

    public void ajoutStatut(StatutsTerrain statut, int num) {
        if (statut.isTemporaire() || !alterations.containsKey(statut)) {
            alterations.put(statut, num);
            //si non temporaire mais déjà présente, on inc le nb d'ocurrence
        } else {
            alterations.merge(statut, 1, Integer::sum);
        }
    }

    public void finDeTourMajStatus() {
        //on décrémente le compteur de tours de chaque statut tmeporaire
        alterations.forEach((k, v) -> {
            if (k.isTemporaire()) {
                alterations.replace(k, v - 1);
            }
        });
        //puis on élimine tous les statuts temporaires dont le compte est tombé à 0
        alterations.entrySet().removeIf(entry -> {
            if (entry.getKey().isTemporaire()) {
                return entry.getValue() <= 0;
            } else {
                return false;
            }
        });
    }

    @JsonIgnore
    public Terrain getCopy() {
        return (Terrain) SerializationUtils.clone(this);
    }

    public boolean hasStatut(StatutsTerrain statutsTerrain){
        return alterations.containsKey(statutsTerrain);
    }

    public void supprimerStatut(StatutsTerrain statutsTerrain){
        alterations.remove(statutsTerrain);
    }

    public HashMap<StatutsTerrain, Integer> getAlterations() {
        return alterations;
    }

    public void setAlterations(HashMap<StatutsTerrain, Integer> alterations) {
        this.alterations = alterations;
    }
    public Meteo getMeteo(){
        return alterations.entrySet().stream().filter(e -> e.getKey().isMeteo() && e.getValue() > 0).findAny().map(a -> StatutsTerrain.getMeteoFromStatut(a.getKey())).orElse(Meteo.NEUTRE);
    }
}
