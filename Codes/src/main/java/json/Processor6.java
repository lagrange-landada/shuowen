package json;


import cn.hutool.core.io.FileUtil;
import json.custom.WordCusWriter;
import json.dto.TextStyle;
import org.apache.commons.io.FileUtils;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/***
 * Created by zhengyu.shang on 2023/08/20.
 */
public class Processor6 {
    public static void main(String[] args) throws IOException {
        File fileDirectory = new File("E:/桌面/新建文件夹 (2)");
        String exportPath = "E:/桌面/卷29-35.docx";
        File[] files = fileDirectory.listFiles();
        List<File> list = Arrays.stream(files).sorted((o1, o2) -> {
            String o1Name = o1.getName().substring(0, o1.getName().length() - 5);
            String o2Name = o2.getName().substring(0, o2.getName().length() - 5);
            return Integer.parseInt(o1Name) - Integer.parseInt(o2Name);
        }).collect(Collectors.toList());
        List<String> documentList = new ArrayList<>();
        for (File file : files) {
            String content = FileUtils.readFileToString(file, "UTF-8");
            String s = content.replaceAll("^\\s*\\n", "");
            documentList.add(s);
        }
        exportMSWord2(documentList, exportPath);
    }

    private static void exportMSWord2(List<String> contentList, String exportPath) {
        WordCusWriter writer = new WordCusWriter();
        StringBuilder sb = new StringBuilder();
        //预置字体
        Font bushouFont = new Font("宋体-方正超大字符集", Font.BOLD, 14);//部头字体
        Font zwFont = new Font("宋体-方正超大字符集", Font.BOLD, 12);//正文字体
        Font textFont = new Font("宋体-方正超大字符集", Font.PLAIN, 12);//段注字体
        String regex1 = "(隱公|桓公|莊公|閔公|僖公|文公|宣公|成公|襄公|昭公|定公|哀公).*年";
        String regex2 = "^(隱公|桓公|莊公|閔公|僖公|文公|宣公|成公|襄公|昭公|定公|哀公).*\n$";
        String regex3 = "周孔子經|周左丘明傳|晋杜預注";
        boolean isTitle1 = false;
        boolean isTitle2 = false;
        String tmp = "";
        boolean isUse = true;
        for (String content : contentList) {
            for (int i = 0; i < content.length(); i++) {
                if (isUse) {
                    sb.append(content.charAt(i));
                }
                if (sb.toString().matches(regex1)) {
                    TextStyle title1Style = new TextStyle(bushouFont, sb.substring(2).trim(), "FF0000", "标题 2", 2);
                    writer.addTextMul(title1Style);
                    sb.setLength(0);
                    isUse = true;
                }
                if (content.charAt(i) == '【') {
                    if (content.charAt(i + 1) == '經') {
                        TextStyle level2Title = new TextStyle(bushouFont, "經", "FF0000", "标题 3", 3);
                        writer.addTextMul(level2Title);
                    }
                    if (content.charAt(i + 1) == '傳') {
                        TextStyle level2Title = new TextStyle(bushouFont, "傳", "FF0000", "标题 3", 3);
                        writer.addTextMul(level2Title);
                    }
                }
                if (content.charAt(i) == '〈') {
                    TextStyle zwStyle = new TextStyle(zwFont, sb.toString().replace("【經】", "")
                            .replace("【傳】", "")
                            .replace("〈", "")
                            .replace("\n", "")
                            .trim(), "660000");
                    writer.addTextMul(zwStyle);
                    sb.setLength(0);

                }
                if (content.charAt(i) == '〉' || content.charAt(i) == '○') {
                    TextStyle noteStyle = new TextStyle(textFont, sb.toString().replace("（", "")
                            .replace("）", "")
                            .replace(")", "")
                            .replace("(", "")
                            .replace("○", "")
                            .replace("〉", ""));
                    TextStyle zhuanText = new TextStyle(textFont, "注", true);
                    TextStyle spaceText = new TextStyle(textFont, " ");

                    if (!noteStyle.getText().isEmpty()) {
                        writer.addTextMul(zhuanText, spaceText, noteStyle);
                    }
                    sb.setLength(0);
                }

                if (content.charAt(i) == '疏') {
                    isUse = false;
                    sb.setLength(0);
                }
                if (content.charAt(i) == '\n') {
                    isUse = true;
                    sb.setLength(0);
                }
                if (content.charAt(i) == '〉') {
                    isUse = true;
                }
                if (content.charAt(i) == '○') {
                    isUse = false;
                }

            }
        }



        // 写出到文件
        writer.flush(FileUtil.file(exportPath));
        // 关闭
        writer.close();
    }
}
