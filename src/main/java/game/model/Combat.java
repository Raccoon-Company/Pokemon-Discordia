package game.model;

import game.Game;
import game.model.enums.Meteo;
import game.model.enums.TypeCombat;
import game.model.enums.TypeCombatResultat;
import utils.Utils;

public class Combat {

    private Game game;

    private Duelliste blanc;
    private Duelliste noir;

    //suivi de l'état

    private Terrain terrainBlanc;
    private Terrain terrainNoir;

    private Pokemon currentPokemonBlanc;
    private Pokemon currentPokemonNoir;

    private Meteo meteo;

    //règles
    private TypeCombat typeCombat;

    private boolean objetsAutorises;

    //compteurs
    private int tentativesDeFuite = 0;
    private int piecesEparpillees = 0;
    private int compteurToursMeteo = 999;

    public Combat(Game game, Duelliste blanc, Duelliste noir, TypeCombat typeCombat, Meteo meteo, boolean objetsAutorises) {
        this.game = game;
        this.typeCombat = typeCombat;
        this.blanc = blanc;
        this.noir = noir;
        this.meteo = meteo;
        this.objetsAutorises = objetsAutorises;

        this.currentPokemonBlanc = blanc.getEquipe().get(0);
        this.currentPokemonNoir = noir.getEquipe().get(0);
    }

    public CombatResultat resolve() {
        CombatResultat combatResultat = new CombatResultat();

        combatResultat.setTypeCombatResultat(TypeCombatResultat.VICTOIRE);
        return combatResultat;
    }

    public void changerMeteo(Meteo meteo) {
        changerMeteo(meteo, Utils.getRandom().nextInt(4) + 2);
    }

    public void changerMeteo(Meteo meteo, int compteurToursMeteo) {
        this.meteo = meteo;
        this.compteurToursMeteo = compteurToursMeteo;
    }
}
