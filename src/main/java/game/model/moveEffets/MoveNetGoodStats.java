package game.model.moveEffets;

import game.model.*;
import game.model.enums.Stats;
import game.model.enums.TypeActionCombat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoveNetGoodStats {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {

            case 14://swords dance
            case 39://mimi queue
            case 43: //groz yeux
            case 45: //rugissement
            case 74://croissance
                attaqueParDefaut(combat, actionCombat, simulation);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation) {
        List<Pokemon> cibles = ciblesAffectees(combat, actionCombat);

        for (Pokemon cible : cibles) {
            actionCombat.getAttaque().getMoveAPI().getStatChanges().forEach(s -> {
                Stats stat = Stats.getById(s.getStat().getId());
                cible.updateStage(combat.getGame().getChannel() , stat, s.getChange(), simulation);
            });
        }
    }
    @NotNull
    public static List<Pokemon> ciblesAffectees(Combat combat, ActionCombat actionCombat) {
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
//            case SPECIFIC_MOVE:
//                break;
//            case SELECTED_POKEMON_ME_FIRST:
//                break;
//            case ALLY:
//                break;
//            case USERS_FIELD:
//                break;
//            case USER_OR_ALLY:
//                break;
//            case OPPONENTS_FIELD:
//                break;
            case USER:
                cibles.add(actionCombat.getLanceur());
                break;
//            case ENTIRE_FIELD:
//                break;
//            case USER_AND_ALLIES:
//                break;
//            case ALL_ALLIES:
//                break;
//            case ALL_POKEMON:
//                break;
            default:
                throw new IllegalStateException("Cible inconnue : MoveDamage");
        }

        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat, c, false, false));
        return cibles;
    }

}
