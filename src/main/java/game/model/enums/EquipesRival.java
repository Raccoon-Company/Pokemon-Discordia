package game.model.enums;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

public enum EquipesRival {
    RIVAL_0_1(0,1,Arrays.asList(new AbstractMap.SimpleEntry<>(4, 5),new AbstractMap.SimpleEntry<>(16, 3))),
    RIVAL_0_4(0,4,Arrays.asList(new AbstractMap.SimpleEntry<>(7, 5),new AbstractMap.SimpleEntry<>(16, 3))),
    RIVAL_0_7(0,7,Arrays.asList(new AbstractMap.SimpleEntry<>(1, 5),new AbstractMap.SimpleEntry<>(16, 3))),

    RIVAL_9_1(9,1,Arrays.asList(new AbstractMap.SimpleEntry<>(4, 9),new AbstractMap.SimpleEntry<>(16, 9))),
    RIVAL_9_4(9,4,Arrays.asList(new AbstractMap.SimpleEntry<>(7, 9),new AbstractMap.SimpleEntry<>(16, 9))),
    RIVAL_9_7(9,7,Arrays.asList(new AbstractMap.SimpleEntry<>(1, 9),new AbstractMap.SimpleEntry<>(16, 9))),

            ;


    private final int progress;
    private final int idStarter;
    private final List<AbstractMap.SimpleEntry<Integer, Integer>> equipe;

    EquipesRival(int progress, int idStarter, List<AbstractMap.SimpleEntry<Integer, Integer>> equipe) {
        this.progress = progress;
        this.idStarter = idStarter;
        this.equipe = equipe;
    }

    public List<AbstractMap.SimpleEntry<Integer, Integer>> getEquipe() {
        return equipe;
    }

    public int getProgress() {
        return progress;
    }

    public int getIdStarter() {
        return idStarter;
    }

    public static EquipesRival obtenir(int progress, int idStarter){
        return Arrays.stream(values()).filter(a -> a.getIdStarter() == idStarter && a.getProgress() == progress).findAny().orElse(null);
    }
}
