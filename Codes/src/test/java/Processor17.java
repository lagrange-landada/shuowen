import cn.hutool.core.io.FileUtil;
import json.custom.WordCusWriter;
import json.dto.TextStyle;
import json.dto.Words;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Created by zhengyu.shang on 2025/02/07.
 */
public class Processor17 {
    public static void main(String[] args) {
        List<TextStyle> test1List = readWord("E:\\桌面\\新建文件夹 (2)\\广雅test3_1.docx");
        // 对word1进行处理，筛选出蓝色的
        List<Map<String, String>> voiceList = new ArrayList<>();
        String curWord = "";
        String curVoice = "";
        for (int i = 0; i < test1List.size(); i++) {
            TextStyle textStyle = test1List.get(i);
            if (Objects.equals(textStyle.getColor(), "660000")) {
                switch (textStyle.getText()) {
                    case "〕":
                    case "）":
                    case "，":
                    case "、":
                    case "。":
                    case "\n":
                        continue;
                }
                if (curWord != "" && curVoice != "") {
                    Map<String, String> map = new HashMap<>();
                    map.put("word", curWord);
                    map.put("voice", curVoice);
                    voiceList.add(map);
                }
                curWord = textStyle.getText();
                curVoice = "";
            }
            if (Objects.equals(textStyle.getColor(), "00B0F0")) {
                curVoice += textStyle.getText();
            }
        }
        List<TextStyle> test2List = readWord("E:\\桌面\\新建文件夹 (2)\\广雅test3_2.docx");
        int vernier = 0;
        Font vocieFont = new Font("宋体-方正超大字符集", Font.PLAIN, 12);

        for (int i = 0; i < voiceList.size(); i++) {
            Map<String, String> voiceMap = voiceList.get(i);
            for (int j = vernier; j < test2List.size(); j++) {
                TextStyle textStyle = test2List.get(j);
                if (Objects.equals(textStyle.getColor(), null)) {
                    if (Objects.equals(voiceMap.get("word"), textStyle.getText())) {
                        TextStyle voiceTestStyle = new TextStyle(vocieFont, voiceMap.get("voice"), "00B0F0");
                        test2List.add(Math.min(j + 1, test2List.size()), voiceTestStyle);
                        vernier = j - vernier < 100 ? j : vernier;
                        break;
                    }
                }
            }
        }
        // 写入
        writeWord(test2List, "E:\\桌面\\新建文件夹 (2)\\广雅test3_result.docx");
    }

