package game.model.moveEffets;

import game.model.*;
import game.model.enums.AlterationEtat;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeSourceDegats;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MoveDamageHeal {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            case 71://vole vie
            case 72://mega sagnsue
                attaqueParDefaut(combat,actionCombat,simulation);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation) {
        attaqueParDefaut(combat, actionCombat, simulation, 1, false);
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation, double modificateurPuissance, boolean alwaysHit) {
        List<Pokemon> cibles = ciblesAffectees(combat, actionCombat, alwaysHit);
        int heal = 0;
        for (Pokemon cible : cibles) {
            if (Utils.getRandomNumber(1, 100) < actionCombat.getAttaque().getMoveAPI().getMeta().getFlinchChance()) {
                cible.applyStatus(AlterationEtat.APEURE, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()), 1, simulation, combat.getGame());
            }

            int degats = cible.calculerDegatsAttaque(actionCombat, combat, simulation, modificateurPuissance);
            heal += degats;
            cible.blesser(degats, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
        }
        actionCombat.getLanceur().soigner(heal, combat.getGame());
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
                throw new IllegalStateException("Cible inconnue : MoveDamageHeal");
        }
        cibles = cibles.stream().filter(Objects::nonNull).collect(Collectors.toList());
        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat, c,alwaysHit, false));
        return cibles;
    }
}
