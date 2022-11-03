package game.model.moveEffets;

import com.github.oscar0812.pokeapi.models.moves.MoveAilment;
import game.model.*;
import game.model.enums.MoveAilmentAPI;
import game.model.enums.Type;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeSourceDegats;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MoveDamageAilment {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
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
                int id = actionCombat.getPokemonCible().getActionsCombat().get(combat.getTurnCount()).getAttaque().getIdMoveAPI();
                if(id == 291){
                    attaqueParDefaut(combat, actionCombat,simulation, Utils.getRandomNumber(4, 5), 2,false);
                }else{
                    attaqueParDefaut(combat, actionCombat,simulation, Utils.getRandomNumber(4, 5), 1,false);
                }
                break;
            case 20: // bind
            case 35://wrap
            case 83://danse flammes
                //TODO item grip claw + 1 tour
                if(!actionCombat.getPokemonCible().hasType(Type.GHOST)){
                    combat.getGame().getChannel().sendMessage(actionCombat.getPokemonCible().getNomPresentation() + " est pris dans l'étreinte de "+actionCombat.getLanceur().getNomPresentation()+ " !").queue();
                    attaqueParDefaut(combat, actionCombat,simulation, Utils.getRandomNumber(4, 5),1,false);
                }else{
                    //pas de binding si type fantome
                    if(combat.verificationsCibleIndividuelle(actionCombat, actionCombat.getPokemonCible(),false,false)){
                        int degats = actionCombat.getPokemonCible().calculerDegatsAttaque(actionCombat, combat, simulation, 1);
                        actionCombat.getPokemonCible().blesser(degats, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
                    }
                }
                break;
            case 34://stomp
                //puissance doublée si la cible a utilisé minimize
                if (actionCombat.getPokemonCible().getActionsCombat().values().stream().anyMatch(a -> a.getAttaque().getIdMoveAPI() == 107)) {
                    attaqueParDefaut(combat, actionCombat, simulation, 1,2, true);
                } else {
                    attaqueParDefaut(combat, actionCombat, simulation, 1,1, false);
                }

                break;
            case 41://doubledard
                attaqueParDefaut(combat, actionCombat, simulation, 1,1, false);
                attaqueParDefaut(combat, actionCombat, simulation, 1,1, false);
                break;
            case 7: //fire punch
            case 8://ice punch
            case 9://thunder punch
            case 40: //dard-venin
            case 52://flammeche
            case 53: //lance flammes
            case 58://laser glace
            case 59://blizzard
            case 60://rafale psy
            case 84://éclair
            case 85://tonnerre
            case 87://fatal foudre
            case 93://choc mental
                attaqueParDefaut(combat, actionCombat, simulation);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat,boolean simulation, int dureeAlteration, int modificateurPuissance, boolean alwaysHit) {
        List<Pokemon> cibles = ciblesAffectees(combat, actionCombat, alwaysHit);

        for (Pokemon cible : cibles) {
            int degats = cible.calculerDegatsAttaque(actionCombat, combat, simulation, modificateurPuissance);
            cible.blesser(degats, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
            MoveAilment ma = actionCombat.getAttaque().getMoveAPI().getMeta().getAilment();
            MoveAilmentAPI localMoveAilmentAPI = MoveAilmentAPI.getById(ma.getId());
            if (localMoveAilmentAPI.getAlterationEtat() != null) {
                if (actionCombat.getAttaque().getMoveAPI().getMeta().getAilmentChance() == 0 || actionCombat.getAttaque().getMoveAPI().getMeta().getAilmentChance() >= Utils.getRandomNumber(1, 100)) {
                    cible.applyStatus(localMoveAilmentAPI.getAlterationEtat(), new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()), dureeAlteration, false, combat.getGame());
                }
            } else {
                System.out.println(localMoveAilmentAPI);
            }

        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation) {
        attaqueParDefaut(combat, actionCombat,simulation, 1,1,false);
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
                throw new IllegalStateException("Cible inconnue : MoveDamageAilment");
        }
        cibles = cibles.stream().filter(Objects::nonNull).collect(Collectors.toList());
        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat,c, alwaysHit, false));
        return cibles;
    }
}