    // 读取word
    private static List<TextStyle> readWord(String sourcePath) {
        List<String> indexList = new ArrayList<>();
        List<TextStyle> wordsList = new ArrayList<>();
        String result = "0";  // 0表示获取正常，1表示获取异常
        InputStream is = null;
        try {
            is = new FileInputStream(new File(sourcePath));
            // 2007版本的word
            XWPFDocument xwpf = new XWPFDocument(is);    // 2007版本，仅支持docx文件处理
            List<XWPFParagraph> paragraphs = xwpf.getParagraphs();

            int lineNum = 1;// 行号

            String volumeNum = "";
            String radical = "";


            if (paragraphs != null && paragraphs.size() > 0) {

                for (XWPFParagraph paragraph : paragraphs) {
                    String titleIdentification = paragraph.getText();
                    int titleLevel = "正文".equals(titleIdentification) ? 0 : 1;
                    String lastColor = "";
                    Font lastFont = null;
                    for (XWPFRun run : paragraph.getRuns()) {
                        String color = run.getColor();//获取文本颜色
                        Font font = new Font(run.getFontName(), run.isBold() ? Font.BOLD : Font.PLAIN, 12);

                        for (String ss : run.text().codePoints()
                                .mapToObj(cp -> new String(Character.toChars(cp)))  // 将 codepoint 转换为字符
                                .collect(Collectors.toList())) {
                            TextStyle textStyle = new TextStyle(font, ss, color);
                            wordsList.add(textStyle);
                        }
                        lastColor = color;
                        lastFont = font;
                    }
                    TextStyle endParagraphStyle = new TextStyle(lastFont, "\n", lastColor, titleIdentification, titleLevel);
                    wordsList.add(endParagraphStyle);
                    /*boolean isContinue = false;
                    String flag = "";
                    String part = "";
                    String voice = "";
                    String shape = "";
                    String word = "";
                    String pin_yin = "";
                    String definition = "";
                    int isXsWord = 0; // 是否为形声字
                    String paragraphText = paragraph.getParagraphText();
                    if (paragraphText.length() == 0) {
                        continue;
                    } else if (paragraphText.matches("[釋|附韋昭].*")) {
                        volumeNum = paragraphText;
                        continue;
                    } else {

                        //word = new String(Character.toChars(paragraphText.codePointAt(0)));
                        //pin_yin = extractPinyinWithTone(paragraphText);
                        // definition = paragraphText.substring(paragraphText.indexOf(pin_yin) + pin_yin.length());
                        //if (paragraphText.contains("（")) {
                        //    flag = paragraphText.substring(paragraphText.lastIndexOf("（") + 1, paragraphText.lastIndexOf("）"));
                        //}

                        List<String> words = new ArrayList<>();
                        List<String> wordVoices = new ArrayList<>();
                        //List<String> definitions = new ArrayList<>();

                        Set<String> flags = new HashSet<>();


                        // 如果当前段落没有红色字体，表示不需要入库
                        int isRed = 0;

                        if (paragraphText.startsWith("[")) {
                            flags.add("1");
                        }

                        for (XWPFRun run : paragraph.getRuns()) {
                            String color = run.getColor();//获取文本颜色
                            String curStr = "";

                            for (String ss : run.text().codePoints()
                                    .mapToObj(cp -> new String(Character.toChars(cp)))  // 将 codepoint 转换为字符
                                    .collect(Collectors.toList())) {
                                if (Objects.equals(color, "FF0000")) {
                                    words.add(ss);
                                    isRed = 1;
                                }
                                if (Objects.equals(color, "4C90CA")) {
                                    wordVoices.add(ss);
                                }
                                curStr = ss;
                            }
                        }
                        if (isRed == 0) {
                            isContinue = true;
                        }

                        word = String.join("", words);
                        voice = String.join("", wordVoices);
                        definition = paragraphText.substring(word.length() + (flags.contains("1") ? 2 : 0));

                        for (String wordVoice : wordVoices) {

                            if (definition.substring(definition.indexOf(wordVoice)).contains("]")) {
                                flags.add("2");
                            }
                        }
                        flag = String.join(";", flags);



                    }
                    if (!isContinue) {
                        String curSql = "INSERT INTO `shuowen`.`t_han_etymology` (`id`, `word`, `word_voice`, " +
                                "`definition`, `volume`, `source`, `flag`, `field1`, `field2`) VALUES " +
                                "(%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");";
                        curSql = String.format(curSql, lineNum++, word, voice, definition,
                                volumeNum, "释名", flag, null, null);
                        indexList.add(curSql);
                    }
                    //content.append("    ").append(paragraph.getParagraphText().trim()).append("\r\n");*/

                }


            }
        } catch (Exception e) {


            System.out.println("docx解析正文异常:" + e);
            result = "1"; // 出现异常
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                    System.out.println("" + e);
                }
            }
        }
        return wordsList;
    }

    // 写入word
    private static void writeWord(List<TextStyle> list, String exportPath) {
        WordCusWriter writer = new WordCusWriter();


        ArrayList<TextStyle> tmpList = new ArrayList<>();
        for (TextStyle textStyle : list) {
            // 需要换行符则另起一段，否则就是当前行写入
            if ("\n".equals(textStyle.getText())) {
                writer.addTextMul(tmpList.toArray(new TextStyle[0]));
                tmpList.clear();
            } else {
                tmpList.add(textStyle);
            }
        }


        // 写出到文件
        writer.flush(FileUtil.file(exportPath));
        // 关闭
        writer.close();

    }
}


