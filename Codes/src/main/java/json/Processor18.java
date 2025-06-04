package json;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/***
 * Created by zhengyu.shang on 2024/11/19.   ---用来导出《文始》SQL脚本
 */

public class Processor18 {
    public static void main(String[] args) {
        List<String> contentDocx = getContentDocx("E:\\A书籍\\语言学习\\汉语言\\shuowen\\文始_章太炎 - 副本.docx");
        System.out.println(contentDocx.size());
        writeToSqlFile(contentDocx, "E:\\A书籍\\语言学习\\汉语言\\shuowen\\sql.sql");


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

            int lineNum = 1;// 行号

            String volumeNum = "";// 1级标题
            String volumeNum2 = "";// 2级标题
            String radical = "";
            int curPara = 1;// 当前 初文 段落行

            String chuwen = "";//初文、准初文

            if (paragraphs != null && paragraphs.size() > 0) {
                for (XWPFParagraph paragraph : paragraphs) {

                    boolean isContinue = false;
                    String flag = "";
                    String part = "";
                    String wordReal = "";
                    String shape = "";
                    String word = "";
                    String wordSon = "";
                    String wordGrandSon = "";
                    String pin_yin = "";
                    String definition = "";
                    String note = "";


                    int isXsWord = 0; // 是否为形声字
                    String paragraphText = paragraph.getParagraphText();
                    if (paragraphText.length() == 0) {
                        continue;
                    } else if (paragraphText.matches("文始[一二三四五六七八九].*")) {
                        volumeNum = paragraphText;
                        continue;
                    } else if (paragraphText.matches("[陰陽]聲.部[甲乙丙丁]")){
                        volumeNum2 = paragraphText;
                        continue;
                    } else {

                        //word = new String(Character.toChars(paragraphText.codePointAt(0)));
                        //pin_yin = extractPinyinWithTone(paragraphText);
                        // definition = paragraphText.substring(paragraphText.indexOf(pin_yin) + pin_yin.length());
                        //if (paragraphText.contains("（")) {
                        //    flag = paragraphText.substring(paragraphText.lastIndexOf("（") + 1, paragraphText.lastIndexOf("）"));
                        //}

                        List<String> words = new ArrayList<>();
                        List<String> wordReals = new ArrayList<>();
                        List<String> wordSons = new ArrayList<>();// 孳乳字
                        List<String> wordGrandSons = new ArrayList<>();// 外部孳乳字
                        List<String> flagList = new ArrayList<>();
                        List<String> definitions = new ArrayList<>();//说解
                        String curWord = "";// 当前字头，用来便于添加{}
                        String curNote = "";// 当前小号字体，用来便于添加<>
                        List<String> notes = new ArrayList<>();//注释





                        // Set<String> flags = new HashSet<>();

//
                        // 如果当前段落没有红色、绿色字体，表示不需要入库
                        int isRedOrGreen = 0;

                        // if (paragraphText.startsWith("[")) {
                        //     flags.add("1");
                        // }
                        // 当前段落的循环
                        String beforeColor = "";
                        int beforeFontSize = 0;
                        int noteNum = 0;
                        int num = 0;




                        for (XWPFRun run : paragraph.getRuns()) {
                            String color = run.getColor();//获取文本颜色
                            int fontSize = run.getFontSize();

                            boolean hasImage = !run.getEmbeddedPictures().isEmpty();  // 检查是否有图片
                            if (hasImage) {
                                definitions.add("[图片]");
                            }

                            for (String ss : run.text().codePoints()
                                    .mapToObj(cp -> new String(Character.toChars(cp)))  // 将 codepoint 转换为字符
                                    .collect(Collectors.toList())) {
                                if (Objects.equals(color, "FF0000")) {
                                    if (Objects.equals(15, fontSize)) {
                                        chuwen = ss;
                                        curPara = 1;
                                    } else if (Objects.equals(color, beforeColor)) {
                                        wordSons.add(ss);
                                    } else {
                                        num++;
                                        wordSons.add(";" + ss);
                                    }

                                    isRedOrGreen = 1; //有红色字体，则导入
                                }
                                if (Objects.equals(color, "00B050")) {
                                    if (Objects.equals(color, beforeColor)) {
                                        wordGrandSons.add(ss);
                                    } else {
                                        flagList.add((num) + "");
                                        wordGrandSons.add(";" + ss);
                                    }
                                    isRedOrGreen = 1; //有绿色字体，则导入

                                }


                                if (Objects.equals(color, "008000")) {
                                    if (Objects.equals(color, beforeColor)) {
                                        wordReals.add(ss);
                                    } else {
                                        flagList.add((num) + "");
                                        wordReals.add(";" + ss);
                                    }

                                }
                                /*if (Objects.equals(color, "00B0F0")) {
                                    if (Objects.equals(color, beforeColor)) {
                                        notes.add(ss);
                                    } else {
                                        noteNum++;
                                        definitions.add(noteNum + "");
                                        notes.add("\n[音義-" + noteNum + "]：" + ss);
                                    }
                                } else if (Objects.equals(color, "000000") || (color == null) || (color == "auto")) {
                                    if (Objects.equals(color, beforeColor)) {
                                        notes.add(ss);
                                    } else {
                                        noteNum++;
                                        definitions.add(noteNum + "");
                                        notes.add("\n[顏注-" + noteNum + "]：" + ss);
                                    }
                                } else {
                                    definitions.add(ss);
                                }*/
                                // 当前段落文本，为了便于识别，对于颜色标记的字体，需要用{}区分出来，同时对于
                                // 小号字体，用<>标识出来
                                if (Objects.equals("FF0000", color) || Objects.equals("00B050", color)) {
                                    curWord += ss;
                                } else if (Objects.equals(8, fontSize)) {
                                    if (!curWord.isEmpty() && !Objects.equals(fontSize, beforeFontSize)) {
                                        definitions.add("{" + curWord + "}");
                                        curWord = "";
                                    }
                                    curNote += (curWord.isEmpty() ? "" : "{" + curWord + "}") + ss;
                                    curWord = "";
                                } else {
                                    if (Objects.equals(8, beforeFontSize)) {
                                        if (curWord.length() != 0) {
                                            curNote += "{" + curWord + "}";
                                            curWord = "";
                                        } else {
                                            definitions.add("<" + curNote + ">" + ss);
                                            curNote = "";
                                        }
                                    } else {
                                        if (curNote.length() != 0) {
                                            String tmp = ss;
                                            if (curWord.length() != 0) {
                                                tmp = "{" + curWord + "}" + tmp;
                                                curWord = "";
                                            }
                                            definitions.add("<" + curNote + ">" + tmp);
                                            curNote = "";
                                        } else if (curWord.length() != 0) {
                                            definitions.add("{" + curWord + "}" + ss);
                                            curWord = "";
                                        } else {
                                            definitions.add(ss);
                                        }
                                    }
                                }

                                //curStr = ss;
                                beforeColor = color;
                                beforeFontSize = fontSize;
                            }

                        }


                        if (isRedOrGreen == 0) {
                            isContinue = true;
                        } else {
                            if (!wordSons.isEmpty()) {
                                wordSon = String.join("", wordSons).substring(1);//去掉第一个分号
                            }
                            if (!wordGrandSons.isEmpty()) {
                                wordGrandSon = String.join("", wordGrandSons).substring(1);//去掉第一个分号
                            }
                            definition = String.join("", definitions);
                            // note = String.join("", notes).trim();
                            // flag = String.join(";", flagList);
                        }
                        // definition = paragraphText.substring(word.length() + (flags.contains("1") ? 2 : 0));

//                        for (String wordVoice : wordVoices) {
//
//                            if (definition.substring(definition.indexOf(wordVoice)).contains("]")) {
//                                flags.add("2");
//                            }
//                        }
                    }
                    if (!isContinue) {
                        String curSql = "INSERT INTO `shuowen`.`t_han_taiyi` (`id`, `word`, `word_son`, `word_grand_son`, `definition`, " +
                                "`word_fk`, `word_son_index`, `word_grand_son_index`, `wang_extend`, `huang_extend`, " +
                                "`lu_extend`, `yang_extend`, `luo_wang_extend`, `shang_extend`, `word_son_extend_index`, " +
                                "`word_grand_son_extend_index`, `volume_1`, `volume_2`, `paragraph`) VALUES " +
                                "(%d, '%s', '%s', '%s', '%s', null, null, null, null, null, null, null, null, null, null, null, '%s', '%s', %d);";
                        curSql = String.format(curSql, lineNum++, chuwen, wordSon, wordGrandSon, definition,
                                volumeNum, volumeNum2, curPara++);
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
