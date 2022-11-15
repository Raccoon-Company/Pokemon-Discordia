package game.model.moveEffets;

import game.Game;
import game.model.ActionCombat;
import game.model.Combat;
import game.model.Pokemon;
import game.model.enums.TypeActionCombat;

public class MoveWholeFieldEffect {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {

            case 114://buée noire
                combat.getPokemonsActifs().forEach(Pokemon::resetStages);
                if (!simulation) {
                    combat.getGame().getChannel().sendMessage("Tous les changements de stats ont été annulés !");
                }
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Game game, ActionCombat actionCombat) {
//entire field
    }
}
