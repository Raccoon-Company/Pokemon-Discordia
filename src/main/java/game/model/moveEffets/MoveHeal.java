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

            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Game game, ActionCombat actionCombat) {
        switch (actionCombat.getTypeCibleCombat()) {
            case SPECIFIC_MOVE:
                break;
            case SELECTED_POKEMON_ME_FIRST:
                break;
            case ALLY:
                break;
            case USERS_FIELD:
                break;
            case USER_OR_ALLY:
                break;
            case OPPONENTS_FIELD:
                break;
            case USER:
                break;
            case RANDOM_OPPONENT:
                break;
            case ALL_OTHER_POKEMON:
                break;
            case SELECTED_POKEMON:
                break;
            case ALL_OPPONENTS:
                break;
            case ENTIRE_FIELD:
                break;
            case USER_AND_ALLIES:
                break;
            case ALL_POKEMON:
                break;
            case ALL_ALLIES:
                break;
        }
    }
}
