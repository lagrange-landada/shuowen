import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

/***
 * Created by zhengyu.shang on 2025/01/27.
 */
public class Processor14 {
    public static void main(String[] args) throws IOException {
        Connection connect = null;

        Document document = readFile("E:\\桌面\\新建文件夹 (2)\\yizheng.html");

        Element script = document.select("script").first();
        String scriptContent = script.html();
        String jsonData = scriptContent.substring(scriptContent.indexOf("window.books =") + "window.books =".length()).trim();
        jsonData = jsonData.substring(0, jsonData.length() - 1);  // 去掉最后的分号
        Gson gson = new Gson();
        // String json = convertToJsonFormat(jsonData).substring(1, convertToJsonFormat(jsonData).length() - 2) + "\"}]";
        List<Map<String, Object>> list1 = gson.fromJson(convertToJsonFormat(jsonData).substring(1, convertToJsonFormat(jsonData).length() - 1), List.class);

        // 先测试几个样本
        list1 = list1.subList(0, 2);
        List<Map<String, Object>> resultList = new ArrayList<>();
        connect = Jsoup.connect("https://punct.gj.cool/punct/test");
        connect.header("Content-Type", "application/json");
        // connect.data("mission", "biaodian");
        for (Map<String, Object> zitiao : list1) {
            // 以500字符为界限，进行分割。
            String definition = (String) zitiao.get("e");

            List<String> eList = new ArrayList<>();
            Connection.Response response = null;
            String respText = "";
            for (String innerStr : definition.split("<br>")) {
                // 如果长度小于500，则不做处理
                if (innerStr.length() <= 500) {
                    // connect.data("src", innerStr);
                    int status = -1;
                    int reTryNum = 0;
                    do {
                        try {
                            connect.requestBody("[{\"src\":\"" + innerStr + "\"}]");
                            response = connect.method(Connection.Method.POST).ignoreContentType(true).execute();
                            status = response.statusCode();
                            respText = response.parse().text();
                            eList.add(respText);
                        } catch (Exception e) {
                            System.out.println("出错的字符：" + innerStr);
                            System.out.println("重试" + (++reTryNum) + "次数");
                            try {
                                Thread.sleep(((int)(Math.random() * 4)) * 1234);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    } while (status != 200);

                } else {
                    // 找出500之内最后一个句号。
                    int start = 0;
                    int end = innerStr.length();
                    StringBuffer stringBuffer = new StringBuffer();
                    while (start < end) {
                        int subEnd = Math.min(start + 500, end);
                        String substring = innerStr.substring(start, subEnd);
                        int lastIndexOf = substring.lastIndexOf("。");
                        String realSubString = substring.substring(0,
                                lastIndexOf ==  -1 ? substring.length() : lastIndexOf + 1);
                        // connect.data("text", realSubString);
                        int status = -1;
                        int reTryNum = 0;
                        do {
                            try {
                                connect.requestBody("[{\"src\":\"" + realSubString + "\"}]");
                                response = connect.method(Connection.Method.POST).ignoreContentType(true).execute();
                                respText = response.parse().text();
                                status = response.statusCode();

                            } catch (Exception e) {
                                System.out.println("出错的字符：" + realSubString);
                                System.out.println("重试" + (++reTryNum) + "次数");
                                try {
                                    Thread.sleep(((int)(Math.random() * 3)) * 1234);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        } while (status != 200);


                        stringBuffer.append(respText);
                        start = subEnd;
                    }
                    eList.add(stringBuffer.toString());
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("a", zitiao.get("a"));
            map.put("b", zitiao.get("b"));
            map.put("c", zitiao.get("c"));
            map.put("d", zitiao.get("d"));
            map.put("e", String.join("<br>", eList));
            resultList.add(map);
        }
        writeToSqlFile(resultList, "E:\\桌面\\新建文件夹 (2)\\json-handled.txt");
    }

    public static String convertToJsonFormat(String input) {
        // 将单引号替换为双引号，适用于字符串值
        String formatted = input.replaceAll("'", "\"");

        // 将"key: value"格式转换成JSON格式的"\"key\": value"
        formatted = formatted.replaceAll("(\\w+):", "\"$1\":");

        // 确保末尾没有多余的逗号
        if (formatted.endsWith(",")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        // 添加花括号，使其成为标准的JSON对象
        formatted = "{" + formatted + "}";

        return formatted;
    }

    public static void writeToSqlFile(List<Map<String, Object>> contentDocx, String filePath) {
        BufferedWriter writer = null;
        try {
            // 创建一个BufferedWriter对象，目标文件是sql.sql
            writer = new BufferedWriter(new FileWriter(new File(filePath)));

            // 遍历 List 中的每一项，逐行写入到 sql.sql 文件
            for (Map<String, Object> map: contentDocx) {
                Gson gson = new Gson();
                writer.write(gson.toJson(map));
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


    public static Document readFile(String filePath) {
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            // 创建BufferedReader对象，用于读取文件
            reader = new BufferedReader(new FileReader(filePath));
            String line;

            // 按行读取文件内容
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            // 捕获IOException并打印错误信息
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件流
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Jsoup.parse(stringBuffer.toString());
    }
}
