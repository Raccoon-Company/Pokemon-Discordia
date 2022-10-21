package game.model.enums;

public enum ZoneTypes {
//    VILLAGE("\uD83C\uDFE1"),
    VILLE("\uD83C\uDFD9"),
    ROUTE("\uD83D\uDEE3");

   private final String emojiCode;

    ZoneTypes(String emojiCode) {
        this.emojiCode = emojiCode;
    }

    public String getEmojiCode() {
        return emojiCode;
    }
}
