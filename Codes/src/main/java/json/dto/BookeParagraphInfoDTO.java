package json.dto;

import lombok.Data;

import java.util.List;

/***
 * Created by zhengyu.shang on 2025/03/19. 识典
 */
@Data
public class BookeParagraphInfoDTO {
    private String paragraphId;
    private Integer paragraphType;
    private BookLineInfoDTO content;
    private String startPageId;
    private String endPageId;
    private Integer contentEncryptType;
    private String translateContent;
    private String chapterId; //章节ID

}
