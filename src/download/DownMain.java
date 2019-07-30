package download;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownMain {
    private static ExecutorService fixedThreadPool;
    private static Vector<String> failedUrlList;
    private static ArrayList<String> originalUrlList;
    private static String mSavePath;
    private static Boolean mQuanxian; //安全验证
    public static void main(String pathname,String savePath,Boolean quanxian){
        mQuanxian = quanxian;
        mSavePath = savePath;
        fixedThreadPool = Executors.newFixedThreadPool(5);
        failedUrlList = new Vector<>();
        originalUrlList = new ArrayList<>();
        FileReader reader = null;
        BufferedReader br = null;
        try {
            reader = new FileReader(pathname);
            br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("https") && !originalUrlList.contains(line)){
                    addFixedThreadPool(line);
                    //过滤点相同的下载链接
                    originalUrlList.add(line);
                }
            }
            mListener.videoOriginalListener(originalUrlList.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (br != null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        endDownVideo();
    }

    public static void main(Vector<String> urlList){
        fixedThreadPool = Executors.newFixedThreadPool(5);
        failedUrlList.clear();
        originalUrlList.clear();
        for (String url : urlList){
            addFixedThreadPool(url);
            //过滤点相同的下载链接
            originalUrlList.add(url);
        }
        mListener.videoOriginalListener(originalUrlList.size());
        endDownVideo();
    }

    private static void endDownVideo(){
        if (!mQuanxian){
            return;
        }
        fixedThreadPool.shutdown();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!fixedThreadPool.isTerminated()) {
                    try{
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
                if (failedUrlList.size() > 0){
                    //String name = Utils.writeFile(failedUrlList);
//                    try{
//                        Thread.sleep(1 * 1000);
//                    } catch (InterruptedException e) {
//                        //e.printStackTrace();
//                    }
                    mListener.videoFailedListener(failedUrlList);
                    System.out.println("--------------------下载失败："+ failedUrlList.size() + "-----------------------");
                } else {
                    mListener.videoSuccessListener(originalUrlList.size());
                }
                System.out.println("下载任务结束了。。。。");
            }
        }).start();
    }

    public static void analysisUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                    .timeout(999999999)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                    .header("Connection", "keep-alive")
                    .header("Host", "www.baidu.com")
                    .header("Referer", "http://api.xfsub.com/")
                    //是忽略请求类型
                    .ignoreContentType(true)
                    .get();
            String title = doc.getElementsByTag("title").toString().replace("/","").replace("<title>","").replace("_好看视频","");
            Elements elements = doc.getElementsByTag("script");

            for (Element element : elements) {
                if (element.data().contains("videoSrc")){
                    String[] data = element.data().split("function");
                    for (String variable : data) {
                        if (variable.contains("videoSrc")){
                            String video = variable.substring(variable.indexOf("videoSrc") + 11,variable.indexOf("poster") - 2).replace("\\","");
                            String imgUrl = variable.substring(variable.indexOf("poster") + 9,variable.indexOf("emitlist") - 2).replace("\\","");
                            File videoFile = new File(mSavePath+ title + ".mp4");
                            if (videoFile.exists()){
                                break;
                            }
                            if (!mQuanxian){
                                break;
                            }
                            DownLoadUtil.httpDownload(imgUrl,mSavePath+ title + ".jpeg");
                            String videoName = mSavePath + UUID.randomUUID().toString();
                            System.out.println("开始下载 :" + title);

                            mListener.videoUpdateNameListener(title);
                            boolean isSuccess = DownLoadUtil.httpDownload(video, title, videoName, mListener);
                            if (!isSuccess){
//                                addFixedThreadPool(url);
                              throw new Exception();
                            } else  {
                                System.out.println("下载成功:" + title);
                                Utils.reNameFile(videoName, mSavePath+ title + ".mp4");
                                mListener.videoProgressListener(title,100);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("下载失败:" + url);
            failedUrlList.add(url);
        }
    }
    public static void addFixedThreadPool(final String url) {
        if (!mQuanxian){
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                analysisUrl(url);
            }
        };
        fixedThreadPool.execute(runnable);
    }

    private static VideoDownLister mListener;
    public static void addVideoNameListener(VideoDownLister lister) {
        mListener = lister;
    }

    public interface VideoDownLister{
        void videoUpdateNameListener(String videoName);
        void videoProgressListener(String title, double downCount);
        void videoFailedListener(Vector<String> failedUrl);
        void videoSuccessListener(int count);
        void videoOriginalListener(int count);
    }
}
