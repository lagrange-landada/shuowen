package json;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.custom.WordCusWriter;
import json.dto.FangYanDO;
import json.dto.ShiMingDO;
import json.dto.TextStyle;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by zhengyu.shang on 2024/12/25. 用来从《方言》txt文本导出到Word文档中
 */
public class Processor11 {
    public static void main(String[] args) throws IOException {
        //顺序 读取文件，然后组成list，并将list输出到word文档中
        File fileDirectory = new File("E:/桌面/新建文件夹 (2)/方言.txt");
        String exportPath = "E:/桌面/新建文件夹 (2)/test.docx";

        // ArrayList<ShiMingDO> wordsList = new ArrayList<>();

        String content = FileUtils.readFileToString(fileDirectory, "UTF-8");


        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<FangYanDO> list = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, FangYanDO.class));

        exportMSWord(list, exportPath);
    }


    private static void exportMSWord(ArrayList<FangYanDO> wordsList, String exportPath) {
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


        for (FangYanDO fangYanDO : wordsList) {
            //ArrayList<TextStyle> textStyles = new ArrayList<>();
            //判断要不要添加部首标题
            if (!fangYanDO.getJuanmu().equals(juanmu)) {
                TextStyle bushou = new TextStyle(headFont, fangYanDO.getJuanmu(), "FF0000", "标题 1", 1);
                writer.addTextMul(bushou);//部头
            }
            // 判断要不要添加词条
            if (!citiao.equals(fangYanDO.getCitiao())) {
                TextStyle textStyle = new TextStyle(xuFont, fangYanDO.getCitiao() + "。", "660000");
                writer.addTextMul(textStyle);
                if (!fangYanDO.getGuzhu().isEmpty()) {
                    TextStyle zhengwen = new TextStyle(textFont, fangYanDO.getGuzhu() + "。", "000000");
                    writer.addTextMul(zhengwen);
                }



            }

            juanmu = fangYanDO.getJuanmu();
            citiao = fangYanDO.getCitiao();




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
