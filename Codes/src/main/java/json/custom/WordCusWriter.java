package json.custom;

import cn.hutool.poi.word.Word07Writer;
import json.dto.TextStyle;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.awt.*;
import java.math.BigInteger;
import java.util.List;


/***
 * Created by zhengyu.shang on 2021/11/23.
 */
public class WordCusWriter extends Word07Writer {

    public Word07Writer addTextMul(TextStyle... textStyles) {
        if (null != textStyles && textStyles.length != 0) {
            String titleIdentification = textStyles[0].getTitleIdentification();
            int titleLevel = textStyles[0].getTitleLevel();
            if (null != titleIdentification) {
                createTitle(super.getDoc(), titleIdentification, titleLevel);
            }
            final XWPFParagraph p = super.getDoc().createParagraph();
            if (null != titleIdentification) {
                p.setStyle(titleIdentification);
            }
            XWPFRun run;
            for (TextStyle texts : textStyles) {
                run = p.createRun();
                run.setText(texts.getText());
                if (null != texts.getFont()) {
                    Font font = texts.getFont();
                    run.setFontFamily(font.getFamily());
                    run.setFontSize(font.getSize());
                    run.setItalic(font.isItalic());
                    run.setBold(font.isBold());
                }
                if (null != texts.getColor()) {
                    run.setColor(texts.getColor());
                }
            }
        }
        return this;
    }

    public void createTitle(XWPFDocument docxDocument, String titleIdentification, int titleLevel) {
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(titleIdentification);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(titleIdentification);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(titleLevel));

        // lower number > style is more prominent in the formats bar
        ctStyle.setUiPriority(indentNumber);

        CTOnOff onoffnull = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onoffnull);

        // style shows up in the formats bar
        ctStyle.setQFormat(onoffnull);

        // style defines a heading of the given level
        CTPPr ppr = CTPPr.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);

        XWPFStyle style = new XWPFStyle(ctStyle);

        // is a null op if already defined
        XWPFStyles styles = docxDocument.createStyles();

        style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);
    }
}
