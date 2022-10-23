package game.model.enums;

import java.awt.*;
import java.util.Arrays;

//2 : genderless, 0 : female, 1 : male
public enum Gender {
    MALE(1,"♂", Color.BLUE),
    FEMALE(0,"♀", Color.PINK),
    NEUTRAL(2,"", Color.BLACK);

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
