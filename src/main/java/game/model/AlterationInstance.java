package game.model;

import game.model.enums.AlterationEtat;

public class AlterationInstance {
    private AlterationEtat alterationEtat;
    private int toursRestants;

    public AlterationInstance(AlterationEtat alterationEtat, int toursRestants) {
        this.alterationEtat = alterationEtat;
        this.toursRestants = toursRestants;
    }

    public AlterationEtat getAlterationEtat() {
        return alterationEtat;
    }

    public void setAlterationEtat(AlterationEtat alterationEtat) {
        this.alterationEtat = alterationEtat;
    }

    public int getToursRestants() {
        return toursRestants;
    }

    public void setToursRestants(int toursRestants) {
        this.toursRestants = toursRestants;
    }
}
