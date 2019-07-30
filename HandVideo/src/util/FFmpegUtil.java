package util;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

public class FFmpegUtil {
    private static CallBackListener mListener;
    private static Timer mTimer;
    private static int mCount;
    private static Thread mWorkThread;
    private static String mVideoPath1;
    private static String mVideoPath2;
    private static boolean mIsFishing;
    private static void videoScale(String videoInputPath, String videoOutPath,String name){
        if (!CheckKeyUtil.CheckKey()){
            return;
        }
        Runtime run = null;
        Process p = null;
        try {
            run = Runtime.getRuntime();
            String tempName = UUID.randomUUID().toString().replace("-","") + ".mp4";
            p = run.exec("./tool/ffmpeg.exe -i "+videoInputPath+" -vf scale=1280:720,setsar=1:1 "+ videoOutPath + "\\" + tempName);
            mVideoPath2 = videoOutPath + tempName;
            //释放进程
            p.getOutputStream().close();
            p.getInputStream().close();
            p.getErrorStream().close();
            p.waitFor();
            if (!mIsFishing) {
                reNameFile(videoOutPath + tempName, videoOutPath + name);
            }
            deleteFile(videoInputPath);
            deleteFile(videoOutPath + tempName);
            mCount = 0;
            mTimer.cancel();
            mListener.updateSuccess(name);
        }catch (Exception e){
            e.printStackTrace();
            mTimer.cancel();
        }finally {
            run.freeMemory();
        }
    }

    private static void videoFilter(String videoInputPath, String videoOutPath, int videoWith,int videoHeight,final String name){
//        ffmpeg -i 11.mp4 -filter_complex "delogo=x=800:y=25:w=250:h=80:show=0" delogo1.mp4
        if (!CheckKeyUtil.CheckKey()){
            return;
        }
        File file = new File(videoOutPath, name);
        if (file.exists()){
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mCount++;
                mListener.updateDown(mCount, name);
            }
        }, 0, 1000);

        Runtime run = null;
        Process p = null;
        try {
            run = Runtime.getRuntime();
            int start = videoWith /4 * 3 -10;
            int with = videoWith /4 ;
            double height = videoHeight / 6.5;
            String tempName = UUID.randomUUID().toString().replace("-","") + ".mp4";
            String command = "./tool/ffmpeg.exe -i "+videoInputPath+" -filter_complex \"delogo=x="+ start +":y=10:w=" + with +":h=" + height + ":show=0\" "+ videoOutPath + tempName;
            System.out.println(command);
            p = run.exec(command);
            mVideoPath1 = videoOutPath + tempName;
            //释放进程
            System.out.println("start");
            p.getOutputStream().close();
            p.getInputStream().close();
            p.getErrorStream().close();
            p.waitFor();
            System.out.println("end");
            if (videoWith == 1280 || videoHeight == 720){
                reNameFile(videoOutPath + tempName,videoOutPath + name);
                deleteFile(videoOutPath + tempName);
                mCount = 0;
                mTimer.cancel();
                mListener.updateSuccess(name);
                System.out.println("跳过修改分辨率");
            } else {
                videoScale(videoOutPath + tempName, videoOutPath, name);
            }
        }catch (Exception e){
            e.printStackTrace();
            mTimer.cancel();
        }finally {
            run.freeMemory();
        }
    }

    public static void setCallBackListener(CallBackListener listener) {
        mListener = listener;
    }

    public static void doMain(final ArrayList<String> fileList,final String saveDirPath) {
        mIsFishing = false;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < fileList.size(); i++) {
                    String pathFile = fileList.get(i);
                    String name = pathFile.substring(pathFile.lastIndexOf("\\") + 1);
                    System.out.println(name);
                    if (!mIsFishing) {
                        videoFilter(pathFile, saveDirPath, videoWidth(pathFile), videoHeight(pathFile),name);
                    }
                }
            }
        };
        mWorkThread = new Thread(runnable);
        mWorkThread.start();
    }

    private static int videoWidth(String video) {
        File source = new File(video);
        Encoder encoder = new Encoder();
        try {
            MultimediaInfo m = encoder.getInfo(source);
//            System.out.println("此视频高度为:"+m.getVideo().getSize().getHeight());
//            System.out.println("此视频宽度为:"+m.getVideo().getSize().getWidth());
            return m.getVideo().getSize().getWidth();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int videoHeight(String video) {
        File source = new File(video);
        Encoder encoder = new Encoder();
        try {
            MultimediaInfo m = encoder.getInfo(source);
            return m.getVideo().getSize().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void reNameFile(String oldName, String newName) {
        try {
            File file = new File(oldName);
            file.renameTo(new File(newName));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void stopRunningProcesses() {
        try {
            String line;
            Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (!line.trim().equals("")) {
                    if (line.contains("ffmpeg.exe")){
                        mIsFishing = true;
                        String[] split = line.split("\"");
                        String cmd = "taskkill /f /pid " + split[3];
                        System.out.println(cmd);
                        Process kill = Runtime.getRuntime().exec(cmd);
                        kill.getInputStream().close();
                        kill.getOutputStream().close();
                        kill.getErrorStream().close();
                        File file = new File(mVideoPath1);
                        if (file.exists()){
                            deleteFile(mVideoPath1);
                        }
                        File file2 = new File(mVideoPath2);
                        if (file2.exists()){
                            deleteFile(mVideoPath2);
                        }
                        break;
                    }
                }
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private static void deleteFile(String path) {
        try {
            File file = new File(path);
            file.delete();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public interface CallBackListener{
        void updateDown(int count,String name);
        void updateSuccess(String name);
    }
}
