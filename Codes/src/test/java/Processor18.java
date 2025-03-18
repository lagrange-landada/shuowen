import config.Config;
import json.dto.TextStyle;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Created by zhengyu.shang on 2025/02/12.
 */
public class Processor18 {
    private static final Config config = new Config();
    private static String shuowenNameTable = "shuowen_voice_revel";

    public static void main(String[] args) {
        // int i = wordNum("E:\\A书籍\\文学\\国内文学\\文选\\wenxuan\\广雅test3_result.docx", 1578);
        // System.out.println(i);
        // readAndWriteGY("E:\\A书籍\\文学\\国内文学\\文选\\wenxuan\\广雅test3_result.docx",
        //         "E:\\A书籍\\文学\\国内文学\\文选\\wenxuan\\广雅_test.txt");
        // String ss = "齋从\uD855\uDF39;禱;省";
        // for (String c : ss.split(";")) {
        //     System.out.println(c);
        // }

        // 排查说文释义中不存在于字头的字
        // checkShuowen("E:\\桌面\\待排查的数据.txt",
        //         "E:\\桌面\\待排查的数据_result.txt");


        // 去重
        removeDuplicate("E:\\桌面\\待排查的数据_result.txt",
                "E:\\桌面\\待排查的数据_result_o1.txt");

    }

    // 统计、字数 6305
    public static int wordNum(String path, int readedNum) {
        List<TextStyle> textStyles = readWord(path);
        List<TextStyle> result = new ArrayList<>();
        String lastWord = "";
        for (int i = 0; i < textStyles.size(); i++) {
            TextStyle e = textStyles.get(i);
            if ("660000".equals(e.getColor()) && e.getText().matches("[、，。]")) {
                result.add(e);
                if (result.size() < readedNum) {
                    lastWord = textStyles.get(i - 3).getText() +
                            textStyles.get(i - 2).getText() +
                            textStyles.get(i - 1).getText();
                }
            }
        }
        System.out.println(lastWord);
        return result.size();
    }

    // 读取WORD文档
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
    // 读取TXT

    public static List<String> readTxt(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    // 写入到TXT文档
    public static void writeToFile(Collection<String> contentDocx, String filePath) {
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

    // 连接数据库
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    // 读取《广雅》文件，并查询说文数据库中存在的数据，输出到txt中
    private static void readAndWriteGY(String sourcePath, String targetPath) {
        List<TextStyle> textStyles = readWord(sourcePath);
        List<String> list = textStyles.stream().filter(e -> "660000".equals(e.getColor()) && !e.getText().matches("[，。、\\{\\}（）\\[\\]〔〕也]"))
                .map(e -> e.getText()).collect(Collectors.toList());
        List<String> resultList = new ArrayList<>();

        List<String> curLineList = new ArrayList<>();
        for (String word : list) {
            if ("\n".equals(word)) {

                resultList.add("\n");
            } else {
                List<String> queryWord = queryWord(word);
                if (!queryWord.isEmpty()) {
                    resultList.addAll(queryWord);
                }
            }
        }
        writeToFile(resultList, targetPath);
    }

    private static List<String> queryWord(String word) {
        String sql = String.format("select * from %s where word regexp ('.*%s.*')", shuowenNameTable, word);
        List<String> resultList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // String result = rs.getString("word") + "：" + rs.getString("definition");
                String result = rs.getString("word");
                resultList.addAll(Arrays.asList(result.split(";")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    // 排查不见于说文字头的字
    private static void checkShuowen(String sourcePath, String targetPath) {
        List<String> stringList = readTxt(sourcePath);
        List<String> unMachedList = new ArrayList<>();
        int i = 0;
        for (String ss : stringList) {
            List<String> tmpList = new ArrayList<>();
            ss.codePoints().forEach(codePoint -> tmpList.add(new String(Character.toChars(codePoint))));
            List<String> curLineResultList = queryWord(String.join("|", tmpList));
            tmpList.removeAll(curLineResultList);
            unMachedList.addAll(tmpList);
            i++;
            System.out.println(i);
        }
        writeToFile(unMachedList, targetPath);
    }

    // 去重
    private static void removeDuplicate(String sourcePath, String targetPath) {
        List<String> stringList = readTxt(sourcePath);
        Set<String> set = new HashSet<>(stringList);
        writeToFile(set, targetPath);
    }

    //
    private static List<String> codePoint(List<String> strs) {
        List<String> list = new ArrayList<>();
        for (String str : strs) {
            str.codePoints().forEach(codePoint -> list.add(new String(Character.toChars(codePoint))));
        }
        return list;
    }

    // 每行每个字之间用制表符隔开
    private static void tabLine(String sourcePath, String targetPath) {
        List<String> stringList = readTxt(sourcePath);
        List<String> transList = new ArrayList<>();
        for (String str : stringList) {
            str.codePoints().forEach(codePoint -> transList.add(new String(Character.toChars(codePoint))));
        }
    }

}
