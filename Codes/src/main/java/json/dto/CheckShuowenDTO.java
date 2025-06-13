package json.dto;

import lombok.Data;

/***
 * Created by zhengyu.shang on 2025/06/06.
 */
@Data
public class CheckShuowenDTO {

    public CheckShuowenDTO(String definition, String part, String voice, String shape) {
        this.definition = definition;
        this.part = part;
        this.voice = voice;
        this.shape = shape;
    }

    private String definition;
    private String part;
    private String voice;
    private String shape;
}
