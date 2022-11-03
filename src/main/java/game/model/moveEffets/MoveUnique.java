package game.model.moveEffets;

import game.Game;
import game.model.*;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeCombatResultat;
import game.model.enums.TypeDuelliste;

public final class MoveUnique {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {

            case 50://disable
                Pokemon cible = actionCombat.getPokemonCible();
                ActionCombat actionCible;
                Attaque attaque;
                if(cible.isaDejaAttaque()){
                    actionCible = actionCombat.getPokemonCible().getActionsCombat().get(combat.getTurnCount());
                }else if(combat.getTurnCount() > 0){
                    actionCible = actionCombat.getPokemonCible().getActionsCombat().get(combat.getTurnCount() - 1);
                }else{
                    combat.fail();
                    return;
                }
                if(actionCible.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)){
                    attaque = actionCible.getAttaque();
                    if(attaque.getIdMoveAPI() != 165){
                        combat.
                    }else{
                        combat.fail();
                        return;
                    }
                }else{
                    combat.fail();
                    return;
                }
                break;
            case 100://téléport

                if (combat.getNoir().getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE) && combat.fuiteAutorisee(actionCombat.getLanceur(), combat.getDuellisteAdverse(actionCombat.getLanceur()), simulation)) {
                    //fuite
                        if (combat.getDuellisteAllie(actionCombat.getLanceur()).getTypeDuelliste().equals(TypeDuelliste.JOUEUR)) {
                            combat.setTypeCombatResultat(TypeCombatResultat.FUITE_JOUEUR); //TODO implémenter la fuite pendant le combat
                        } else {
                            combat.setTypeCombatResultat(TypeCombatResultat.FUITE_ADVERSAIRE);
                        }

                } else {
                        combat.fail();
                }

                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }
}
