package download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadUtil {

    public static boolean httpDownload(String httpUrl, String saveFile) {
        return httpDownload(httpUrl, "", saveFile, null);
    }

    public static boolean httpDownload(String httpUrl, String title, String saveFile, DownMain.VideoDownLister listener) {
        int byteRead;
        int downCout = 0;
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            URL url = new URL(httpUrl);
            //2.获取链接
            URLConnection conn = url.openConnection();
            //3.输入流
            inStream = conn.getInputStream();
            //3.写入文件
            fs = new FileOutputStream(saveFile);
            int totalLenth = conn.getContentLength();
            byte[] buffer = new byte[1024];
            double percent = 0D;
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
                downCout += byteRead;
                double newPercent = downCout * 1.0 / totalLenth * 10000 / 100;
                if (newPercent - percent >= 5) {
                    System.out.printf("%s 进度:%.2f%%\n", title, newPercent);
                    percent = newPercent;
                    if (listener != null){
                        listener.videoProgressListener(title, newPercent);
                    }
                }
            }
            inStream.close();
            fs.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }
}
