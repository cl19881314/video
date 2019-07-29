package download;

import java.io.*;
import java.util.Date;
import java.util.Vector;

public class Utils {
    /**
     * 读入TXT文件
     */
    public static void readFile() {
        String pathname = "D:\\input.txt"; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try {
            FileReader reader = new FileReader(pathname);
            BufferedReader br = new BufferedReader(reader);
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入TXT文件
     */
    public static void writeFile(String url) {
        try {
            File writeName = new File("D:\\failed.txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(url + "\r\n"); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String writeFile(Vector<String> urlList) {
        FileWriter writer = null;
        BufferedWriter out = null;
        String name = String.valueOf(new Date().getMonth() + 1) +String.valueOf(new Date().getDay()) + String.valueOf(new Date().getHours()) + String.valueOf(new Date().getMinutes()) + String.valueOf(new Date().getSeconds());
        String filePath = "D:\\failed" + name +".txt";
        try {
            File writeName = new File(name);
            writeName.createNewFile();
            writer = new FileWriter(writeName);
            out = new BufferedWriter(writer);
            for (String url : urlList) {
                // \r\n即为换行
                out.write(url + "\r\n");
            }
            // 把缓存区内容压入文件
            out.flush();
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("下载失败，写入文件异常");
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (out != null){
                    out.close();
                }
                return filePath;
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return "";
    }

    public static void reNameFile(String oldName, String newName) {
        try {
            File file = new File(oldName);
            file.renameTo(new File(newName));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
