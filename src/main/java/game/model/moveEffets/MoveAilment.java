package game.model.moveEffets;

import game.model.*;
import game.model.enums.AlterationEtat;
import game.model.enums.MoveAilmentAPI;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeSourceDegats;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MoveAilment {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            case 48://supersonic
                attaqueParDefaut(combat, actionCombat, simulation, AlterationEtat.getToursConfusion(), false);
                break;
            case 47://berceuse
                //TODO sweet veil, throat chop, throat spray, insomnia ,soundproof, vital spirit
            case 79://poudre dodo
            case 95://hypnose
                //TODO insomnia vital spirit sap sipper overcoat sweet veil safety goggles
                attaqueParDefaut(combat, actionCombat, simulation, AlterationEtat.getToursSommeil(), false);
                break;
            case 73://vampigraine
            case 77://poudre toxik
            case 78://paraspore
            case 86://cagéclair
            case 92://toxik
                attaqueParDefaut(combat, actionCombat, simulation, 1, false);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation, int dureeAlteration, boolean alwaysHit) {
        List<Pokemon> cibles = ciblesAffectees(combat, actionCombat, alwaysHit);

        for (Pokemon cible : cibles) {
            MoveAilment ma = actionCombat.getAttaque().getMoveAPI().getMeta().getAilment();
            MoveAilmentAPI localMoveAilmentAPI = MoveAilmentAPI.getById(ma.getId());
            if (localMoveAilmentAPI.getAlterationEtat() != null) {
                if (actionCombat.getAttaque().getMoveAPI().getMeta().getAilmentChance() == 0 || actionCombat.getAttaque().getMoveAPI().getMeta().getAilmentChance() >= Utils.getRandomNumber(1, 100)) {
                    cible.applyStatus(localMoveAilmentAPI.getAlterationEtat(), new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()), dureeAlteration, false);
                }
            } else {
                System.out.println(localMoveAilmentAPI);
            }

        }
    }

    @NotNull
    public static List<Pokemon> ciblesAffectees(Combat combat, ActionCombat actionCombat, boolean alwaysHit) {
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
//            case USER:
//                break;
//            case ENTIRE_FIELD:
//                break;
//            case USER_AND_ALLIES:
//                break;
//            case ALL_ALLIES:
//                break;
//            case ALL_POKEMON:
//                break;
            default:
                throw new IllegalStateException("Cible inconnue : MoveAilment");
        }

        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat, alwaysHit, false));
        return cibles;
    }
}
