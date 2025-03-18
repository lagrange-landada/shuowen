import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/***
 * Created by zhengyu.shang on 2025/01/28.
 */
public class Processor16 {
    public static void main(String[] args) {
        // 读取文件
        List<String> list = readFile("E:\\桌面\\新建文件夹 (2)\\广雅疏证排序.txt");
        // 帅选、排序
        Pattern pattern = Pattern.compile("(\\d+)$");
        // List<String> collect = list.stream().filter(e -> e.matches(".*\\d$"))
        List<String> collect = list.stream().filter(e -> e.matches(".*([1-9]\\d{2}|[1-9]\\d{3,})$"))
                .sorted(((o1, o2) -> {
                    Matcher matcher1 = pattern.matcher(o1);
                    Matcher matcher2 = pattern.matcher(o2);
                    if (matcher1.find() && matcher2.find()) {
                        Integer num1 = Integer.valueOf(matcher1.group());
                        Integer num2 = Integer.valueOf(matcher2.group());
                        return num1 - num2;
                    } else {
                        return 0; // 如果没有匹配到数字，可以按需要返回其他排序方式
                    }
                })).collect(Collectors.toList());
        // 输出
        writeToSqlFile(collect, "E:\\桌面\\新建文件夹 (2)\\广雅疏证排序_result2.txt");
    }

    //
    public static List<String> readFile(String filePath) {
        BufferedReader reader = null;
        List<String> list = new ArrayList<>();
        try {
            // 创建BufferedReader对象，用于读取文件
            reader = new BufferedReader(new FileReader(filePath));
            String line;

            // 按行读取文件内容
            while ((line = reader.readLine()) != null) {
                list.add(line);
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

        return list;
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
}
