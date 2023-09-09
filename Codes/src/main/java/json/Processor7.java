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
public class Processor7 {
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
        String regex4 = "卷(一|二|三|四|五|六|七|八|九|十|).*\\s.*";
        boolean isTitle1 = false;
        boolean isTitle2 = false;
        String tmp = "";
        boolean isUse = true;
        String title = "";
        for (String content : contentList) {
            String[] split = content.split("\r\n");
            for (String line : split) {
                String text = line.replace("　", "");
                if (line.startsWith("總目 >")) {
                    continue;
                }
                if (line.startsWith("　　○") || line.startsWith("○")) {
                    continue;
                }
                if (line.startsWith("　　[疏]") || line.startsWith("[疏]")) {
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                if (text.matches(regex4)) {
                    continue;
                }
                if (text.startsWith("【經】")) {
                    String year = text.substring(4, text.indexOf("年") + 1);
                    TextStyle title1Style = new TextStyle(bushouFont, year, "FF0000", "标题 2", 2);
                    writer.addTextMul(title1Style);
                    TextStyle subheading = new TextStyle(bushouFont, "【經】" , "FF0000", "标题 3", 3);
                    writer.addTextMul(subheading);
                    TextStyle textStyle = new TextStyle(textFont, text.replace("【經】", ""));
                    writer.addTextMul(textStyle);
                }
                if (text.startsWith("【傳】")) {
                    TextStyle subheading = new TextStyle(bushouFont, "【傳】", "FF0000", "标题 3", 3);
                    writer.addTextMul(subheading);
                    TextStyle textStyle = new TextStyle(textFont, text.replace("【傳】", ""));
                    writer.addTextMul(textStyle);
                }

                

            }
        }



        // 写出到文件
        writer.flush(FileUtil.file(exportPath));
        // 关闭
        writer.close();
    }
}
