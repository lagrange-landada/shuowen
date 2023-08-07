import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.word.Word07Writer;
import json.custom.WordCusWriter;
import json.dto.TextStyle;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

/***
 * Created by zhengyu.shang on 2021/11/23.
 */
public class MyTest {
    @Test
    public void sdf() {
        Word07Writer writer = new Word07Writer();

        // 添加段落（标题）
        writer.addText(new Font("方正小标宋简体", Font.PLAIN, 22), "我是第一部分", "我是第二部分");
        // 添加段落（正文）
        writer.addText(new Font("宋体", Font.PLAIN, 22), "我是正文第一部分", "我是正文第二部分");

        // 写出到文件
        writer.flush(FileUtil.file("E:/A书籍/文字通解/test.docx"));
        // 关闭
        writer.close();
    }

    @Test
    public void sadfkl() {
        WordCusWriter writer = new WordCusWriter();
        ArrayList<TextStyle> textStyles = new ArrayList<>();

        TextStyle textStyle1 = new TextStyle();
        textStyle1.setText("我是个大傻逼");
        textStyle1.setFont(new Font("方正小标宋简体", Font.PLAIN, 22));
        textStyles.add(textStyle1);
        TextStyle textStyle2 = new TextStyle();
        textStyle2.setText("我爱中国");
        textStyle2.setFont(new Font("方正小标宋简体", Font.PLAIN, 10));
        textStyles.add(textStyle2);

        //writer.addTextMul(null, textStyles);
        // 写出到文件
        writer.flush(FileUtil.file("E:/A书籍/文字通解/test.docx"));
        // 关闭
        writer.close();
    }

    @Test
    public void test() throws Exception {
        int level = 1;
        String styleName = "标题 1";
        String name = "标题内容";
        String filePath = "E:/A书籍/文字通解/test.docx";
        // 获得word的pack对象
        OPCPackage pack = POIXMLDocument.openPackage( filePath );
        // 获得XWPFDocument对象
        XWPFDocument doc = new XWPFDocument( pack );
        addCustomHeadingStyle( doc, styleName, level );
        XWPFParagraph paragraph = doc.getParagraphs().get( 0 );
        // 段落的格式,下面及个设置,将使新添加的文字向左对其,无缩进.
        paragraph.setIndentationLeft( 0 );
        paragraph.setIndentationHanging( 0 );
        paragraph.setAlignment( ParagraphAlignment.LEFT );
        // paragraph.setWordWrap( true );
        paragraph.setStyle( styleName );
        // 在段落中新插入一个run,这里的run我理解就是一个word文档需要显示的个体,里面可以放文字,参数0代表在段落的最前面插入
        XWPFRun run = paragraph.insertNewRun( 0 );
        // 设置run内容
        run.setText( "中国" );
        run.setFontFamily( "宋体" );
        run.setBold( true );
        run.setFontSize( 20 );
        run.addBreak( BreakType.TEXT_WRAPPING );
        // 生成的标题文件
        File newFile = new File( "E:/A书籍/文字通解/testXXXXX.docx" );
        FileOutputStream fos = new FileOutputStream( newFile );
        doc.write( fos );
        fos.flush();
        fos.close();
        pack.close();
        newFile.delete();
    }

    /**
     * 设置标题样式
     * @param docxDocument 文档对象
     * @param strStyleId “标题 1”
     * @param headingLevel 1
     */
    public void addCustomHeadingStyle( XWPFDocument docxDocument, String strStyleId, int headingLevel ) {

        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId( strStyleId );

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal( strStyleId );
        ctStyle.setName( styleName );

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal( BigInteger.valueOf( headingLevel ) );

        // lower number > style is more prominent in the formats bar
        ctStyle.setUiPriority( indentNumber );

        CTOnOff onoffnull = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed( onoffnull );

        // style shows up in the formats bar
        ctStyle.setQFormat( onoffnull );

        // style defines a heading of the given level
        CTPPr ppr = CTPPr.Factory.newInstance();
        ppr.setOutlineLvl( indentNumber );
        ctStyle.setPPr( ppr );

        XWPFStyle style = new XWPFStyle( ctStyle );

        // is a null op if already defined
        XWPFStyles styles = docxDocument.createStyles();

        style.setType( STStyleType.PARAGRAPH );
        styles.addStyle( style );

    }

}
