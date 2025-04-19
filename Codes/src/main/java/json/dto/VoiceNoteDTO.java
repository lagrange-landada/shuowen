package json.dto;

import lombok.Data;

/***
 * Created by zhengyu.shang on 2025/03/21.
 */
@Data
public class VoiceNoteDTO {
    public VoiceNoteDTO(String text, String voice) {
        this.text = text;
        this.voice = voice;
    }

    private String text;
    private String voice;
}
