package game.model;

import org.w3c.dom.Text;

import java.awt.*;

public class TextUI extends ElementUI {
    private String text;
    private Font font;
    private Color color;

    public TextUI(int x, int y, String text, Font font, Color color) {
        super(x, y);

        this.text = text;
        this.font = font;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
