package game.model.moveEffets;

import game.Game;
import game.model.ActionCombat;
import game.model.Combat;
import game.model.Terrain;
import game.model.enums.StatutsTerrain;
import game.model.enums.TypeActionCombat;

public class MoveFieldEffect {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            case 54://brume
                actionCombat.getTerrainCible().ajoutStatut(StatutsTerrain.MIST, 5);
                if(!simulation){
                    combat.getGame().getChannel().sendMessage("Les pokémons de "+ combat.getDuellisteAllie(actionCombat.getLanceur()).getNom()+ " sont protégés par la brume !").queue();
                }
                break;
            case 113://mur lumlière
                actionCombat.getTerrainCible().ajoutStatut(StatutsTerrain.LIGHT_SCREEN, 5);//light clay item
                if(!simulation){
                    combat.getGame().getChannel().sendMessage("Mur Lumière augmente la défense spéciale de vos pokémons !").queue();
                }
                break;
            case 115://reflect
                actionCombat.getTerrainCible().ajoutStatut(StatutsTerrain.REFLECT, 5);//light clay item
                if(!simulation){
                    combat.getGame().getChannel().sendMessage("Mur Lumière augmente la défense de vos pokémons !").queue();
                }
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation) {
        switch (actionCombat.getTypeCibleCombat()) {
//            case SPECIFIC_MOVE:
//                break;
//            case SELECTED_POKEMON_ME_FIRST:
//                break;
//            case ALLY:
//                break;
            case USERS_FIELD:
                break;
//            case USER_OR_ALLY:
//                break;
            case OPPONENTS_FIELD:
                break;
//            case USER:
//                break;
//            case RANDOM_OPPONENT:
//                break;
//            case ALL_OTHER_POKEMON:
//                break;
//            case SELECTED_POKEMON:
//                break;
//            case ALL_OPPONENTS:
//                break;
            case ENTIRE_FIELD:
                break;
//            case USER_AND_ALLIES:
//                break;
//            case ALL_POKEMON:
//                break;
//            case ALL_ALLIES:
//                break;
        }
    }
}
