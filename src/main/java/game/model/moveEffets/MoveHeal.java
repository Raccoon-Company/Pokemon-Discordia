package game.model.moveEffets;

import game.Game;
import game.model.ActionCombat;
import game.model.Combat;
import game.model.enums.TypeActionCombat;

public class MoveHeal {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            case 105://recover
                attaqueParDefaut(combat.getGame(), actionCombat);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Game game, ActionCombat actionCombat) {

        int montant = actionCombat.getLanceur().getMaxHp() / 2;

        actionCombat.getLanceur().soigner(montant, game);

    }
}
