package json.dto;

import lombok.Data;
import org.json.JSONObject;

import java.util.List;

/***
 * Created by zhengyu.shang on 2025/03/19.
 */
@Data
public class BookLineInfoDTO {
    private List<LineInfo> lines;

    @Data
    public class LineInfo {
        private String lineId;
        private Integer lineNum;
        private Integer lineType; // 1 -正文；2-注释
        private String content;
        private JSONObject objectRef;
        private JSONObject charPic;
        private String figure;
        private JSONObject pagePass;
        private String logicSentenceId;
        private JSONObject pfMarkedInfo;
        private String table;
    }

}
