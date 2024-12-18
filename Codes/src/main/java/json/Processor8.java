package json;

import com.sun.org.slf4j.internal.LoggerFactory;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/***
 * Created by zhengyu.shang on 2024/11/19.
 */

public class Processor8 {
    public static void main(String[] args) {
        Map<String, String> contentDocx = getContentDocx("E:\\桌面\\新建文件夹 (2)\\测试.docx");
        System.out.println(contentDocx.get("content"));
    }


    /**
     * 获取正文文件内容，docx方法
     *
     * @param path
     * @return
     */
    public static Map<String, String> getContentDocx(String path) {
        Map<String, String> map = new HashMap();
        StringBuffer content = new StringBuffer("");
        String result = "0";  // 0表示获取正常，1表示获取异常
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
            // 2007版本的word
            XWPFDocument xwpf = new XWPFDocument(is);    // 2007版本，仅支持docx文件处理
            List<XWPFParagraph> paragraphs = xwpf.getParagraphs();
            if (paragraphs != null && paragraphs.size() > 0) {
                for (XWPFParagraph paragraph : paragraphs) {
                    if (!paragraph.getParagraphText().startsWith("    ")) {
                        for (XWPFRun run : paragraph.getRuns()) {
                            run.getFontFamily();//获取当前运行中的字体
                            run.getColor();//获取文本颜色
                            run.getTextHightlightColor();//高亮显示
                        }
                        content.append("    ").append(paragraph.getParagraphText().trim()).append("\r\n");
                    } else {
                        content.append(paragraph.getParagraphText());
                    }
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
            map.put("result", result);
            map.put("content", content.toString());
        }
        return map;
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
