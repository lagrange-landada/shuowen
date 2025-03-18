import cn.hutool.core.io.FileUtil;
import json.custom.WordCusWriter;
import json.dto.FangYanDO;
import json.dto.TextStyle;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Created by zhengyu.shang on 2025/01/27.
 */
public class Processor13 {

    public static void main(String[] args) throws IOException, InterruptedException {

        //读取word文件
        // List<String> contentDocx = getContentDocx("E:\\桌面\\新建文件夹 (2)\\广雅.docx");

        //第一种方式
        Connection connect = null;
        String[] contentDocx = {"有", "天", "[释天]", "有"};
        List<String> resultList = new ArrayList<>();
        int i = 0;
        for (String s : contentDocx) {
            //添加参数
            // 进度
            // System.out.println("=============" + ((++i) + 0.00) / contentDocx.size() + "===============");
            if (s.contains("[")) {
                resultList.add(s);
            } else {
                connect = Jsoup.connect("http://www.kaom.net/book_xungu8.php");
                connect.data("word",s)
                        .data("biamti", "yes")
                        .data("page", "no");
                Connection.Response response = connect.method(Connection.Method.POST).ignoreContentType(true).execute();
                //获取数据，转换成HTML格式
                Document document = response.parse();
                // System.out.println(document);
                Elements elements = document.select("a[href*=b=xungu_gysz]");
                // 找到相应的标签，接着是拼接，
                List<String> collect = elements.stream().filter(e -> !resultList.contains(e.text())).
                        map(e -> e.text()).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    resultList.add(collect.get(0));
                }
                Thread.sleep(1500);
            }

        }

        // 写入到新的word中去。
        exportMSWord(resultList, "E:\\桌面\\新建文件夹 (2)\\广雅test1111.docx");




    }


    public static List<String> getContentDocx(String path) {
        List<String> indexList = new ArrayList<>();
        String result = "0";  // 0表示获取正常，1表示获取异常
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
            // 2007版本的word
            XWPFDocument xwpf = new XWPFDocument(is);    // 2007版本，仅支持docx文件处理
            List<XWPFParagraph> paragraphs = xwpf.getParagraphs();

            int lineNum = 1;// 行号

            String volumeNum = "";
            String radical = "";


            if (paragraphs != null && paragraphs.size() > 0) {
                for (XWPFParagraph paragraph : paragraphs) {
                    // 取每一行的、第一个的#660000颜色字体，但是不是“也”字。也不是“□”。更不是标点符号“、。，”
                    // 如果排除完所有，都没有适合的索引，那么直接跳过这一行
                    // 因为有些字是编外码，需要特殊考虑。
                    out:
                    for (XWPFRun run : paragraph.getRuns()) {
                        String color = run.getColor();//获取文本颜色


                        for (String ss : run.text().codePoints()
                                .mapToObj(cp -> new String(Character.toChars(cp)))  // 将 codepoint 转换为字符
                                .collect(Collectors.toList())) {
                            String regex = "[也□、。，\\[]";
                            if (Objects.equals(color, "660000") &&
                                    !ss.matches(regex)) {
                                indexList.add(ss);

                            } else if (Objects.equals(color, "FF0000")) {
                                // 标题
                                indexList.add("[" + ss + "]");
                            }
                        }
                    }
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
        return indexList;
    }


    private static void exportMSWord(List<String> StringList, String exportPath) {
        WordCusWriter writer = new WordCusWriter();
        String juanmu = "";
        String citiao = "";
        //预置字体
        Font bushouFont = new Font("宋体-方正超大字符集", Font.PLAIN, 18);//部头字体
        Font headFont = new Font("宋体-方正超大字符集", Font.BOLD, 18);//字头字体
        Font xiaozhuanFont = new Font("方正字迹-顧建平篆書 繁U", Font.PLAIN, 28);//小篆字体
        Font pinyinFont = new Font("News Gothic MT", Font.PLAIN, 12);//拼音字体
        Font xuFont = new Font("宋体-方正超大字符集", Font.BOLD, 12);//许书释文字体
        Font textFont = new Font("宋体-方正超大字符集", Font.PLAIN, 12);//段注字体


        for (String string : StringList) {
            //ArrayList<TextStyle> textStyles = new ArrayList<>();
            if (string.contains("[")) {
                TextStyle textStyle1 = new TextStyle(headFont, string.replaceAll("[\\[\\]]", ""), "FF0000", "标题 1", 1);
                writer.addTextMul(textStyle1);
            } else {
                TextStyle textStyle = new TextStyle(xuFont, string, "660000");
                writer.addTextMul(textStyle);
            }











            //添加多种字体风格的 完整释文
            //writer.addTextMul(textStyles.toArray(new TextStyle[0]));
        }




        /*for (Words words : wordsList) {
            ArrayList<TextStyle> textStyles = new ArrayList<>();
            //判断要不要添加部首标题
            if (!words.getRadical().equals(radical)) {
                TextStyle bushou = new TextStyle(bushouFont, words.getRadical() + " 部", "FF0000", "标题 1", 1);
                writer.addTextMul(bushou);//部头
            }
            radical = words.getRadical();

            TextStyle header = new TextStyle(headFont, words.getWordhead(), "FF0000");
            textStyles.add(header);//字头

            TextStyle xiaozhuan = new TextStyle(xiaozhuanFont, words.getWordhead());
            textStyles.add(xiaozhuan);//小篆

            TextStyle pinyin = new TextStyle(pinyinFont, words.getPinyin_full(), "FF0000");
            textStyles.add(pinyin);//拼音

            List<DuanNotes> notes = words.getDuan_notes();
            if (notes.size() == 0) {
                TextStyle bodyText1 = new TextStyle(xuFont, words.getExplanation(), "660000");
                textStyles.add(bodyText1);//许书原文
                TextStyle bodyText2 = new TextStyle(textFont, "（新）");
                textStyles.add(bodyText2);//新附字标记
            } else {
                for (DuanNotes note : notes) {
                    TextStyle bodyText = new TextStyle(xuFont, note.getExplanation(), "660000");
                    textStyles.add(bodyText);//许书原文

                    TextStyle noteText = new TextStyle(textFont, note.getNote());
                    textStyles.add(noteText);//段注
                }
            }
            //添加多种字体风格的 完整释文
            writer.addTextMul(textStyles.toArray(new TextStyle[0]));
        }*/
        // 写出到文件
        writer.flush(FileUtil.file(exportPath));
        // 关闭
        writer.close();
    }
}
