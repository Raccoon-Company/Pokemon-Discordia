package game.model.moveEffets;

import game.model.*;
import game.model.enums.AlterationEtat;
import game.model.enums.StatutsTerrain;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeSourceDegats;
import org.jetbrains.annotations.NotNull;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MoveDamage {

    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            //jackpot
            case 6:
                //TODO doublé si tenu piece rune ou encens veine, capacité etrennes
                combat.setPiecesEparpillees(combat.getPiecesEparpillees() + (actionCombat.getLanceur().getLevel() * 5));
                attaqueParDefaut(combat, actionCombat, simulation);
                break;
            case 3://torgnoles
            case 4: //poing comète
            case 31://fury attack
                //TODO check interactions : focus sash focus band sturdyweak armor stamina rocky helmet
                if(combat.verificationsCibleIndividuelle(actionCombat, actionCombat.getPokemonCible(),false)) {
                    //TODO clone, riposte, patience
                    int degats = actionCombat.getPokemonCible().calculerDegatsAttaque(actionCombat, combat, 1);
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
                    for (int i = 0; i <= n; i++) {
                        actionCombat.getPokemonCible().blesser(degats, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
                    }
                }
                break;
            case 24://double pied
                if(combat.verificationsCibleIndividuelle(actionCombat, actionCombat.getPokemonCible(),false)){
                    int degatsDoublePied = actionCombat.getPokemonCible().calculerDegatsAttaque(actionCombat, combat, 1);
                    actionCombat.getPokemonCible().blesser(degatsDoublePied, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
                    actionCombat.getPokemonCible().blesser(degatsDoublePied, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
                }
                break;
            case 16: //tornade
                //TODO doubler les degats si cible bounce/fly/skydrop
                attaqueParDefaut(combat, actionCombat, simulation, 1, false);
                break;
            case 19://vol
                /**
                 * "Inflicts regular damage.
                 * User flies high into the air for one turn, becoming immune to attack, and hits on the second turn.
                 * During the immune turn, gust, hurricane, sky uppercut, smack down, thunder, twister, and whirlwind still hit the user normally.
                 * gust and twister also have double power against the user.
                 * The damage from hail and sandstorm still applies during the immune turn.
                 * The user may be hit during its immune turn if under the effect of lock on, mind reader, or no guard.
                 * This move cannot be used while gravity is in effect.
                 * This move cannot be selected by sleep talk."
                 */
                if (combat.getTerrainBlanc().hasStatut(StatutsTerrain.GRAVITY)) {
                    combat.fail();
                    return;
                }
                if (actionCombat.getLanceur().hasStatut(AlterationEtat.SEMI_INVULNERABLE)) { // TODO ou si power herb est tenu
                    //attaque
                    attaqueParDefaut(combat, actionCombat, simulation, 1, false);
                    actionCombat.getLanceur().enleveStatut(AlterationEtat.SEMI_INVULNERABLE);
                } else {
                    //s'envole
                    combat.getGame().getChannel().sendMessage(actionCombat.getLanceur().getNomPresentation() + " s'envole !").queue();
                    actionCombat.getLanceur().applyStatus(AlterationEtat.SEMI_INVULNERABLE, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()), 2, simulation);
                    actionCombat.getLanceur().getActionsCombat().put(combat.getTurnCount() + 1, new ActionCombat(actionCombat));
                }
                break;
            case 23://stomp
                //puissance doublée si la cible a utilisé minimize
                if (actionCombat.getPokemonCible().getActionsCombat().values().stream().anyMatch(a -> a.getAttaque().getIdMoveAPI() == 107)) {
                    attaqueParDefaut(combat, actionCombat, simulation, 2, true);
                } else {
                    attaqueParDefaut(combat, actionCombat, simulation, 1, false);
                }

                break;
            case 26://pied sauté
                if (combat.getTerrainBlanc().hasStatut(StatutsTerrain.GRAVITY)) {
                    combat.fail();
                    return;
                }
                if(combat.verificationsCibleIndividuelle(actionCombat, actionCombat.getPokemonCible(), false)){
                    int degatsPiedSaute = actionCombat.getPokemonCible().calculerDegatsAttaque(actionCombat, combat, 1);
                    actionCombat.getPokemonCible().blesser(degatsPiedSaute, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
                }else{
                    combat.getGame().getChannel().sendMessage(actionCombat.getLanceur().getNomPresentation()+ " rate son attaque et se blesse !").queue();
                    actionCombat.getLanceur().blesser(actionCombat.getLanceur().getMaxHp() / 2, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()));
                }
                break;
            case 1://pound
            case 2://karate chop
            case 5: //ultimapoing
            case 10: //griffe
            case 11: //vice grip
            case 13://razor wind
            case 15://coupe
            case 17://cru-ailes
            case 21://souplesse
            case 22://fouet lianes
            case 25://ultimawashi
            case 27: //rolling kick
            case 29://coupdboule
            case 30: //koudkorne
            case 33://charge
                attaqueParDefaut(combat, actionCombat, simulation);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation) {
        attaqueParDefaut(combat, actionCombat, simulation, 1, false);
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat, boolean simulation, double modificateurPuissance, boolean alwaysHit) {
        List<Pokemon> cibles = ciblesAffectees(combat, actionCombat);

        for (Pokemon cible : cibles) {
            if (Utils.getRandomNumber(1, 100) < actionCombat.getAttaque().getMoveAPI().getMetaData().getFlinchChance()) {
                cible.applyStatus(AlterationEtat.APEURE, new SourceDegats(TypeSourceDegats.POKEMON, actionCombat.getLanceur()), 1, simulation);
            }

            int degats = cible.calculerDegatsAttaque(actionCombat, combat, modificateurPuissance);
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

        cibles.removeIf(c -> combat.verificationsCibleIndividuelle(actionCombat, c,false));
        return cibles;
    }


}
