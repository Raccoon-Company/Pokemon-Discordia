package game.model.enums;

public enum NiveauIA {
    BASIQUE(0), //calcul le coup en fonction de l'algo pour être le plus efficace, pas de chgt de pokemons
    RANDOM(0), //choisis ses attaques aléatoirement
    AVANCE(1), //calcule les coups avec l'algo, peut utiliser une guérison et choisis le meilleur pokémon possible quand le précédent est ko
    PRO(2), //calcule les coups avec l'algo, peut utiliser deux guérisons, changer de pokémon et choisis le meilleur pokémon possible quand le précédent est ko
    HUMAN(0),
    ; //les coups sont choisis par un humain

    private final int nbPotionsAutorisees;

    NiveauIA(int nbPotionsAutorisees) {

        this.nbPotionsAutorisees = nbPotionsAutorisees;
    }


    public int getNbPotionsAutorisees() {
        return nbPotionsAutorisees;
    }
}
