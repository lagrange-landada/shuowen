package json;

import com.sun.org.slf4j.internal.LoggerFactory;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/***
 * Created by zhengyu.shang on 2024/11/19.   ---用来导出说文段注本文本 的 SQL脚本
 */

public class Processor8 {
    public static void main(String[] args) {
        List<String> contentDocx = getContentDocx("E:\\桌面\\新建文件夹 (2)\\句 部.docx");
        System.out.println(contentDocx.size());
        writeToSqlFile(contentDocx, "E:\\桌面\\新建文件夹 (2)\\sql.sql");

    }


    /**
     * 获取正文文件内容，docx方法
     *
     * @param path
     * @return
     */
    public static List<String> getContentDocx(String path) {
        List<String> sqlList = new ArrayList<>();
        String result = "0";  // 0表示获取正常，1表示获取异常
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
            // 2007版本的word
            XWPFDocument xwpf = new XWPFDocument(is);    // 2007版本，仅支持docx文件处理
            List<XWPFParagraph> paragraphs = xwpf.getParagraphs();

            int lineNum = 2916;// 行号

            String volumeNum = "";
            String radical = "";



            if (paragraphs != null && paragraphs.size() > 0) {
                for (XWPFParagraph paragraph : paragraphs) {

                    boolean isContinue = false;
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
                    } else if (paragraphText.matches("第[一二三四五六七八九十]{1,}卷")) {
                        volumeNum = paragraphText;
                        continue;
                    } else if (paragraphText.matches(". 部")) {
                        radical = paragraphText.replace(" ", "");
                        continue;
                    } else if (paragraphText.matches("文[一二三四五六七八九十]{1,}.*")) {
                        continue;
                    } else {

                        word = new String(Character.toChars(paragraphText.codePointAt(0)));
                        pin_yin = extractPinyinWithTone(paragraphText);
                        definition = paragraphText.substring(paragraphText.indexOf(pin_yin) + pin_yin.length());
                        if (paragraphText.contains("（")) {
                            flag = paragraphText.substring(paragraphText.lastIndexOf("（") + 1, paragraphText.lastIndexOf("）"));
                        }

                        List<String> parts = new ArrayList<>();
                        List<String> shapes = new ArrayList<>();
                        List<String> voices = new ArrayList<>();


                        for (XWPFRun run : paragraph.getRuns()) {
                            String color = run.getColor();//获取文本颜色

                            for (String ss : run.text().codePoints()
                                    .mapToObj(cp -> new String(Character.toChars(cp)))  // 将 codepoint 转换为字符
                                    .collect(Collectors.toList())) {
                                if (Objects.equals(color, "70AD47")) {
                                    shapes.add(ss);
                                    parts.add(ss);
                                }
                                if (Objects.equals(color, "4C90CA")) {
                                    voices.add(ss);
                                    parts.add(ss);
                                    isXsWord = 1;
                                }
                            }

                            if (Objects.equals(color, "7030A0")) {
                                isContinue = true;
                            }
                            // if (run.getStyle().equals("标题 2")) {
                            //     isContinue = true;
                            // }
                        }

                        part = String.join(";", parts);
                        voice = String.join(";", voices);
                        shape = String.join(";", shapes);
                    }
                    if (!isContinue) {
                        String curSql = "INSERT INTO `shuowen`.`shuowen_voice_revel` (`id`, `word`, `pin_yin`, `volume`, `radical`, " +
                                "`definition`, `part`, `voice`, `intter_voice`, `shape`, `same_source`, `little_same_source`, " +
                                "`duan_17`, `wang_30`, `flag`, `is_xs_word`, `intter_shape`, `field1`, `field2`) " +
                                "VALUES (%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", " +
                                "%d, \"%s\", \"%s\", \"%s\");";
                        curSql = String.format(curSql, lineNum++, word, pin_yin, volumeNum,
                                radical, definition.replace("（" + flag + "）", ""), part, voice, null, shape, null, null, null, null, flag, isXsWord
                                , null, null, null);
                        sqlList.add(curSql);
                    }
                    //content.append("    ").append(paragraph.getParagraphText().trim()).append("\r\n");
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
        return sqlList;
    }

    /**
     * 获取当前行拼音
     *
     * @param input
     * @return
     */
    public static String extractPinyinWithTone(String input) {
        String result = "";
        String regex = "[a-zɡ]{0,}[āáǎàēéěèīíǐìōóǒòūúǔùüǖǚǜ]{0,}";
        int start = 0;
        int end = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.substring(i, i + 1).matches(regex)) {
                start = start != 0 ? start : i;
            } else if (start != 0 && end == 0 && (!input.substring(i, i + 1).matches(regex) && input.charAt(i) != ' ')) {
                end = i;
            }
        }
        return input.substring(start, end).trim();
    }


    public static void writeToSqlFile(List<String> contentDocx, String filePath) {
        BufferedWriter writer = null;
        try {
            // 创建一个BufferedWriter对象，目标文件是sql.sql
            writer = new BufferedWriter(new FileWriter(new File(filePath)));

            // 遍历 List 中的每一项，逐行写入到 sql.sql 文件
            for (String line : contentDocx) {
                writer.write(line);
                writer.newLine();  // 每条内容后加上换行符
            }

            System.out.println("内容成功写入到 " + filePath);

        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        } finally {
            // 关闭资源
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("关闭 BufferedWriter 时出错: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 获取正文文件内容，doc方法
     *
     * @param path
     * @return
     */
    public Map<String, String> getContentDoc(String path) {
        Map<String, String> map = new HashMap();
        StringBuffer content = new StringBuffer("");
        String result = "0";  // 0表示获取正常，1表示获取异常
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
            // 2003版本的word
            WordExtractor extractor = new WordExtractor(is);  // 2003版本 仅doc格式文件可处理，docx文件不可处理
            String[] paragraphText = extractor.getParagraphText();   // 获取段落，段落缩进无法获取，可以在前添加空格填充
            if (paragraphText != null && paragraphText.length > 0) {
                for (String paragraph : paragraphText) {
                    if (!paragraph.startsWith("    ")) {
                        content.append("    ").append(paragraph.trim()).append("\r\n");
                    } else {
                        content.append(paragraph);
                    }
                }
            }
        } catch (Exception e) {

            System.out.println("doc解析正文异常:" + e);
            result = "1"; // 出现异常
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                    System.out.println("" + e);
                }
            }
            map.put("result", result);
            map.put("content", content.toString());
        }
        return map;
    }

    /**
     * 获取正文文件内容，wps方法
     *
     * @param path
     * @return
     */
    public Map<String, String> getContentWps(String path) {
        Map<String, String> map = new HashMap();
        StringBuffer content = new StringBuffer("");
        String result = "0";  // 0表示获取正常，1表示获取异常
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
            // wps版本word
            HWPFDocument hwpf = new HWPFDocument(is);
            WordExtractor wordExtractor = new WordExtractor(hwpf);
            // 文档文本内容
            String[] paragraphText1 = wordExtractor.getParagraphText();
            if (paragraphText1 != null && paragraphText1.length > 0) {
                for (String paragraph : paragraphText1) {
                    if (!paragraph.startsWith("    ")) {
                        content.append("     ").append(paragraph.trim()).append("\r\n");
                    } else {
                        content.append(paragraph);
                    }
                }
            }
        } catch (Exception e) {

            System.out.println("wps解析正文异常:" + e);
            result = "1"; // 出现异常
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                    System.out.println("" + e);
                }
            }
            map.put("result", result);
            map.put("content", content.toString());
        }
        return map;
    }

}
