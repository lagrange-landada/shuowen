package json;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import json.custom.WordCusWriter;
import json.dto.DuanNotes;
import json.dto.TextStyle;
import json.dto.Words;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhengyu.shang on 2021/11/22.
 */
public class Processor {
    public static void main(String[] args) throws IOException {
        //顺序 读取文件，然后组成list，并将list输出到word文档中
        File fileDirectory = new File("E:/A书籍/文字通解/shuowen-master/test");
        String exportPath = "E:/A书籍/文字通解/test.docx";
        File[] files = fileDirectory.listFiles();
        List<File> list = Arrays.stream(files).sorted((o1, o2) -> {
            String o1Name = o1.getName().substring(0, o1.getName().length() - 5);
            String o2Name = o2.getName().substring(0, o2.getName().length() - 5);
            return Integer.parseInt(o1Name) - Integer.parseInt(o2Name);
        }).collect(Collectors.toList());
        ArrayList<Words> wordsList = new ArrayList<>();
        for (File file : list) {
            String content = FileUtils.readFileToString(file, "UTF-8");
            Words words = JSON.parseObject(content, Words.class);
            wordsList.add(words);
        }
        System.out.println(wordsList.size());
        exportMSWord(wordsList, exportPath);
    }

    private static void exportMSWord(ArrayList<Words> wordsList, String exportPath) {
        WordCusWriter writer = new WordCusWriter();
        String radical = "";
        //预置字体
        Font bushouFont = new Font("宋体-方正超大字符集", Font.PLAIN, 18);//部头字体
        Font headFont = new Font("宋体-方正超大字符集", Font.BOLD, 18);//字头字体
        Font xiaozhuanFont = new Font("方正字迹-顧建平篆書 繁U", Font.PLAIN, 28);//小篆字体
        Font pinyinFont = new Font("News Gothic MT", Font.PLAIN, 12);//拼音字体
        Font xuFont = new Font("宋体-方正超大字符集", Font.BOLD, 12);//许书释文字体
        Font textFont = new Font("宋体-方正超大字符集", Font.PLAIN, 12);//段注字体

        for (Words words : wordsList) {
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
        }
        // 写出到文件
        writer.flush(FileUtil.file(exportPath));
        // 关闭
        writer.close();
    }

}
