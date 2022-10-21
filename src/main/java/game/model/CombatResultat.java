package game.model;

import game.model.enums.TypeCombatResultat;

public class CombatResultat {
    private Combat combat;
    private TypeCombatResultat typeCombatResultat;

    public CombatResultat() {
    }

    public CombatResultat(Combat combat) {
        this.combat = combat;
        this.typeCombatResultat = TypeCombatResultat.EN_COURS;
    }

    public CombatResultat(Combat combat, TypeCombatResultat typeCombatResultat) {
        this.combat = combat;
        this.typeCombatResultat = typeCombatResultat;
    }

    public Combat getCombat() {
        return combat;
    }

    public void setCombat(Combat combat) {
        this.combat = combat;
    }

    public TypeCombatResultat getTypeCombatResultat() {
        return typeCombatResultat;
    }

    public void setTypeCombatResultat(TypeCombatResultat typeCombatResultat) {
        this.typeCombatResultat = typeCombatResultat;
    }
}
