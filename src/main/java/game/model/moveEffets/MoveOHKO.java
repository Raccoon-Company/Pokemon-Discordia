package game.model.moveEffets;

import game.model.ActionCombat;
import game.model.Combat;
import game.model.Pokemon;
import game.model.SourceDegats;
import game.model.enums.TypeActionCombat;
import game.model.enums.TypeSourceDegats;
import utils.Utils;

public class MoveOHKO {
    public static void utiliser(Combat combat, ActionCombat actionCombat, boolean simulation) {
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        switch (actionCombat.getAttaque().getIdMoveAPI()) {
            case 12://guillotine
            case 32://empalkorne
                attaqueParDefaut(combat, actionCombat);
                break;
            default:
                combat.getGame().getChannel().sendMessage("L'attaque " + actionCombat.getNomAttaque() + " n'a pas encore été implémentée. C'est un taf monstrueux et le dev a la flemme. cheh.").queue();
        }
    }

    private static void attaqueParDefaut(Combat combat, ActionCombat actionCombat) {
//Inflicts damage equal to the target's max HP.
// Ignores accuracy and evasion modifiers.
// This move's accuracy is 30% plus 1% for each level the user is higher than the target.
// If the user is a lower level than the target, this move will fail.
// Because this move inflicts a specific and finite amount of damage, endure still prevents the target from fainting.
// The effects of lock on, mind reader, and no guard still apply, as long as the user is equal or higher level than the target.
// However, they will not give this move a chance to break through detect or protect.
        Pokemon lanceur = actionCombat.getLanceur();
        Pokemon cible = actionCombat.getPokemonCible();

        if(combat.verificationsCibleIndividuelle(actionCombat, cible, true, false)){
            if (lanceur.getLevel() < cible.getLevel()) {
                combat.fail();
            } else {

                int accuracy = 30 + lanceur.getLevel() - cible.getLevel();
                //TODO lockon mind reader no guard

                if (Utils.getRandomNumber(1, 100) <= accuracy) {
                    cible.blesser(cible.getCurrentHp(), new SourceDegats(TypeSourceDegats.POKEMON, lanceur));
                    combat.getGame().getChannel().sendMessage("K.O. d'un seul coup !").queue();
                } else {
                    combat.fail();
                }
            }
        }
    }
}
