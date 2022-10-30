package game.model.enums.moveEffets;

import game.Game;
import game.model.ActionCombat;
import game.model.Combat;
import game.model.Duelliste;
import game.model.Pokemon;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeCombat;
import utils.Utils;

import javax.swing.text.Utilities;
import java.util.ArrayList;
import java.util.List;

public class MoveDamage {
    public static void utiliser(Combat combat, ActionCombat actionCombat){
        if(!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)){
            return;
        }

        switch(actionCombat.getAttaque().getIdMoveAPI()){
            case 1://pound
            case 33://charge
                attaqueParDefaut(combat, actionCombat);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque "+actionCombat.getNomAttaque()+" n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat) {
        List<Pokemon> cibles = new ArrayList<>();
        Duelliste allie = combat.getDuellisteAllie(actionCombat.getLanceur());
        Duelliste adverse = combat.getDuellisteAdverse(actionCombat.getLanceur());
        switch (actionCombat.getTypeCibleCombat()) {
            case ALL_OTHER_POKEMON:
                cibles.add(adverse.getPokemonActif());
                cibles.add(adverse.getPokemonActifBis());
                cibles.add(allie.getPokemonActif());
                cibles.add(allie.getPokemonActifBis());
                cibles.removeIf(c -> !c.estEnVie() || c.getId() == actionCombat.getLanceur().getId());
                break;
            case SELECTED_POKEMON:
            case RANDOM_OPPONENT:
                cibles.add(actionCombat.getPokemonCible());
                break;
            case ALL_OPPONENTS:
                cibles.add(adverse.getPokemonActif());
                cibles.add(adverse.getPokemonActifBis());
                break;
        }

        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat, c));
    }

}
