package json.dto;

import lombok.Data;

import java.util.List;

/***
 * Created by zhengyu.shang on 2025/03/19.识典
 */
@Data
public class ChapterInfoDTO {
    private String grandTitleId;//一级
    private String grandTitleName;
    private String parentTitleId;//二级
    private String parentTitleName;
    private String subTitleId;//三级
    private String subTitleName;
}
