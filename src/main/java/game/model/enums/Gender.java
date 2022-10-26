package game.model.enums;

import java.awt.*;
import java.util.Arrays;

public enum Gender {
    MALE(2,"♂", Color.BLUE),
    FEMALE(1,"♀", Color.PINK),
    NEUTRAL(3,"", Color.BLACK);

    private final String emoji;
    private final int idGender;
    private Color color;

    Gender(int idGender, String emoji, Color color) {
        this.emoji = emoji;
        this.idGender = idGender;
        this.color = color;
    }

    public static Gender getById(int i) {
        return Arrays.stream(values()).filter(v -> v.getIdGender() == i).findAny().orElse(Gender.NEUTRAL);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getIdGender() {
        return idGender;
    }
}
