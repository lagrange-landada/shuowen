package json;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import json.custom.WordCusWriter;
import json.dto.TextStyle;
import json.dto.Words;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author szy
 * @date 2023/8/6 22:28
 */
public class Processor2 {
    public static void main(String[] args) throws IOException {
        File file = new File("C:/Users/shangzhengyu/Desktop/测试.txt");
        String exportPath = "C:/Users/shangzhengyu/Desktop/test.docx";
        String content = FileUtils.readFileToString(file, "UTF-8");
        exportMSWord2(content, exportPath);
    }

    private static void exportMSWord2(String content, String exportPath) {
        WordCusWriter writer = new WordCusWriter();
        StringBuilder sb = new StringBuilder();
        //预置字体
        Font zwFont = new Font("宋体-方正超大字符集", Font.BOLD, 12);//正文字体
        Font textFont = new Font("宋体-方正超大字符集", Font.PLAIN, 12);//段注字体

        boolean isUse = true;//是否有用
        for (int i = 0; i < content.length(); i++) {
            if (isUse) {
                sb.append(content.charAt(i));
            }
            if (content.charAt(i) == '〈') {
                TextStyle zwTextStyle = new TextStyle(zwFont, sb.toString().replace("〈", ""), "660000");
                writer.addTextMul(zwTextStyle);
                sb.setLength(0);
                isUse = true;
            }
            if (content.charAt(i) == '〉' || content.charAt(i) == '○' || content.charAt(i) == '【') {
                String replace = sb.toString().replace("〉", "")
                        .replace("○", "")
                        .replace("【", "")
                        .replace("　", "")
                        .trim();
                if (!replace.isEmpty()) {
                    TextStyle zhuanText = new TextStyle(textFont, "傳", true);
                    TextStyle spaceText = new TextStyle(textFont, " ");
                    TextStyle textStyle = new TextStyle(textFont, replace);
                    writer.addTextMul(zhuanText, spaceText, textStyle);
                }
                sb.setLength(0);
                if (content.charAt(i) == '○') {
                    isUse = false;
                }
                if (content.charAt(i) == '〉') {
                    isUse = true;
                }
                if (content.charAt(i) == '【') {
                    isUse = false;
                }
            }
            if (content.charAt(i) == '\n') {
                sb.setLength(0);
                isUse = true;
            }
        }

        // 写出到文件
        writer.flush(FileUtil.file(exportPath));
        // 关闭
        writer.close();

    }
}
