package util;

import java.io.*;
import java.util.Properties;

public class ConfigValue {
    protected static Properties props= new Properties();
    /***
     * 配置文件
     */
    private static File configFile;
    public static final String configFilePath = System.getProperty("user.home") + File.separator + ".chen.properties";
    /***
     * 读取配置文件
     * @throws IOException
     */
    public static String readConfig(String key){
        try {
            configFile = new File(configFilePath);
            if (configFile.exists()) {
                InputStream inStream = new FileInputStream(configFile);
                props.load(inStream);
                inStream.close();//及时关闭资源
            }
            return props.getProperty(key);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /***
     * 保存到配置文件中
     * @throws IOException
     */
    public static void saveConfig(String key, String value){
        try {
            OutputStream out = new FileOutputStream(configFile);
            props.setProperty(key, value);
            props.store(out, "chen value");
            out.close();//及时关闭资源
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
