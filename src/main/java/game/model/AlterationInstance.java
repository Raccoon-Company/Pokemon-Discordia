package game.model;

import game.model.enums.AlterationEtat;

public class AlterationInstance {
    private AlterationEtat alterationEtat;

    private SourceDegats sourceAlteration;
    private int toursRestants;

    public AlterationInstance(AlterationEtat alterationEtat, SourceDegats sourceAlteration,int toursRestants) {
        this.alterationEtat = alterationEtat;
        this.sourceAlteration = sourceAlteration;
        this.toursRestants = toursRestants;
    }

    public SourceDegats getSourceAlteration() {
        return sourceAlteration;
    }

    public void setSourceAlteration(SourceDegats sourceAlteration) {
        this.sourceAlteration = sourceAlteration;
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
