package game.model.moveEffets;

import game.model.*;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeSourceDegats;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MoveDamage {
    public static void utiliser(Combat combat, ActionCombat actionCombat) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            //jackpot
            case 6 :
                //TODO doublé si tenu piece rune ou encens veine, capacité etrennes
                combat.setPiecesEparpillees(combat.getPiecesEparpillees() + (actionCombat.getLanceur().getLevel()*5));
                attaqueParDefaut(combat, actionCombat);
                break;
            case 3://torgnoles
            case 4: //poing comète
                //TODO clone, riposte, patience
                int degats = actionCombat.getPokemonCible().calculerDegatsAttaque(actionCombat, combat);
                int ran = Utils.getRandomNumber(1, 8);
                int n;
                if (ran <= 3) {
                    n = 2;
                } else if (ran <= 6) {
                    n = 3;
                } else if (ran == 7) {
                    n = 4;
                } else {
                    n = 5;
                }
                for(int i =0;i<=n;i++){
                    actionCombat.getPokemonCible().blesser(degats, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
                }
                break;
            case 1://pound
            case 2://karaate chop
            case 5: //ultimapoing
            case 10: //griffe
            case 33://charge
                attaqueParDefaut(combat, actionCombat);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat) {
        List<Pokemon> cibles = ciblesAffectees(combat, actionCombat);

        for (Pokemon cible : cibles) {
            int degats = cible.calculerDegatsAttaque(actionCombat,combat);
            cible.blesser(degats, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
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
                throw new IllegalStateException("Cible inconnue : MoveDamage");
        }

        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat, c));
        return cibles;
    }


}
