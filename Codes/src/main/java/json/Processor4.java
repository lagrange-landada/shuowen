package json;

import cn.hutool.core.io.FileUtil;
import json.custom.WordCusWriter;
import json.dto.TextStyle;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
public class Processor4 {
    public static void main(String[] args) throws IOException {
        File fileDirectory = new File("E:/桌面/test");
        String exportPath = "E:/桌面/test.docx";
        File[] files = fileDirectory.listFiles();
        List<File> list = Arrays.stream(files).sorted((o1, o2) -> {
            String o1Name = o1.getName().substring(0, o1.getName().length() - 5);
            String o2Name = o2.getName().substring(0, o2.getName().length() - 5);
            return Integer.parseInt(o1Name) - Integer.parseInt(o2Name);
        }).collect(Collectors.toList());
        List<Document> documentList = new ArrayList<>();
        for (File file : files) {
            String content = FileUtils.readFileToString(file, "UTF-8");
            Document doc = Jsoup.parse(content);
            documentList.add(doc);
        }
        exportMSWord2(documentList, exportPath);
    }

    private static void exportMSWord2(List<Document> documentList, String exportPath) {
        WordCusWriter writer = new WordCusWriter();
        StringBuilder sb = new StringBuilder();
        //预置字体
        Font bushouFont = new Font("宋体-方正超大字符集", Font.BOLD, 14);//部头字体
        Font zwFont = new Font("宋体-方正超大字符集", Font.BOLD, 12);//正文字体
        Font textFont = new Font("宋体-方正超大字符集", Font.PLAIN, 12);//段注字体
        String regex1 = "^(隱公|桓公|莊公|閔公|僖公|文公|宣公|成公|襄公|昭公|定公|哀公).*經$";
        String regex2 = "^(隱公|桓公|莊公|閔公|僖公|文公|宣公|成公|襄公|昭公|定公|哀公).*傳$";
        String regex3 = "周孔子經|周左丘明傳|晋杜預注";
        boolean isTitle1 = false;
        boolean isTitle2 = false;
        String tmp = "";
        for (Document doc : documentList) {
            for (Element spanElement : doc.select("span")) {
                String className = spanElement.className();
                String style = spanElement.attr("style");
                String jingStyle = "font-weight: bold; font-size: 120%;";
                String zhuanStyle = "FONT-SIZE: 120%";
                String noteStyle = "font-size: 95%;color:#A22F01";
                String text = spanElement.text();
                if (text.matches(regex3)) {
                    continue;
                }
                if (className.equals("mw-headline")) {
                    if (text.length() == 2) {
                        TextStyle level1Title = new TextStyle(bushouFont, text, "FF0000", "标题 1", 1);
                        writer.addTextMul(level1Title);
                        isTitle1 = true;
                        isTitle2 = false;
                    }else {
                        /*if (!isTitle2 && !tmp.equals(text.substring(2, text.length() - 1))) {
                            TextStyle level2Title = new TextStyle(bushouFont, text.substring(2, text.length() - 1), "FF0000", "标题 2", 2);
                            writer.addTextMul(level2Title);
                            tmp = text.substring(2, text.length() - 1);
                            isTitle2 = true;
                            isTitle1 = false;
                        }*/
                        if (text.matches(regex1)) {
                            TextStyle level2Title = new TextStyle(bushouFont, text.substring(2, text.length() - 1), "FF0000", "标题 2", 2);
                            writer.addTextMul(level2Title);
                            TextStyle jing = new TextStyle(zwFont, "经", "FF0000", "标题 3", 3);
                            writer.addTextMul(jing);

                        }
                        if (text.matches(regex2)) {
                            TextStyle level2Title = new TextStyle(zwFont, "傳", "FF0000", "标题 3", 3);
                            writer.addTextMul(level2Title);
                        }
                    }


                }

                if (style.equals(jingStyle)) {
                    TextStyle zwStyle = new TextStyle(zwFont, text.replaceAll("\\u3000", ""), "660000");
                    writer.addTextMul(zwStyle);
                }
                if (style.equals(zhuanStyle)) {
                    TextStyle zwStyle = new TextStyle(zwFont, text.replaceAll("\\u3000", ""), "660000");
                    writer.addTextMul(zwStyle);
                }
                if (style.equals(noteStyle)) {
                    TextStyle zhuanText = new TextStyle(textFont, "注", true);
                    TextStyle spaceText = new TextStyle(textFont, " ");
                    TextStyle textStyle = new TextStyle(textFont, text.replace("【注】", "").trim());
                    writer.addTextMul(zhuanText, spaceText, textStyle);
                }

            }
        }


        // 写出到文件
        writer.flush(FileUtil.file(exportPath));
        // 关闭
        writer.close();
    }
}
