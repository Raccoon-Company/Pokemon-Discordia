package game.model.moveEffets;

import game.Game;
import game.model.ActionCombat;
import game.model.Attaque;
import game.model.Combat;
import game.model.Pokemon;
import game.model.enums.TypeActionCombat;

public final class MoveUnique {
    public static void utiliser(Combat combat, ActionCombat actionCombat) {
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
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }
}
