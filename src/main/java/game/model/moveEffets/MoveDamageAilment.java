package game.model.moveEffets;

import com.github.oscar0812.pokeapi.models.moves.MoveAilment;
import game.model.*;
import game.model.enums.MoveAilmentAPI;
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
            case 7 : //fire punch
            case 8://ice punch
            case 9://thunder punch
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
            MoveAilment ma =  actionCombat.getAttaque().getMoveAPI().getMeta().getAilment();
            MoveAilmentAPI localMoveAilmentAPI = MoveAilmentAPI.getById(ma.getId());
            if(localMoveAilmentAPI.getAlterationEtat() != null){
                if(actionCombat.getAttaque().getMoveAPI().getMeta().getAilmentChance() >= Utils.getRandomNumber(1,100)){
                    cible.applyStatus(localMoveAilmentAPI.getAlterationEtat(), new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()), 1, false);
                }
            }else{
                System.out.println(localMoveAilmentAPI);
            }

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
