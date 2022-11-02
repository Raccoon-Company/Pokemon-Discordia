package game.model.moveEffets;

import game.model.*;
import game.model.enums.MoveAilmentAPI;
import game.model.enums.Type;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeSourceDegats;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MoveDamageAilment {
    public static void utiliser(Combat combat, ActionCombat actionCombat) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            case 250: // whirlpool
                if(actionCombat.getPokemonCible().hasType(Type.GHOST)){
                    combat.getGame().getChannel().sendMessage("L'attaque est sans effet !").queue();
                    return;
                }
                //TODO item grip claw + 1 tour
                combat.getGame().getChannel().sendMessage(actionCombat.getPokemonCible().getNomPresentation() + " est pris dans l'étreinte de "+actionCombat.getLanceur().getNomPresentation()+ " !").queue();
                //TODO doubler la puissance si pendant dive
                attaqueParDefaut(combat, actionCombat, Utils.getRandomNumber(4, 5), 1);
                break;
            case 20: // bind
                if(actionCombat.getPokemonCible().hasType(Type.GHOST)){
                    combat.getGame().getChannel().sendMessage("L'attaque est sans effet !").queue();
                    return;
                }
                //TODO item grip claw + 1 tour
                combat.getGame().getChannel().sendMessage(actionCombat.getPokemonCible().getNomPresentation() + " est pris dans l'étreinte de "+actionCombat.getLanceur().getNomPresentation()+ " !").queue();
                attaqueParDefaut(combat, actionCombat, Utils.getRandomNumber(4, 5),1);
                break;
            case 7: //fire punch
            case 8://ice punch
            case 9://thunder punch
                attaqueParDefaut(combat, actionCombat);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, int dureeAlteration, int modificateurPuissance) {
        List<Pokemon> cibles = ciblesAffectees(combat, actionCombat);

        for (Pokemon cible : cibles) {
            int degats = cible.calculerDegatsAttaque(actionCombat, combat, modificateurPuissance);
            cible.blesser(degats, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
            MoveAilment ma = actionCombat.getAttaque().getMoveAPI().getMeta().getAilment();
            MoveAilmentAPI localMoveAilmentAPI = MoveAilmentAPI.getById(ma.getId());
            if (localMoveAilmentAPI.getAlterationEtat() != null) {
                if (actionCombat.getAttaque().getMoveAPI().getMeta().getAilmentChance() >= Utils.getRandomNumber(1, 100)) {
                    cible.applyStatus(localMoveAilmentAPI.getAlterationEtat(), new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()), dureeAlteration, false);
                }
            } else {
                System.out.println(localMoveAilmentAPI);
            }

        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat) {
        attaqueParDefaut(combat, actionCombat, 1,1);
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
                throw new IllegalStateException("Cible inconnue : MoveDamageAilment");
        }

        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat, c, false));
        return cibles;
    }
}
