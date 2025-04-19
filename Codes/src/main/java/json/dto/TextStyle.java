package json.dto;

import lombok.Data;

import java.awt.*;
import java.util.Objects;

/***
 * Created by zhengyu.shang on 2021/11/23.
 */
@Data
public class TextStyle {
    public TextStyle() {
    }

    public TextStyle(Font font, String text, String color, String titleIdentification, int titleLevel) {
        this.font = font;
        this.text = text;
        this.color = color;
        this.titleIdentification = titleIdentification;
        this.titleLevel = titleLevel;
    }

    public TextStyle(Font font, String text, String color) {
        this.font = font;
        this.text = text;
        this.color = color;
    }

    public TextStyle(Font font, String text) {
        this.font = font;
        this.text = text;
    }

    public TextStyle(Font font, String text, boolean isShade) {
        this.font = font;
        this.text = text;
        this.isShade = isShade;
    }

    private Font font;

    private String text;

    private String color;

    private String titleIdentification;

    private int titleLevel;

    private boolean isShade;

    private boolean isHighColor;//默认黄色

}
