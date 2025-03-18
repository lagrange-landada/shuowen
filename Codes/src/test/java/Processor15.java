import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Created by zhengyu.shang on 2025/01/27.
 */
public class Processor15 {
    public static void main(String[] args) throws IOException {
        /*Connection connect = Jsoup.connect("https://punct.gj.cool/punct/test");
        // connect.data("src", "周易天一地二馥案本書二從偶一地之數然則一者天之數也。繫辭傳天下之動貞夫一者也虞注一謂乾元萬物之動各資天一陽氣以生故貞夫一。");
        connect.header("Content-Type", "application/json");
        connect.requestBody("[{\"src\":\"【戴侗六書故謂弌不能古於一欲以弌為小篆一為古文案小篆意趨簡易數目字尤所習用故省弌為一其二三皆因一積成之猶古文之積亖為四也戴氏以\uD857\uDCD1為古文緐為小篆豈李斯作字之本意乎】\"}]");
        Connection.Response response = connect.method(Connection.Method.POST).ignoreContentType(true).execute();
        String respText = response.parse().text();
        System.out.println(respText);*/
        Connection connect = null;
        connect = Jsoup.connect("http://www.kaom.net/book_xungu8.php");
        connect.data("pageSize","10")
                .data("biamti", "yes")
                .data("page", "yes");
        Connection.Response response = connect.method(Connection.Method.POST).ignoreContentType(true).execute();
        //获取数据，转换成HTML格式
        Document document = response.parse();
        // System.out.println(document);
        Elements elements = document.select("a[href*=b=xungu_gysz]");
        System.out.println(elements.text());



    }
}
