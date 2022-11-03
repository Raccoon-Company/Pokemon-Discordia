package game.model.moveEffets;

import game.model.ActionCombat;
import game.model.Combat;
import game.model.Duelliste;
import game.model.Pokemon;
import game.model.enums.AlterationEtat;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeCombatResultat;
import game.model.enums.TypeDuelliste;

public abstract class MoveForceSwitch {
    public static void utiliser(Combat combat, ActionCombat actionCombat) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            case 18://cyclone

                if(actionCombat.getPokemonCible().hasStatut(AlterationEtat.RACINES)){ // || ability suction cups
                    combat.fail();
                    return;
                }

                attaqueParDefaut(combat,actionCombat);
                break;
            case 46: //hurlement
                if(actionCombat.getPokemonCible().hasStatut(AlterationEtat.RACINES)){ // || ability suction cups et soundproof
                    combat.fail();
                    return;
                }

                attaqueParDefaut(combat,actionCombat);
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat) {
        if (combat.getNoir().getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE)) {
            //fuite
            if (actionCombat.getLanceur().getLevel() < actionCombat.getPokemonCible().getLevel()) {
                combat.fail();
            } else {
                if (combat.getDuellisteAllie(actionCombat.getLanceur()).getTypeDuelliste().equals(TypeDuelliste.JOUEUR)) {
                    combat.setTypeCombatResultat(TypeCombatResultat.FUITE_JOUEUR); //TODO implémenter la fuite pendant le combat
                } else {
                    combat.setTypeCombatResultat(TypeCombatResultat.FUITE_ADVERSAIRE);
                }
            }
        } else {
            Duelliste adversaire = combat.getDuellisteAllie(actionCombat.getPokemonCible());
            Pokemon remplacant = adversaire.getEquipe().stream().filter(a -> a.estEnVie() && !a.equals(actionCombat.getPokemonCible())).findAny().orElse(null);
            if (remplacant != null) {
                combat.changerPokemonActif(adversaire, actionCombat.getPokemonCible() ,remplacant);
            } else {
                combat.fail();
            }
        }
    }
}
